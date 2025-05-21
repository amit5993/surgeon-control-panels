package com.surgeon.controlpanels.common


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.surgeon.controlpanels.R
import org.json.JSONObject
import java.io.File
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


val INTENT_DATA = "INTENT_DATA"

//fun Context.loadImageNoCrop(image: String, imageView: ImageView) {
//    val options =
//        RequestOptions().placeholder(R.drawable.no_image).error(R.drawable.no_image)
//
//    Glide.with(this).load(image).apply(options)
//        .transition(DrawableTransitionOptions.withCrossFade())
//        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
//}

fun Context.loadImageNoCrop(image: String, imageView: ImageView) {
    val options = RequestOptions()
        .placeholder(R.drawable.no_image)  // Image shown while loading
        .error(R.drawable.no_image)        // Image shown on error
        .dontTransform()                   // Prevents any transformation like cropping or scaling
        .diskCacheStrategy(DiskCacheStrategy.ALL) // Caches all versions of the image

    Glide.with(this)
        .load(image)
        .apply(options)
        .transition(DrawableTransitionOptions.withCrossFade()) // Smooth transition
        .into(imageView)
}

fun Context.loadImageNoCrop(image: File, imageView: ImageView) {
    val options = RequestOptions()
        .placeholder(R.drawable.no_image)  // Image shown while loading
        .error(R.drawable.no_image)        // Image shown on error
        .dontTransform()                   // Prevents any transformation like cropping or scaling
        .diskCacheStrategy(DiskCacheStrategy.ALL) // Caches all versions of the image

    Glide.with(this)
        .load(image)
        .apply(options)
        .transition(DrawableTransitionOptions.withCrossFade()) // Smooth transition
        .into(imageView)
}


fun Context.loadImage(image: String, imageView: ImageView) {
    val options = RequestOptions().centerCrop().placeholder(R.drawable.no_image)
        .error(R.drawable.no_image)

    try {
        Glide.with(this).load(image).apply(options)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
    } catch (e: Exception) {
    }
}

fun Context.loadImage(image: File, imageView: ImageView) {
//    allLog("PaymentImage", image)
    Glide.with(this).load(image).into(imageView)
}

fun Context.loadResImage(image: Int, imageView: ImageView) {
    val options = RequestOptions().centerCrop().placeholder(R.drawable.no_image)
        .error(R.drawable.no_image)

    Glide.with(this).load(image)
//        .apply(options)
        .transition(DrawableTransitionOptions.withCrossFade(500))
        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
}

fun getOrderStatus(type: String): String {
    var returnString = ""

    when (type) {
        "0" -> {
            returnString = "Pending"
        }

        "1" -> {
            returnString = "Process"
        }

        "2" -> {
            returnString = "Preparing"
        }

        "3" -> {
            returnString = "Cancel"
        }

        "4" -> {
            returnString = "Complete"
        }
    }
    return returnString
}

fun showCustomToastLayout(context: Context?, message: String?) {
    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val toastView = inflater.inflate(R.layout.custom_toast, null)
    val textView = toastView.findViewById<TextView>(R.id.customToastText)
    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    textView.text = message
    toast.view = toastView
    toast.show()
}

fun isAppExists(context: Context?, packageName: String?): Boolean {
    return try {
        context!!.packageManager.getPackageInfo(packageName!!, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun PasswordFieldHideShow(editText: EditText, IsShowHide: Boolean) {
    if (IsShowHide) {
        editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        editText.setSelection(editText.text.length)
    } else {
        editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        editText.transformationMethod = PasswordTransformationMethod.getInstance()
        editText.setSelection(editText.text.length)
    }
}

fun generateRandomSequenceNo(len: Int): String {
    val chars = "1234567890"
    val rnd = Random
    val sb = StringBuilder(len)
    for (i in 0 until len) sb.append(chars[rnd.nextInt(chars.length)])
    return sb.toString()
}

fun Context.isNetworkConnectionAvailable(): Boolean {
    return if (CheckInternetConnection()) {
        true
    } else {
        CheckNetworkConnectionDialog(
            resources.getString(R.string.no_connection),
            resources.getString(R.string.turn_on_connection)
        )
        false

    }
}

fun Context.CheckInternetConnection(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    val isConnected = activeNetwork != null && activeNetwork.isConnected
    return isConnected
}

fun Context.CheckNetworkConnectionDialog(title: String, msg: String) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(msg)
    builder.setNegativeButton("ok") { dialog, _ -> dialog.dismiss() }
    val alertDialog = builder.create()
    alertDialog.show()
}

fun Context.ToastMessage(message: String) {
//    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    showCustomToastLayout(this, message)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


var dialog: Dialog? = null

@SuppressLint("StaticFieldLeak")
var act: Activity? = null

var sDailog: Dialog? = null

fun Activity.openLoader() {

    act = this

    act!!.runOnUiThread(Runnable {
        if (sDailog != null && sDailog!!.isShowing) {
            sDailog!!.dismiss()
        }

        sDailog = Dialog(this)
        sDailog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sDailog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        sDailog!!.setContentView(R.layout.progress)
        sDailog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sDailog!!.window!!.setDimAmount(0.3f)
        sDailog!!.setCancelable(false)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sDailog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        sDailog!!.show()
        sDailog!!.window!!.attributes = lp
    })
}

fun Activity.closeLoader() {/*try {
        if (dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }*/

    act = this
    act!!.runOnUiThread(Runnable {


        if (sDailog != null && sDailog!!.isShowing) {
            sDailog!!.dismiss()
        }
    })


}


/**
 * To start another activity.
 *
 * @param context get context from activity.
 * @param aClass get reference of the class
 */
fun Activity.NewIntentWithAnimation(
    ourClass: Class<*>, isAnimation: Boolean, isFinish: Boolean
) {
    val intent = Intent(this, ourClass)
    startActivity(intent)
    if (isAnimation) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    } else {
        overridePendingTransition(R.anim.animation_one, R.anim.animation_two)
    }
    if (isFinish) {
        //finish()
        finishAffinity()
    }
}

/**
 * To start another activity.
 *
 * @param context get context from activity.
 * @param aClass get reference of the class
 */
fun Activity.NewIntentWithAnimationClearTop(ourClass: Class<*>, isAnimation: Boolean) {
    val intent = Intent(this, ourClass)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    if (isAnimation) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    } else {
        overridePendingTransition(R.anim.animation_one, R.anim.animation_two)
    }
}

fun Activity.NewIntentWithData(
    ourClass: Class<*>, data: Boolean, isAnimation: Boolean
) {
    val intent = Intent(this, ourClass)
    intent.putExtra(INTENT_DATA, data)
    startActivity(intent)
    if (isAnimation) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    } else {
        overridePendingTransition(R.anim.animation_one, R.anim.animation_two)
    }
}


