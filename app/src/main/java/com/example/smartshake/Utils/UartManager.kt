package com.example.smartshake.Utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.dwin.serialportlibrary.SerialInter
import com.dwin.serialportlibrary.SerialManage
import com.example.smartshake.uart.MachineState
import com.example.smartshake.uart.ProtocolParser

/**
 * Singleton that manages the UART serial connection to the ESP32 machine controller.
 *
 * Usage:
 *   UartManager.init(context)         // once, e.g. in MainActivity.onCreate
 *   UartManager.connect()             // open the serial port
 *   UartManager.sendDispenseCommand(flavourId, baseId)
 *   UartManager.disconnect()          // close when done
 */
object UartManager : SerialInter {

    private const val TAG = "UartManager"

    // --- Configuration ---
    const val DEFAULT_PORT      = "/dev/ttyS0"
    const val DEFAULT_BAUD_RATE = 115200

    // --- State ---
    var isConnected: Boolean = false
        private set

    val machineState = MachineState()
    private val protocolParser = ProtocolParser(machineState)

    private lateinit var serialManage: SerialManage
    private var currentPort: String = DEFAULT_PORT

    // Packet assembly buffer (serial data can arrive in fragments)
    private val packetBuffer = StringBuilder()
    private val mainHandler   = Handler(Looper.getMainLooper())

    // Optional callback so UI can react to connection events
    var onConnectionChanged: ((connected: Boolean, port: String) -> Unit)? = null
    var onDataParsed: (() -> Unit)? = null

    // Watchdog
    private var lastRxTime: Long = 0L
    private var pollingRunnable: Runnable? = null

    // -------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------

    fun init() {
        serialManage = SerialManage.getInstance()
        serialManage.init(this)
        Log.d(TAG, "UartManager initialised")
    }

    fun connect(port: String = DEFAULT_PORT, baudRate: Int = DEFAULT_BAUD_RATE) {
        currentPort = port
        Log.d(TAG, "Opening port $port at $baudRate baud")
        serialManage.open(port, true, baudRate, 8, 1, 0, 100)
    }

    fun disconnect() {
        stopHeartbeat()
        serialManage.close()
        isConnected = false
        Log.d(TAG, "Port closed")
    }

    private fun startHeartbeat() {
        stopHeartbeat()
        lastRxTime = System.currentTimeMillis()
        pollingRunnable = object : Runnable {
            override fun run() {
                val now = System.currentTimeMillis()
                // If 15 seconds have passed without Rx, mark as disconnected
                if (now - lastRxTime > 15000 && isConnected) {
                    isConnected = false
                    Log.w(TAG, "Heartbeat lost! ESP32 unplugged or frozen.")
                    mainHandler.post { onConnectionChanged?.invoke(false, currentPort) }
                }

                // Send a polling heartbeat (also gets current stock)
                //sendStockRequest()
                mainHandler.postDelayed(this, 5000)
            }
        }
        mainHandler.postDelayed(pollingRunnable!!, 300) // start slightly after connect
    }

    private fun stopHeartbeat() {
        pollingRunnable?.let { mainHandler.removeCallbacks(it) }
        pollingRunnable = null
    }

    fun release() {
        disconnect()
        serialManage.release()
    }

    // -------------------------------------------------------
    // Commands
    // -------------------------------------------------------

    /**
     * Send a dispense command to the ESP32.
     * @param flavourId  Flavour slot number (1–5)
     * @param baseId     0 = Water, 1 = Milk
     */
    fun sendDispenseCommand(flavourId: Int, baseId: Int) {
        sendRaw("CMD|F=$flavourId|B=$baseId")
    }

    fun sendStockRequest() {
        sendRaw("CMD|GET=STOCK")
    }

    private fun sendRaw(cmd: String) {
        if (!isConnected) {
            Log.w(TAG, "Cannot send — port not open: $cmd")
            return
        }
        val finalCmd = if (cmd.endsWith("\n")) cmd else "$cmd\n"
        Log.i(TAG, "================ ESP32 TX ================")
        Log.i(TAG, "Sending to ESP32: ${finalCmd.trim()}")
        Log.i(TAG, "==========================================")
        serialManage.send(finalCmd.toByteArray())
    }

    // -------------------------------------------------------
    // SerialInter callbacks
    // -------------------------------------------------------

    override fun connectMsg(path: String?, isSuccess: Boolean) {
        isConnected = isSuccess
        if (isSuccess && path != null) {
            currentPort = path
            startHeartbeat()
        } else {
            stopHeartbeat()
        }
        
        val connectedPort = path ?: currentPort
        Log.d(TAG, "connectMsg: $connectedPort success=$isSuccess")
        mainHandler.post { onConnectionChanged?.invoke(isSuccess, connectedPort) }
    }

    override fun readData(path: String?, bytes: ByteArray?, size: Int) {
        if (bytes == null || size <= 0) return
        
        // Feed the watchdog
        lastRxTime = System.currentTimeMillis()
        if (!isConnected) {
            // Auto-recover if we were disconnected via timeout but suddenly Rx data
            isConnected = true
            Log.i(TAG, "Heartbeat restored! ESP32 is back online.")
            mainHandler.post { onConnectionChanged?.invoke(true, currentPort) }
        }

        val chunk = String(bytes, 0, size)
        Log.i(TAG, "RAW RX CHUNK [${size} bytes]: ${chunk.trim()}")
        packetBuffer.append(chunk)

        // Parse line-by-line
        var idx: Int
        while (packetBuffer.indexOf("\n").also { idx = it } != -1) {
            val line = packetBuffer.substring(0, idx).trim()
            packetBuffer.delete(0, idx + 1)
            if (line.isNotEmpty()) {
                Log.i(TAG, "================ ESP32 RX ================")
                Log.i(TAG, "Received from ESP32: $line")
                Log.i(TAG, "==========================================")
                mainHandler.post {
                    protocolParser.parse(line)
                    onDataParsed?.invoke()
                }
            }
        }

        // Handle buffers stuck without newline (fragmented packets)
        if (packetBuffer.length > 64 && packetBuffer.contains("|")) {
            val fragment = packetBuffer.toString().trim()
            if (fragment.startsWith("DATA|") || fragment.startsWith("ACK|")) {
                mainHandler.post {
                    protocolParser.parse(fragment)
                    onDataParsed?.invoke()
                }
                packetBuffer.clear()
            }
        }
    }
}
