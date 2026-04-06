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
        val tvConnectionStatus = findViewById<android.widget.TextView>(R.id.tvConnectionStatus)

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
        initUart(startShake, tvConnectionStatus)
    }

    private fun initUart(startShake: FrameLayout, tvConnectionStatus: android.widget.TextView) {
        UartManager.init()
        UartManager.onConnectionChanged = { connected, port ->
            Log.d(TAG, "UART connection: $connected on $port")

            // Update UI State
            startShake.isEnabled = connected
            startShake.alpha = if (connected) 1.0f else 0.5f

            if (connected) {
                tvConnectionStatus.text = "Machine Status: Connected"
                tvConnectionStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50")) // Green
                UartManager.sendStockRequest()
            } else {
                tvConnectionStatus.text = "Machine Status: Disconnected"
                tvConnectionStatus.setTextColor(android.graphics.Color.parseColor("#F44336")) // Red
                Toast.makeText(this, "Device Unhealthy - ESP32 Disconnected", Toast.LENGTH_LONG).show()
            }
        }
        UartManager.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        UartManager.release()
    }
}