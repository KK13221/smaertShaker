package com.example.smartshake

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smartshake.Utils.Utils

class DoneActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.hideSystemBars(this)
        setContentView(R.layout.activity_done)

        // Navigate back to UniqueId (or MainActivity) after 5 seconds to restart the flow
        handler.postDelayed({
            val intent = Intent(this, UniqueId::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