fun Activity.NewIntentWithData(
    ourClass: Class<*>, hashMap: HashMap<String, Any>, isAnimation: Boolean, isFinish: Boolean
) {
    val intent = Intent(this, ourClass)
    intent.putExtra(INTENT_DATA, hashMap)
    startActivity(intent)
    if (isAnimation) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    } else {
        overridePendingTransition(R.anim.animation_one, R.anim.animation_two)
    }
    if (isFinish) {
        finish()
    }
}

/**
 * To start activity with request code.
 *
 * @param context get context from activity.
 * @param aClass get reference of the class
 * @param requestCode send request code to activity
 */
fun Activity.startActivityForResult(aClass: Class<*>, requestCode: Int, isAnimation: Boolean) {
    val intent = Intent(this, aClass)
    startActivityForResult(intent, requestCode)
//            ((Activity) context).overridePendingTransition(R.anim.slide_up, 0);
    if (isAnimation) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    } else {
        overridePendingTransition(R.anim.animation_one, R.anim.animation_two)
    }
}

fun Activity.NextPreviousAnimation(isAnimation: Boolean, isFinish: Boolean) {
    if (isAnimation) {
        overridePendingTransition(R.anim.anim_right, R.anim.anim_left)
    } else {
        overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl)
    }

    if (isFinish) {
        finish()
    }
}


fun html_decode(ul: String): URI? {
    try {
        val ult = URL(ul)
        return URI("http", ult.host, ult.path, ult.query, null)
    } catch (e: Exception) {
        return null
    }

}

fun getCurrentDate(): String {
    val c = Calendar.getInstance()
    val day = c[Calendar.DAY_OF_MONTH]
    val month = c[Calendar.MONTH]
    val year = c[Calendar.YEAR]
    val date = day.toString() + "-" + (month + 1) + "-" + year
    return date
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val str = sdf.format(Date())
    return str
}

