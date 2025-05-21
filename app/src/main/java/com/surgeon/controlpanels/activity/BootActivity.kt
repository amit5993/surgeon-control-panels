package com.surgeon.controlpanels.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.surgeon.controlpanels.R

class BootActivity : AppCompatActivity() {
    private val TAG = "BootActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)
        
        Log.d(TAG, "BootActivity created")
        
        // Short delay then launch main app
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val launchIntent = Intent(this, SplashActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("FROM_BOOT", true)
                }
                startActivity(launchIntent)
                Log.d(TAG, "SplashActivity launched from BootActivity")
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Error launching SplashActivity: ${e.message}", e)
                finish()
            }
        }, 2000)
    }
}