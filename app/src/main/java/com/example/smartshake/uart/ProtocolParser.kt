package com.example.smartshake.uart

import android.util.Log

/**
 * Parses raw UART lines from the ESP32 into [MachineState].
 *
 * Supported message formats:
 *   DATA|F1=1|F2=0|MILK=1|WATER=0|...
 *   ACK|OK
 *   ERR|CODE=...|MSG=...
 *   STATUS|CLEANING
 *   LOG|...
 */
class ProtocolParser(private val machineState: MachineState) {

    companion object {
        private const val TAG = "ProtocolParser"
    }

    fun parse(line: String) {
        if (!line.contains("|")) return
        val parts = line.trim().split("|")
        if (parts.isEmpty()) return

        when (parts[0]) {
            "DATA"   -> parseData(parts)
            "ACK"    -> if (parts.getOrNull(1) == "OK") Log.d(TAG, "Command acknowledged: OK")
            "ERR"    -> parseError(parts)
            "STATUS" -> parts.getOrNull(1)?.let { machineState.currentStatus = it }
            "LOG"    -> Log.d(TAG, "ESP32 Log: $line")
            else     -> Log.w(TAG, "Unknown prefix: ${parts[0]}")
        }
    }

    private fun parseData(parts: List<String>) {
        for (i in 1 until parts.size) {
            val kv = parts[i]
            if (!kv.contains("=")) continue
            val (key, value) = kv.split("=", limit = 2).map { it.trim().uppercase() }
            if (key.startsWith("F") || key == "MILK" || key == "WATER") {
                machineState.setStock(key, value.toIntOrNull() ?: 0)
            } else {
                machineState.setSensor(key, value)
            }
        }
    }

    private fun parseError(parts: List<String>) {
        val msg = parts.drop(1).joinToString(" ")
        machineState.lastError = msg
        Log.e(TAG, "Machine Error: $msg")
    }
}