// Function to check if the current time is within 30 minutes of surgery time
fun isWithin30Minutes(surgeryDate: String, surgeryTime: String): Boolean {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    try {
        val surgeryDateTime = inputDateFormat.parse("$surgeryDate $surgeryTime")
        val currentTime = System.currentTimeMillis()

        // 30 minutes in milliseconds
        val thirtyMinutesInMillis = 30 * 60 * 1000

        // Check if the current time is within 30 minutes of surgery time
//        return surgeryDateTime?.time?.let {
//            it - currentTime in 0 until thirtyMinutesInMillis
//        } ?: false


        return surgeryDateTime?.time?.let { scheduledTime ->
            // Condition 1: If current time is before surgery time but within 30 minutes of it
            (scheduledTime - currentTime in 0 until thirtyMinutesInMillis)
                    // Condition 2: OR if the scheduled surgery time is already passed
                    || (currentTime >= scheduledTime)
        } ?: false

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun getDateFormatForDashboard(inputDateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    try {
        val date = inputFormat.parse(inputDateString)
        val formattedDate = outputFormat.format(date)
        return formattedDate
    } catch (e: Exception) {
        return inputDateString
    }

}

fun getTimeFormatForDashboard(inputDateString: String): String {
    val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    try {
        val date = inputFormat.parse(inputDateString)
        val formattedDate = outputFormat.format(date)
        return formattedDate
    } catch (e: Exception) {
        return inputDateString
    }

}

fun Context.putLong(k: String, v: Long) {
    val editor = getSharedPreferences("video_data", 0).edit()
    editor.putLong(k, v)
    editor.apply()
    editor.apply()
}

fun Context.getLong(k: String): Long {
    return getSharedPreferences("video_data", 0)?.getLong(k, 0) ?: 0
}

//fun getGradient(c1: Int, c2: Int): Drawable {
////    Color.parseColor("#" + Hex)
//    val gd = GradientDrawable(
//        GradientDrawable.Orientation.TL_BR,
//        intArrayOf(c1, c2)
//    )
//    gd.cornerRadius = 0f
//
//    return gd
//}

fun getGradient(c1: String, c2: String): Drawable {
//    Color.parseColor("#" + Hex)

    var color1 = c1
    var color2 = c2

    if (color1.isEmpty()) {
        color1 = "#FFFFFF"
    }
    if (color2.isEmpty()) {
        color2 = "#FFFFFF"
    }


    val gd = GradientDrawable(
        GradientDrawable.Orientation.TL_BR,
        intArrayOf(Color.parseColor(color1), Color.parseColor(color2))
    )
    gd.setStroke(3, Color.WHITE)
    gd.cornerRadius = 20f


    return gd
}

fun getGradientNormal(c1: String, c2: String): Drawable {
//    Color.parseColor("#" + Hex)

    var color1 = c1
    var color2 = c2

    if (color1.isEmpty()) {
        color1 = "#FFFFFF"
    }
    if (color2.isEmpty()) {
        color2 = "#FFFFFF"
    }


    val gd = GradientDrawable(
        GradientDrawable.Orientation.TL_BR,
        intArrayOf(Color.parseColor(color1), Color.parseColor(color2))
    )
//    gd.setStroke(3, Color.WHITE)
//    gd.cornerRadius = 20f


    return gd
}

fun convertHumidity(input: String): Double {
    return if (input.length == 3) {
        val number = input.toDoubleOrNull()
        if (number != null) {
            val result = number / 10
            result
        } else {
            0.0
        }
    } else {
        input.toDouble()
    }
}

fun convertTemp(input: String): Double {
    return if (input.length == 3) {
        val number = input.toDoubleOrNull()
        if (number != null) {
            val result = number / 10
            result
        } else {
            0.0
        }
    } else {
        input.toDouble()
    }
}

fun getOTModeLabel(context: Context): String = when (context.getCurrentMode()) {
    OTStatus.STARTED -> "END SURGERY"
    OTStatus.FINISHED -> "Start Cleaning Mode"
    OTStatus.CLEANING -> "Finish Cleaning Mode"
    OTStatus.CLEANING_FINISH -> "Start Defumigation"
    OTStatus.DEFUMIGATION -> "Ready For New Surgery"//"Start Disinfection"
//    OTStatus.DISINFECTION -> "Ready For New Surgery"
    OTStatus.READY_FOR_OT -> "BEGIN SURGERY"
    else -> "Start"
}

fun updateOtMode(context: Context) {
    when (context.getCurrentMode()) {
        OTStatus.STARTED -> {
            context.setCurrentMode(OTStatus.FINISHED)
        }

        OTStatus.FINISHED -> {
            context.setCurrentMode(OTStatus.CLEANING)
        }

        OTStatus.CLEANING -> {
            context.setCurrentMode(OTStatus.CLEANING_FINISH)
        }

        OTStatus.CLEANING_FINISH -> {
            context.setCurrentMode(OTStatus.DEFUMIGATION)
        }

        OTStatus.DEFUMIGATION -> {
            context.setCurrentMode(OTStatus.READY_FOR_OT)
            //context.setCurrentMode(OTStatus.DISINFECTION)
        }

//        OTStatus.DISINFECTION -> {
//            context.setCurrentMode(OTStatus.READY_FOR_OT)
//        }

        OTStatus.READY_FOR_OT -> {
            context.setCurrentMode(OTStatus.STARTED)
        }
    }
}

fun callUpdateOTModeAPI(context: Context, otId: Int, duration: String): JSONObject {

    val mode = when (context.getCurrentMode()) {
        OTStatus.STARTED -> OTStatus.FINISHED
        OTStatus.FINISHED -> OTStatus.CLEANING
        OTStatus.CLEANING -> OTStatus.CLEANING_FINISH
        OTStatus.CLEANING_FINISH -> OTStatus.DEFUMIGATION
        OTStatus.DEFUMIGATION -> OTStatus.READY_FOR_OT //OTStatus.DISINFECTION
        //OTStatus.DISINFECTION -> OTStatus.READY_FOR_OT
        OTStatus.READY_FOR_OT -> OTStatus.STARTED
        else -> ""
    }

    return JSONObject().apply {
        put("operation_theatre_id", otId)
        put("mode", mode)
        put("duration", duration)
    }

}