package com.example.smartshake

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartshake.Utils.Utils
import com.example.smartshake.data.model.FlavourItem

class DoneActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.hideSystemBars(this)
        setContentView(R.layout.activity_done)

        // Receive data and update stats
        @Suppress("DEPRECATION")
        val selectedFlavours = intent.getSerializableExtra("selected_flavours") as? ArrayList<FlavourItem>
        
        var totalProtein = 0
        selectedFlavours?.forEach {
            totalProtein += (it.Protein?.toIntOrNull() ?: 0) * it.scoops
        }
        
        if (totalProtein > 0) {
            findViewById<TextView>(R.id.tvProteinValue).text = "${totalProtein}g"
        }

        // Button Listeners
        findViewById<Button>(R.id.btnOpenCompartment).setOnClickListener {
            Toast.makeText(this, "Opening Compartment...", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnPrintReceipt).setOnClickListener {
            Toast.makeText(this, "Printing Receipt...", Toast.LENGTH_SHORT).show()
        }

        // Restart Flow — navigate back after 10 seconds (increased from 5s for better viewing)
        handler.postDelayed({
            val intent = Intent(this, UniqueId::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 10000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
