package com.example.smartshake.uart

/**
 * Central state holder for the Smart Shaker machine.
 * Tracks stock availability (flavours + base) and sensor readings received over UART.
 */
class MachineState {

    // Flavour/base availability: 1 = Available, 0 = Empty
    private val stockMap = mutableMapOf(
        "F1" to 0, "F2" to 0, "F3" to 0, "F4" to 0, "F5" to 0,
        "MILK" to 0, "WATER" to 0
    )

    // Live sensor values (IR, FLOW, LC, LS etc.)
    private val sensorMap = mutableMapOf<String, String>()

    var currentStatus: String = "IDLE"
    var lastError: String = ""

    fun setStock(key: String, value: Int) { stockMap[key] = value }
    fun getStock(key: String): Int = stockMap[key] ?: 0
    fun getStockMap(): Map<String, Int> = stockMap

    fun setSensor(key: String, value: String) { sensorMap[key] = value }
    fun getSensor(key: String): String = sensorMap[key] ?: "0"
}
