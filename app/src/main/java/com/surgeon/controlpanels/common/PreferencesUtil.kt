package com.surgeon.controlpanels.common

import android.content.Context
import android.content.SharedPreferences
import com.surgeon.controlpanels.R
import java.text.SimpleDateFormat
import java.util.*

internal const val OTDetails = "OTDetails"
internal const val device = "Device"
internal const val latestSurgery = "LatestSurgery"
internal const val currentMode = "CurrentMode"
internal const val isEntrance = "IsEntrance"
internal const val isReport = "IsReport"
internal const val isFirstTempToday = "IsFirstTempToday"
internal const val isFirstRhToday = "IsFirstRhToday"
internal const val token = "Token"
internal const val sound = "sound"
internal const val lastOpenDate = "LastOpenDate"
internal const val SrWsl = "SrWsl"

fun Context.getSharedPreferences(): SharedPreferences {
    return getSharedPreferences(
        getString(R.string.app_name).replace(" ", "_"),
        Context.MODE_PRIVATE
    )
}

fun Context.setOTDetails(data: String) {
    val editor = getSharedPreferences().edit()
    editor.putString(OTDetails, data)
    editor.apply()
}

fun Context.getOTDetails(): String {
    return getSharedPreferences().getString(OTDetails, "")!!
}

fun Context.setSound(data: Boolean) {
    val editor = getSharedPreferences().edit()
    editor.putBoolean(sound, data)
    editor.apply()
}

fun Context.getSound(): Boolean {
    return getSharedPreferences().getBoolean(sound, true)
}

fun Context.setIsEntranceApp(data: Boolean) {
    val editor = getSharedPreferences().edit()
    editor.putBoolean(isEntrance, data)
    editor.apply()
}

fun Context.getIsEntranceApp(): Boolean {
    return getSharedPreferences().getBoolean(isEntrance, false)
}

fun Context.setIsFirstTempToday(data: Boolean) {
    val editor = getSharedPreferences().edit()
    editor.putBoolean(isFirstTempToday, data)
    editor.apply()
}

fun Context.getIsFirstTempToday(): Boolean {
    return getSharedPreferences().getBoolean(isFirstTempToday, false)
}

fun Context.setIsFirstRhToday(data: Boolean) {
    val editor = getSharedPreferences().edit()
    editor.putBoolean(isFirstRhToday, data)
    editor.apply()
}

fun Context.getIsFirstRhToday(): Boolean {
    return getSharedPreferences().getBoolean(isFirstRhToday, false)
}

fun Context.isFirstOpenToday(): Boolean {
    val prefs = getSharedPreferences()
    val lastDate = prefs.getString(lastOpenDate, null)

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    return if (lastDate != today) {
        prefs.edit().putString(lastOpenDate, today).apply()
        setIsFirstTempToday(true)
        setIsFirstRhToday(true)
        true
    } else {
        false
    }
}


fun Context.clearPreferences() {
    val editor = getSharedPreferences().edit()
    editor.clear()
    editor.apply()
}