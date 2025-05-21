package com.surgeon.controlpanels.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.surgeon.controlpanels.activity.BootActivity

class BootReceiver : BroadcastReceiver() {
    private val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val action = intent.action
            Log.d(TAG, "BootReceiver onReceive: $action")

            if (action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
                action == Intent.ACTION_USER_UNLOCKED) {

                Log.d(TAG, "Boot completed - starting BootActivity")

                // Add a small delay to ensure system is ready
                Handler(Looper.getMainLooper()).postDelayed({
                    val bootIntent = Intent(context, BootActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    }
                    context.startActivity(bootIntent)
                    Log.d(TAG, "BootActivity started")
                }, 5000)
            }
                            } catch (e: Exception) {
            Log.e(TAG, "Error in BootReceiver", e)
        }
    }
}


//override fun onReceive(context: Context, intent: Intent) {
//    try {
//        val action = intent.action
//        Log.d(TAG, "BootReceiver onReceive: $action")
//
//        if (action == Intent.ACTION_BOOT_COMPLETED ||
//            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
//            action == Intent.ACTION_USER_UNLOCKED
//        ) {
//
//            Log.d(TAG, "Boot completed - scheduling app launch with WorkManager")
//
//            val appLaunchRequest = OneTimeWorkRequestBuilder<AppLaunchWorker>()
//                .setInitialDelay(10, TimeUnit.SECONDS)
//                .build()
//
//            WorkManager.getInstance(context).enqueue(appLaunchRequest)
//        }
//    } catch (e: Exception) {
//        Log.e(TAG, "Error in BootReceiver", e)
//    }
//}