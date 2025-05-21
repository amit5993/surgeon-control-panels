package com.surgeon.controlpanels.service

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

class BootForegroundService : Service() {
    companion object {
        private const val TAG = "BootForegroundService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate called")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand called")

        try {
            // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }

            // Create intent for SplashActivity
            val notificationIntent = Intent(this, SplashActivity::class.java)
            notificationIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )

            // Create pending intent
            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
        }

            val pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, pendingIntentFlags
            )

            // Build notification
            val notification = NotificationCompat.Builder(this, "boot_channel_id")
                .setContentTitle("App Starting")
                .setContentText("App is starting after boot...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .build()

            // Start foreground service with notification
            startForeground(1, notification)
            Log.d(TAG, "Foreground service started with notification")

            // Launch SplashActivity after a delay
            // In the onStartCommand method, replace the activity launch code with:

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    // Use a more direct approach to start the activity
                    val launchIntent = Intent(applicationContext, SplashActivity::class.java)
                    launchIntent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    launchIntent.action = Intent.ACTION_MAIN
                    launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)

                    Log.d(TAG, "Starting activity directly: SplashActivity")
                    applicationContext.startActivity(launchIntent)

                    // Wait a bit longer before stopping the service
                    Handler(Looper.getMainLooper()).postDelayed({
                        Log.d(TAG, "Stopping service after activity launch")
                        stopSelf()
                    }, 3000)
                } catch (e: Exception) {
                    Log.e(TAG, "Error launching SplashActivity directly: ${e.message}", e)

                    try {
                        // Fallback to BootHelperActivity
//                        val helperIntent = Intent(applicationContext, BootHelperActivity::class.java)
//                        helperIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        Log.d(TAG, "Falling back to BootHelperActivity")
//                        applicationContext.startActivity(helperIntent)
                    } catch (e2: Exception) {
                        Log.e(TAG, "Error launching BootHelperActivity: ${e2.message}", e2)
                    }
                }
            }, 5000) // 5 second delay
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStartCommand: ${e.message}", e)
}

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy called")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "boot_channel_id",
                    "Boot Service Channel",
                    NotificationManager.IMPORTANCE_LOW
                )
                channel.description = "Channel for boot service notifications"
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification channel: ${e.message}", e)
        }
    }
}
