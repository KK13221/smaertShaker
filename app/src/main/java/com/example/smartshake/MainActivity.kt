package com.example.smartshake

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartshake.Utils.UartManager
import com.example.smartshake.Utils.Utils

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable immersive mode
        Utils.hideSystemBars(this)

        setContentView(R.layout.activity_main)

        val startShake = findViewById<FrameLayout>(R.id.layoutStart)
        val viewConnectionStatus = findViewById<android.view.View>(R.id.viewConnectionStatus)

        // Disable button initially until connection is confirmed
        startShake.isEnabled = true
        startShake.alpha = 0.5f

        startShake.setOnClickListener {
            startActivity(Intent(this, Flavour::class.java))
        }

        // Hidden Admin Panel trigger logic (3 clicks on tvSubtitle)
        val tvSubtitle = findViewById<android.widget.TextView>(R.id.tvSubtitle)
        var clickCount = 0
        tvSubtitle.setOnClickListener {
            clickCount++
            if (clickCount >= 3) {
                clickCount = 0
                startActivity(Intent(this, AdminWebViewActivity::class.java))
            }
        }

        // Initialise UART and open serial port to ESP32
        initUart(startShake, viewConnectionStatus)
    }

    private fun initUart(startShake: FrameLayout, viewConnectionStatus: android.view.View) {
        UartManager.init()

        // Helper to update UI
        val updateUI = { connected: Boolean ->
            val dataReady = UartManager.isDataReady
            Log.d(TAG, "UART UI Update: connected=$connected, dataReady=$dataReady")

            // Only enable the button if data is actively flowing from the machine
            startShake.isEnabled = connected && dataReady
            startShake.alpha = if (connected && dataReady) 1.0f else 0.5f

            val statusColor = when {
                !connected -> "#F44336" // Red
                connected && !dataReady -> "#FF9800" // Orange
                connected && dataReady -> "#4CAF50" // Green
                else -> "#F44336"
            }
            
            viewConnectionStatus.backgroundTintList = 
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(statusColor))
        }

        UartManager.onConnectionChanged = { connected, _ ->
            updateUI(connected)
        }

        // Initial sync: set UI based on current state immediately
        updateUI(UartManager.isConnected)

        UartManager.connect()
    }


    override fun onDestroy() {
        super.onDestroy()
        // Removed UartManager.release() to ensure stable connection 
        // throughout the application's lifecycle, especially during flow restarts.
    }
}