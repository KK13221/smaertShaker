package com.example.smartshake.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class GlowCupView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f * context.resources.displayMetrics.density
    }

    private var progress = 0f
    private var alphaPulse = 0.5f

    private val rectF = RectF()

    init {
        // Pulse animator for the falling liquid
        ValueAnimator.ofFloat(0.5f, 0.8f).apply {
            duration = 1500
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                alphaPulse = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    /**
     * Updates the liquid level in the cup.
     * @param newProgress A value from 0.0 to 1.0 (0% to 100%).
     */
    fun setProgress(newProgress: Float) {
        this.progress = newProgress.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val cornerRadius = 30f * context.resources.displayMetrics.density

        // 1. The Machine Alcove
        val alcoveWidth = w * 0.9f
        val alcoveHeight = h
        val alcoveStartX = (w - alcoveWidth) / 2
        val alcoveStartY = 0f
        val alcoveCornerRadius = 40f * context.resources.displayMetrics.density

        // Dark back plate
        paint.color = Color.parseColor("#131313")
        rectF.set(alcoveStartX, alcoveStartY, alcoveStartX + alcoveWidth, alcoveStartY + alcoveHeight)
        canvas.drawRoundRect(rectF, alcoveCornerRadius, alcoveCornerRadius, paint)

        // Horizontal scanline
        val scanlineY = h * 0.35f
        paint.color = Color.parseColor("#4DE8FF00") // Electric lime with 30% alpha
        paint.strokeWidth = 2f * context.resources.displayMetrics.density
        canvas.drawLine(alcoveStartX - 40f, scanlineY, alcoveStartX + alcoveWidth + 40f, scanlineY, paint)

        // 2. Translucent Cup
        val cupRectWidth = w * 0.7f
        val cupRectHeight = h * 0.65f
        val startX = (w - cupRectWidth) / 2
        val startY = h - cupRectHeight - 40f

        paint.color = Color.parseColor("#1E1E1E")
        rectF.set(startX, startY, startX + cupRectWidth, startY + cupRectHeight)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

        val maxFluidHeight = cupRectHeight - 20f
        val currentFluidHeight = maxFluidHeight * progress
        val fluidTop = (startY + cupRectHeight - currentFluidHeight)

        // 3. The Pipe Nozzle (NEW)
        val pipeWidth = 30f * context.resources.displayMetrics.density
        val pipeHeight = 40f * context.resources.displayMetrics.density
        val pipeX = (w - pipeWidth) / 2
        val pipeY = 0f
        
        paint.color = Color.WHITE
        rectF.set(pipeX, pipeY, pipeX + pipeWidth, pipeY + pipeHeight)
        canvas.drawRoundRect(rectF, 10f, 10f, paint)

        // 4. Falling fluid stream
        if (progress < 1f && progress > 0f) {
            val alphaInt = (alphaPulse * 255).toInt()
            paint.color = Color.argb(alphaInt, 255, 255, 255) // White stream
            
            // Pulsing width for "premium" flow feel
            val streamWidthBase = 12f * context.resources.displayMetrics.density
            val currentStreamWidth = streamWidthBase * (0.8f + (alphaPulse * 0.4f)) 
            
            paint.strokeWidth = currentStreamWidth
            paint.strokeCap = Paint.Cap.ROUND
            canvas.drawLine(w / 2, pipeHeight - 5f, w / 2, fluidTop + 10f, paint)
        }

        // 5. Fluid Fill
        if (progress > 0) {
            // We use a path to clip the liquid exactly to the cup's rounded corners
            val clipPath = android.graphics.Path()
            rectF.set(startX, startY, startX + cupRectWidth, startY + cupRectHeight)
            clipPath.addRoundRect(rectF, cornerRadius, cornerRadius, android.graphics.Path.Direction.CW)
            
            canvas.save()
            canvas.clipPath(clipPath)
            
            // Draw the liquid rect inside the clipped area
            rectF.set(startX, fluidTop, startX + cupRectWidth, startY + cupRectHeight)
            val gradient = LinearGradient(0f, fluidTop, 0f, startY + cupRectHeight,
                Color.parseColor("#E6FFFFFF"), // 90% white (User's preferred color)
                Color.parseColor("#99FFFFFF"), // 60% white
                Shader.TileMode.CLAMP
            )
            paint.shader = gradient
            canvas.drawRect(rectF, paint)
            paint.shader = null
            
            canvas.restore()
        }

        // 6. Cup Border
        strokePaint.color = Color.parseColor("#333333")
        rectF.set(startX, startY, startX + cupRectWidth, startY + cupRectHeight)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, strokePaint)
    }
}
