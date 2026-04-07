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

        val progressBar = findViewById<android.widget.ProgressBar>(R.id.pbDispensingProgress)
        val tvPercentage = findViewById<android.widget.TextView>(R.id.tvProgressPercentage)
        val tvSeq03 = findViewById<android.widget.TextView>(R.id.tvSeq03)
        val glowCupView = findViewById<com.example.smartshake.ui.views.GlowCupView>(R.id.glowCupView)

        // Animate progress from 0 to 100 over 7 seconds
        android.animation.ValueAnimator.ofInt(0, 100).apply {
            duration = 7000
            interpolator = android.view.animation.LinearInterpolator()
            addUpdateListener { animator ->
                val progress = animator.animatedValue as Int
                
                // Update text and horizontal bar (0-100)
                progressBar.progress = progress
                tvPercentage.text = "$progress%"
                tvSeq03.text = "[SEQ_03] ELECTROLYTE_SYNC... $progress%"
                
                // Update liquid cup level (0.0 - 1.0)
                glowCupView.setProgress(progress / 100f)
            }
            start()
        }

        // Receive selected flavours to pass it logic to MixingActivity
        val selectedFlavours = intent.getSerializableExtra("selected_flavours")

        // Wait for the 7s animation to finish
        handler.postDelayed({
            val intent = Intent(this, MixingActivity::class.java)
            intent.putExtra("selected_flavours", selectedFlavours)
            startActivity(intent)
            finish()
        }, 7000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
