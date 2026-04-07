package com.example.smartshake

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.smartshake.Utils.Utils
import com.example.smartshake.data.model.FlavourItem
import com.example.smartshake.ui.views.MixingVisualView

class MixingActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.hideSystemBars(this)
        setContentView(R.layout.activity_mixing)

        val mixingVisualView = findViewById<MixingVisualView>(R.id.mixingVisualView)

        // Receive selected flavours and load the primary one's image
        @Suppress("DEPRECATION")
        val selectedFlavours = intent.getSerializableExtra("selected_flavours") as? ArrayList<FlavourItem>
        val primaryFlavour = selectedFlavours?.find { it.scoops > 0 }

        primaryFlavour?.let { flavour ->
            Glide.with(this)
                .asBitmap()
                .load(flavour.image)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        mixingVisualView.setFlavorImage(resource)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }

        val pbSegments = arrayOf(
            findViewById<android.widget.ProgressBar>(R.id.pbSegment1),
            findViewById<android.widget.ProgressBar>(R.id.pbSegment2),
            findViewById<android.widget.ProgressBar>(R.id.pbSegment3),
            findViewById<android.widget.ProgressBar>(R.id.pbSegment4),
            findViewById<android.widget.ProgressBar>(R.id.pbSegment5)
        )

        // Animate the 5 segments over 6.5 seconds
        android.animation.ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 6500
            interpolator = android.view.animation.LinearInterpolator()
            addUpdateListener { animator ->
                val totalProgress = animator.animatedValue as Float
                val segmentCount = pbSegments.size
                
                for (i in 0 until segmentCount) {
                    val segmentStart = i.toFloat() / segmentCount
                    val segmentEnd = (i + 1).toFloat() / segmentCount
                    
                    val segmentProgress = when {
                        totalProgress <= segmentStart -> 0f
                        totalProgress >= segmentEnd -> 1f
                        else -> (totalProgress - segmentStart) / (segmentEnd - segmentStart)
                    }
                    
                    pbSegments[i].progress = (segmentProgress * 100).toInt()
                }
            }
            start()
        }

        // Automatically navigate to DoneActivity after 6.5 seconds
        handler.postDelayed({
            val intent = Intent(this, DoneActivity::class.java)
            intent.putExtra("selected_flavours", selectedFlavours)
            startActivity(intent)
            finish()
        }, 6500)

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
