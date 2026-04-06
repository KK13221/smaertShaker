package com.example.smartshake

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smartshake.Utils.Utils

class DispensingActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.hideSystemBars(this)
        setContentView(R.layout.activity_dispensing)

        // Wait for the 6.5s animation on GlowCupView to finish, plus a slight buffer
        handler.postDelayed({
            val intent = Intent(this, MixingActivity::class.java)
            startActivity(intent)
            finish()
        }, 7000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
