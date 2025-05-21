package com.surgeon.controlpanels.common

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import com.surgeon.controlpanels.R


class VerticalSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var labelOff: TextView
    private lateinit var labelOn: TextView
    private lateinit var switchHandle: View

    private var isChecked = false

    init {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.vertical_switch, this)
        orientation = VERTICAL

        labelOff = findViewById(R.id.labelOff)
        labelOn = findViewById(R.id.labelOn)
        switchHandle = findViewById(R.id.switchHandle)

        updateView()

        setOnClickListener{
            toggle()
        }

//        setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                toggle()
//                true
//            } else {
//                false
//            }
//        }
    }

    private fun toggle() {
        isChecked = !isChecked
//        animateSwitch()
        updateView()
    }

    private fun animateSwitch() {
        val translationY = if (isChecked) -switchHandle.height.toFloat() else 0f
        ViewCompat.animate(switchHandle)
            .translationY(translationY)
            .setDuration(200)
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View) {}
                override fun onAnimationEnd(view: View) {
                    updateView()
                }
                override fun onAnimationCancel(view: View) {}
            })
            .start()
    }

//    private fun updateView() {
//        if (isChecked) {
//            setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
//            labelOff.visibility = View.GONE
//            labelOn.visibility = View.VISIBLE
//        } else {
//            setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
//            labelOff.visibility = View.VISIBLE
//            labelOn.visibility = View.GONE
//        }
//    }

    private fun updateView() {
        val colorRes = if (isChecked) android.R.color.holo_green_light else android.R.color.holo_red_light
        val color = ContextCompat.getColor(context, colorRes)

        // Create a GradientDrawable with rounded corners.
        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 10 * context.resources.displayMetrics.density
            setColor(color)
        }

        // Set the drawable as the background.
        background = backgroundDrawable

        // Toggle the visibility of labels accordingly.
        if (isChecked) {
            labelOff.visibility = View.GONE
            labelOn.visibility = View.VISIBLE
        } else {
            labelOff.visibility = View.VISIBLE
            labelOn.visibility = View.GONE
        }
    }

    fun setChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
//            animateSwitch()
            updateView()
        }
    }

    fun isChecked(): Boolean = isChecked
}
