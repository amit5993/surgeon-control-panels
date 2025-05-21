package com.surgeon.controlpanels.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CircularSeekBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress: Int = 0
    private var max: Int = 100
    private var startAngle: Float = -90f
    private val strokeWidth: Float = 20f
    private val progressColor: Int = Color.CYAN
    private val backgroundColor: Int = Color.DKGRAY
    private val textColor: Int = Color.WHITE
    private val textSize: Float = 80f

    private val backgroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = this@CircularSeekBar.strokeWidth
        color = backgroundColor
        isAntiAlias = true
    }

    private val progressPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = this@CircularSeekBar.strokeWidth
        color = progressColor
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = textColor
        textSize = this@CircularSeekBar.textSize
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val pointerPaint = Paint().apply {
        style = Paint.Style.FILL
        color = progressColor
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val radius = (min(width, height) / 2).toFloat() - strokeWidth

        val cx = (width / 2).toFloat()
        val cy = (height / 2).toFloat()

        // Draw the background circle
        canvas.drawCircle(cx, cy, radius, backgroundPaint)

        // Calculate the sweep angle
        val sweepAngle = (progress / max.toFloat()) * 360

        // Draw the progress arc
        canvas.drawArc(
            cx - radius, cy - radius,
            cx + radius, cy + radius,
            startAngle, sweepAngle,
            false, progressPaint
        )

        // Draw the progress value in the center
        canvas.drawText(progress.toString(), cx, cy + textSize / 3, textPaint)

        // Draw the pointer
        val pointerX = cx + radius * cos(Math.toRadians((startAngle + sweepAngle).toDouble())).toFloat()
        val pointerY = cy + radius * sin(Math.toRadians((startAngle + sweepAngle).toDouble())).toFloat()
        canvas.drawCircle(pointerX, pointerY, strokeWidth / 2, pointerPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x - width / 2
            val y = event.y - height / 2
            val angle = (atan2(y, x) * (180 / Math.PI) + 360) % 360
            val newProgress = (angle / 360 * max).toInt()
            progress = newProgress
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun getProgress(): Int {
        return progress
    }

    fun setMax(max: Int) {
        this.max = max
        invalidate()
    }

    fun getMax(): Int {
        return max
    }
}