package com.surgeon.controlpanels.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.common.getIsEntranceApp
import com.surgeon.controlpanels.common.getOTDetails
import com.surgeon.controlpanels.common.isFirstOpenToday
import com.surgeon.controlpanels.common.setIsEntranceApp
import com.surgeon.controlpanels.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        getIncomingIntent()
        isFirstOpenToday()

        //todo comment in production
        //setIsEntranceApp(false)

        playVideo()
        requestDisableBatteryOptimization()
    }

    private fun getIncomingIntent() {
        try {
            val fromBoot = intent.getBooleanExtra("FROM_BOOT", false)
            if (fromBoot) {
                Log.d("SplashActivity", "Started from boot")
                // Special handling for boot case if needed
            }

            // Rest of your onCreate code
        } catch (e: java.lang.Exception) {
            Log.e("SplashActivity", "Error in onCreate", e)
            // Handle the error gracefully
        }
    }

    private fun navigate() {

        if (getOTDetails().isNotEmpty()) {
            if (getIsEntranceApp()) {
                startActivity(Intent(this, Entrance2::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
        } else {
            startActivity(Intent(this, Login::class.java))
        }

        //startActivity(Intent(this, Login::class.java))
        finish()
    }

    private fun playVideo() {
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_video)
        binding.videoView.setVideoURI(videoUri)

        binding.videoView.setOnCompletionListener {
            navigate()
        }

        binding.videoView.setOnErrorListener { mp, what, extra ->
            navigate()
            true
        }

        binding.videoView.start()
    }

    private fun requestDisableBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = packageName
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("SplashActivity", "Error requesting battery optimization: ${e.message}")
                }
            }
        }
    }
}