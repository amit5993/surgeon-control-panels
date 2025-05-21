package com.surgeon.controlpanels.common

import android.content.Context
import android.content.SharedPreferences
import com.surgeon.controlpanels.R
import java.text.SimpleDateFormat
import java.util.*

internal const val userData = "UserData"
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

fun Context.setToken(data: String) {
    val editor = getSharedPreferences().edit()
    editor.putString(token, data)
    editor.apply()
}

fun Context.getToken(): String? {
    return getSharedPreferences().getString(token, "")
}

fun Context.setSound(data: Boolean) {
    val editor = getSharedPreferences().edit()
    editor.putBoolean(sound, data)
    editor.apply()
}

fun Context.getSound(): Boolean {
    return getSharedPreferences().getBoolean(sound, true)
}


fun Context.setUserData(data: String) {
    val editor = getSharedPreferences().edit()
    editor.putString(userData, data)
    editor.apply()
}

fun Context.getUserData(): String? {
    return getSharedPreferences().getString(userData, "")
}


fun Context.setDeviceData(data: String) {
    val editor = getSharedPreferences().edit()
    editor.putString(device, data)
    editor.apply()
}

fun Context.getDeviceData(): String? {
    return getSharedPreferences().getString(device, "")
}

fun Context.setLatestSurgery(data: String) {
    val editor = getSharedPreferences().edit()
    editor.putString(latestSurgery, data)
    editor.apply()
}

fun Context.getLatestSurgery(): String? {
    return getSharedPreferences().getString(latestSurgery, "")
}

fun Context.setCurrentMode(data: String) {
    val editor = getSharedPreferences().edit()
    editor.putString(currentMode, data)
    editor.apply()
}

fun Context.getCurrentMode(): String? {
    return getSharedPreferences().getString(currentMode, OTStatus.READY_FOR_OT)
}

fun Context.setIsEntranceLogin(data: Boolean) {
    val editor = getSharedPreferences().edit()
    editor.putBoolean(isEntrance, data)
    editor.apply()
}

fun Context.getIsEntranceLogin(): Boolean {
    return getSharedPreferences().getBoolean(isEntrance, false)
}

fun Context.setIsReportLogin(data: Boolean) {
    val editor = getSharedPreferences().edit()
    editor.putBoolean(isReport, data)
    editor.apply()
}

fun Context.getIsReportLogin(): Boolean {
    return getSharedPreferences().getBoolean(isReport, false)
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


//fun Context.setSrWsl(data: String) {
//    val editor = getSharedPreferences().edit()
//    editor.putString(SrWsl, data)
//    editor.apply()
//}
//
//fun Context.getSrWsl(): String? {
//    return getSharedPreferences().getString(SrWsl, "")
//}


fun Context.clearPreferences() {
    val editor = getSharedPreferences().edit()
    editor.clear()
    editor.apply()
}