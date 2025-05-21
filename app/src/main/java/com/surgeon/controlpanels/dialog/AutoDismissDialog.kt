package com.surgeon.controlpanels.dialog
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent

open class AutoDismissDialog(context: Context) : Dialog(context) {

    // Delay (in milliseconds) before auto-dismiss
    var autoDismissDelay: Long = 10000L

    private val handler = Handler(Looper.getMainLooper())
    private val autoDismissRunnable = Runnable {
        if (isShowing) {
            dismiss()
        }
    }

    init {
        // When the dialog is shown, start the auto-dismiss timer.
        setOnShowListener {
            handler.postDelayed(autoDismissRunnable, autoDismissDelay)
        }
    }

    // Override dispatchTouchEvent to reset the timer on any touch.
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // Remove any pending callbacks
        handler.removeCallbacks(autoDismissRunnable)
        // Post a new callback for the auto-dismiss timer
        handler.postDelayed(autoDismissRunnable, autoDismissDelay)
        return super.dispatchTouchEvent(ev)
    }

    // Ensure we cancel the timer when dismissing the dialog.
    override fun dismiss() {
        handler.removeCallbacks(autoDismissRunnable)
        super.dismiss()
    }
}
