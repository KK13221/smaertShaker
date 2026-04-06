package com.example.smartshake.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class MixingVisualView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private var rotationDegrees = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 5f * context.resources.displayMetrics.density
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 110f * context.resources.displayMetrics.scaledDensity
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    
    private val percentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E8FF00") // Electric Lime
        textSize = 36f * context.resources.displayMetrics.scaledDensity
        isFakeBoldText = true
    }

    private val rectF = RectF()

    init {
        // Rotation Animator
        ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 12000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                rotationDegrees = it.animatedValue as Float
                invalidate()
            }
            start()
        }

        // Progress Animator
        ValueAnimator.ofFloat(0f, 0.65f).apply {
            duration = 6500
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2f
        val cy = h / 2f
        
        val innerRadius = w * 0.4f

        // Draw chocolate backing
        paint.shader = RadialGradient(
            cx, cy, innerRadius,
            intArrayOf(Color.parseColor("#8B5A2B"), Color.parseColor("#3B2416")),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, innerRadius, paint)
        paint.shader = null

        // Draw double dashed rings with rotation
        canvas.save()
        canvas.rotate(rotationDegrees, cx, cy)
        
        rectF.set(cx - w * 0.45f, cy - h * 0.45f, cx + w * 0.45f, cy + h * 0.45f)
        
        val numSegments = 12
        val sweepAngle = (360f / numSegments) - 15f
        
        for (i in 0 until numSegments) {
            val angleOffset = i * (360f / numSegments)
            val active = i < (numSegments * 0.65).toInt() // Match exactly Compose logic
            
            strokePaint.color = if (active) Color.parseColor("#E8FF00") else Color.parseColor("#1AE8FF00")
            canvas.drawArc(rectF, angleOffset, sweepAngle, false, strokePaint)
        }
        
        canvas.restore()

        // Draw text
        val valueStr = (progress * 100).toInt().toString()
        val textWidth = textPaint.measureText(valueStr)
        val textY = cy + (textPaint.textSize / 3) // Vertical center alignment roughly
        
        canvas.drawText(valueStr, cx, textY, textPaint)
        canvas.drawText("%", cx + (textWidth / 2) + 10f, textY - 40f, percentPaint)
    }
}
