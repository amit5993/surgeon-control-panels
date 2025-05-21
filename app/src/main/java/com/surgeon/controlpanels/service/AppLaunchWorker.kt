package com.surgeon.controlpanels.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.surgeon.controlpanels.activity.SplashActivity

class AppLaunchWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private val TAG = "AppLaunchWorker"
    
    override fun doWork(): Result {
        try {
            Log.d(TAG, "AppLaunchWorker starting app")
            
            val launchIntent = Intent(applicationContext, SplashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("FROM_BOOT", true)
            }
            applicationContext.startActivity(launchIntent)
            Log.d(TAG, "SplashActivity launched from worker")
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error launching app: ${e.message}", e)
            return Result.failure()
        }
    }
}