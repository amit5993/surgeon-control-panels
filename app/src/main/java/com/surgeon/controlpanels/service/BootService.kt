package com.surgeon.controlpanels.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.activity.SplashActivity

class BootService : Service() {
    private val TAG = "BootService"
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "boot_service_channel"
    private val MAX_ATTEMPTS = 5
    private var attemptCount = 0

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BootService onCreate")

        // Start as foreground service to increase reliability
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "BootService started")

        // First attempt immediately
        attemptToStartApp()

        return START_STICKY
    }

    private fun attemptToStartApp() {
        if (attemptCount >= MAX_ATTEMPTS) {
            Log.d(TAG, "Maximum attempts reached, stopping service")
            stopSelf()
            return
        }

        attemptCount++
        Log.d(TAG, "Attempt #$attemptCount to start app")

        try {
            val launchIntent = Intent(applicationContext, SplashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                addCategory(Intent.CATEGORY_LAUNCHER)
                action = Intent.ACTION_MAIN
                putExtra("FROM_BOOT", true)
            }
            applicationContext.startActivity(launchIntent)
            Log.d(TAG, "SplashActivity launch attempt #$attemptCount")

            // Schedule next attempt with increasing delay
            Handler(Looper.getMainLooper()).postDelayed({
                attemptToStartApp()
            }, (5000 * attemptCount).toLong()) // Increasing delay: 5s, 10s, 15s, etc.
        } catch (e: Exception) {
            Log.e(TAG, "Error launching SplashActivity: ${e.message}", e)

            // Try again sooner if there was an error
            Handler(Looper.getMainLooper()).postDelayed({
                attemptToStartApp()
            }, 3000)
        }
    }

    private fun createNotification(): Notification {
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Boot Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent for notification click
        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Starting App")
            .setContentText("Preparing to launch the application...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}