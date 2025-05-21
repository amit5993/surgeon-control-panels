package com.surgeon.controlpanels.common

import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TimeDifferenceUpdater(private val startTime: String, private val callback: (String) -> Unit) {

    private val handler = Handler(Looper.getMainLooper())
    private var elapsedMillis: Long = 0
    private var isRunning = false

    private val runnable = object : Runnable {
        override fun run() {
            if (!isRunning) return // Stop if the timer is not running

            elapsedMillis += 1000 // Increment time by 1 second

            val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60

            val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            callback(formattedTime) // Update UI

            handler.postDelayed(this, 1000) // Run every second
        }
    }

    fun start() {
        if (isRunning) return // Prevent multiple starts

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try {
            val startDate = dateFormat.parse(startTime)
            val currentDate = Date()

            elapsedMillis = currentDate.time - (startDate?.time ?: 0)
            isRunning = true
            handler.post(runnable) // Start updating
        } catch (e: Exception) {
            e.printStackTrace()
            callback("Invalid time")
        }
    }

    fun stop() {
        if (!isRunning) return
        isRunning = false
        handler.removeCallbacks(runnable) // Stop updating
    }
}