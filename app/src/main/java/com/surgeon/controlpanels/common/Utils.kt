package com.surgeon.controlpanels.common

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
}