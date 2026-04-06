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

        // Fill progress animator
        ValueAnimator.ofFloat(0f, 0.65f).apply {
            duration = 6500
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start() // Auto start for now
        }
    }

    fun startDispense() {
        progress = 0f
        ValueAnimator.ofFloat(0f, 0.65f).apply {
            duration = 6500
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
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

        // 3. Falling fluid stream
        if (progress < 1f && progress > 0f) {
            val alphaInt = (alphaPulse * 255).toInt()
            paint.color = Color.argb(alphaInt, 232, 255, 0)
            paint.strokeWidth = 14f * context.resources.displayMetrics.density
            paint.strokeCap = Paint.Cap.ROUND
            canvas.drawLine(w / 2, alcoveStartY, w / 2, fluidTop + 10f, paint)
        }

        // 4. Fluid Fill
        if (progress > 0) {
            rectF.set(startX, fluidTop, startX + cupRectWidth, startY + cupRectHeight)
            val gradient = LinearGradient(0f, fluidTop, 0f, startY + cupRectHeight,
                Color.parseColor("#E6E8FF00"), // 90%
                Color.parseColor("#99E8FF00"), // 60%
                Shader.TileMode.CLAMP
            )
            paint.shader = gradient
            canvas.drawRoundRect(rectF, if (progress >= 0.95f) cornerRadius else 0f, cornerRadius, paint)
            paint.shader = null
        }

        // 5. Cup Border
        strokePaint.color = Color.parseColor("#333333")
        rectF.set(startX, startY, startX + cupRectWidth, startY + cupRectHeight)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, strokePaint)
    }
}
