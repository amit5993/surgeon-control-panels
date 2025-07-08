package com.surgeon.controlpanels.common

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator

object Utils {
    private var lastClickTime: Long = 0

    fun canClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        val canClick = currentTime - lastClickTime > 2000 // 2 seconds delay
        if (canClick) {
            lastClickTime = currentTime
        }
        return canClick
    }

    fun blinkAnimation(view: View){
        val animation: Animation = AlphaAnimation(1f, 0f)
        animation.setDuration(600)
        animation.interpolator = LinearInterpolator()
        animation.setRepeatCount(Animation.INFINITE)
        animation.repeatMode = Animation.REVERSE
        view.startAnimation(animation)
    }
}