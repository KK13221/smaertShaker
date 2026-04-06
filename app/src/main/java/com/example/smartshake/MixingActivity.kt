package com.example.smartshake

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smartshake.Utils.Utils

class MixingActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.hideSystemBars(this)
        setContentView(R.layout.activity_mixing)

        // Automatically navigate to DoneActivity after 6.5 seconds
        handler.postDelayed({
            val intent = Intent(this, DoneActivity::class.java)
            startActivity(intent)
            finish()
        }, 6500)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
