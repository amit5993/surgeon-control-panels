package com.surgeon.controlpanels.common

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.surgeon.controlpanels.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CombinedClockView @JvmOverloads constructor(
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

    private val datePaint = Paint().apply {
        color = Color.WHITE
        textSize = 16f
        typeface = Typeface.create("poppins_regular", Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            invalidate()
            handler.postDelayed(this, 16)
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

        // Draw analog clock face
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.clock_face)
        canvas.drawBitmap(bitmap, null, canvas.clipBounds, dialPaint)

        // Get current time
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = now }
        val hours = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f

        // Draw analog hands
        val hourAngle = (hours + minutes / 60f) * 30f
        val minuteAngle = (minutes + seconds / 60f) * 6f
        val secondAngle = seconds * 6f

        drawHand(canvas, widthF / 2, heightF / 2, radius * 0.5f, hourAngle, 8f, Color.WHITE)
        drawHand(canvas, widthF / 2, heightF / 2, radius * 0.75f, minuteAngle, 6f, Color.WHITE)
        drawHand(canvas, widthF / 2, heightF / 2, radius * 0.9f, secondAngle, 4f, Color.RED)

        // Draw date
        val dateText = dateFormat.format(Date())
        canvas.drawText(dateText, widthF / 2, heightF * 0.4f, datePaint)
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
        val radian = Math.toRadians(angle.toDouble() - 90)
        val endX = cx + length * Math.cos(radian).toFloat()
        val endY = cy + length * Math.sin(radian).toFloat()
        canvas.drawLine(cx, cy, endX, endY, handPaint)
    }
}

