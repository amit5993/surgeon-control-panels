package com.surgeon.controlpanels.common

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.surgeon.controlpanels.R
import java.util.Calendar

class AnalogClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dialPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val handPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    // Update at roughly 60fps
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            invalidate() // Force a redraw
            handler.postDelayed(this, 16) // about 16ms delay ~60fps
        }
    }

    init {
        handler.post(runnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val widthF = width.toFloat()
        val heightF = height.toFloat()
        val radius = Math.min(widthF, heightF) / 2 - 16

        // Draw the clock dial using a background image (adjust as needed)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.clock_face)
        canvas.drawBitmap(bitmap, null, canvas.clipBounds, dialPaint)

        // Get the current time (with milliseconds for smooth seconds)
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = now }
        val hours = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        // Use fractional seconds for smooth movement:
        val seconds = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f

        // Calculate angles for the hands:
        // Hour hand: 360/12 = 30° per hour
        val hourAngle = (hours + minutes / 60f) * 30f
        // Minute hand: 360/60 = 6° per minute, adding seconds fraction
        val minuteAngle = (minutes + seconds / 60f) * 6f
        // Second hand: 360/60 = 6° per second, using fractional seconds for smoothness
        val secondAngle = seconds * 6f

        // Draw the hands
        drawHand(canvas, widthF / 2, heightF / 2, radius * 0.5f, hourAngle, 8f, Color.WHITE)
        drawHand(canvas, widthF / 2, heightF / 2, radius * 0.75f, minuteAngle, 6f, Color.WHITE)
        drawHand(canvas, widthF / 2, heightF / 2, radius * 0.9f, secondAngle, 4f, Color.RED)
    }

    private fun drawHand(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        length: Float,
        angle: Float,
        strokeWidth: Float,
        color: Int
    ) {
        handPaint.color = color
        handPaint.strokeWidth = strokeWidth
        // Convert angle to radians (subtract 90° to adjust for canvas coordinate system)
        val radian = Math.toRadians(angle.toDouble() - 90)
        val endX = cx + length * Math.cos(radian).toFloat()
        val endY = cy + length * Math.sin(radian).toFloat()
        canvas.drawLine(cx, cy, endX, endY, handPaint)
    }
}

