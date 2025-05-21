package com.surgeon.controlpanels.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.surgeon.controlpanels.model.dbsokect.DeviceSettingModel
import com.surgeon.controlpanels.model.dbsokect.LightDataModel
import com.surgeon.controlpanels.model.theme.ThemeModel
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DbHelper(val context: Context) :
    SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    val TABLE_THEME = "table_theme"


    //val context: Context? = null
    var db: SQLiteDatabase = writableDatabase

    companion object {
        private const val DATABASE_NAME = "dcp_db.sqlite"
        private const val DATABASE_VERSION = 1
    }

    private fun fetchDataFromColumn(cursorCategory: Cursor, keyName: String): String {
        return cursorCategory.getString(cursorCategory.getColumnIndex(keyName))
    }

    private fun currentTime(): String? {

        val calendar = Calendar.getInstance()
        //System.out.println("Current Date = " + calendar.time)
//        calendar.add(Calendar.SECOND, -2)
        //System.out.println("Updated Date = " + calendar.time)

        val sdf = SimpleDateFormat("HH:mm:ss")

        return sdf.format(calendar.time)
        //return sdf.format(Date())
    }

    /**
     *  update theme Data
     */
    fun insertOrderData(orderId: String, jsonObject: String): Boolean {
        val newValues = ContentValues()
        newValues.put("order_id", orderId)
        newValues.put("json", jsonObject)
        newValues.put("status", 0)
        val i = db.insert(TABLE_THEME, null, newValues)
        Log.d("DB_TAG", "Log$i")
        return i > 0
    }

    /**
     *  update theme Data
     */
    fun updateTheme(data: ThemeModel, id: Int) {

        val newValues = ContentValues()
//        newValues.put("id", data.id)
        newValues.put("t_type", data.type)
        newValues.put("t_color1", data.color1)
        newValues.put("t_color2", data.color2)
        newValues.put("t_font_color", data.fontColor)

        val update = db.update(TABLE_THEME, newValues, "t_id=$id", null)
        Log.d("updateTheme", "Log$update")

    }

    @SuppressLint("Range")
    fun getTheme(type: String): ThemeModel {
        var returnData: ThemeModel = ThemeModel()

        try {
            val c = db.rawQuery(
                "SELECT * FROM $TABLE_THEME WHERE t_type  = '$type'",
                null
            )
            while (c.moveToNext()) {
                returnData = ThemeModel(
                    c.getInt(c.getColumnIndex("t_id")),
                    c.getString(c.getColumnIndex("t_type")),
                    c.getString(c.getColumnIndex("t_color1")),
                    c.getString(c.getColumnIndex("t_color2")),
                    c.getString(c.getColumnIndex("t_font_color")),
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return returnData
    }


    fun getDeviceSettingsByType(type: Int): ArrayList<DeviceSettingModel> {
        val returnData: ArrayList<DeviceSettingModel> = ArrayList()

        val query = "SELECT * FROM device_setting WHERE type = ?"
        val cursor = db.rawQuery(query, arrayOf(type.toString()))

        //val returnData = mutableMapOf<String, Any>()

        try {
            while (cursor.moveToNext()) {

                val d = DeviceSettingModel()
                d.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                d.key = cursor.getString(cursor.getColumnIndexOrThrow("key"))
                d.value = cursor.getString(cursor.getColumnIndexOrThrow("value"))
                d.type = cursor.getInt(cursor.getColumnIndexOrThrow("type"))
                d.name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                returnData.add(d)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }

        return returnData
    }

    fun getAllLightData(): List<LightDataModel> {
        val lightSettingsList = ArrayList<LightDataModel>()

        try {
            for (i in 1..7) {
                val onOffKey = "S_Light_${i}_ON_OFF"
                val intensityKey = "S_Light_${i}_Intensity"

                val query = """
                SELECT value, (
                    SELECT value FROM device_setting WHERE key = '$intensityKey'
                ) AS intensity_value, name
                FROM device_setting
                WHERE key = '$onOffKey'
            """.trimIndent()

                val cursor = db.rawQuery(query, null)

                if (cursor.moveToFirst()) {
                    val onOffValue = cursor.getString(0) ?: "N/A"
                    val intensityValue = cursor.getString(1)?.toIntOrNull() ?: 0
                    val name = cursor.getString(2) ?: "Unknown" // Get name from DB

                    lightSettingsList.add(LightDataModel(onOffKey, onOffValue, intensityKey, intensityValue, name))
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Error fetching light settings: ${e.message}")
        } finally {
        }

        return lightSettingsList
    }

    fun updateLightSetting(position: Int, onOffValue: String? = null, intensityValue: Int? = null) {
        val db = this.writableDatabase

        try {
            if (onOffValue != null) {
                val onOffKey = "S_Light_${position}_ON_OFF"
                val contentValuesOnOff = ContentValues().apply {
                    put("value", onOffValue.toString())
                }
                db.update("device_setting", contentValuesOnOff, "key = ?", arrayOf(onOffKey))
                Log.d("DB_UPDATE", "Updated Light $position: ON_OFF = $onOffValue")
            }

            if (intensityValue != null) {
                val intensityKey = "S_Light_${position}_Intensity"
                val formattedIntensity = String.format("%03d", intensityValue) // Ensure "050" format
                val contentValuesIntensity = ContentValues().apply {
                    put("value", formattedIntensity)
                }
                db.update("device_setting", contentValuesIntensity, "key = ?", arrayOf(intensityKey))
                Log.d("DB_UPDATE", "Updated Light $position: Intensity = $formattedIntensity")
            }
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Error updating light setting: ${e.message}")
        } finally {
        }
    }

    fun updateDeviceSetting(key: String, newValue: String) {

        val contentValues = ContentValues().apply {
            put("value", newValue)
        }

        val whereClause = "key = ?"
        val whereArgs = arrayOf(key)

        val rowsAffected = db.update("device_setting", contentValues, whereClause, whereArgs)

        if (rowsAffected > 0) {
            Log.d("DB_UPDATE", "Updated key: $key with value: $newValue")
        } else {
            Log.d("DB_UPDATE", "No rows updated for key: $key")
        }

    }

    fun updateDeviceSettingName(key: String, newName: String) {

        val contentValues = ContentValues().apply {
            put("name", newName)
        }

        val whereClause = "key = ?"
        val whereArgs = arrayOf(key)

        val rowsAffected = db.update("device_setting", contentValues, whereClause, whereArgs)

        if (rowsAffected > 0) {
            Log.d("DB_UPDATE", "Updated key: $key with value: $newName")
        } else {
            Log.d("DB_UPDATE", "No rows updated for key: $key")
        }

    }

    fun getDeviceSettingValue(key: String): String {
        val cursor = db.query(
            "device_setting",                // Table name
            arrayOf("value"),                // Columns to retrieve
            "key = ?",                       // Selection
            arrayOf(key),                    // Selection arguments
            null, null, null
        )
        val value = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow("value"))
        } else {
            ""
        }
        cursor.close()
        return value
    }


    fun getDeviceSettingName(key: String): String {
        val cursor = db.query(
            "device_setting",
            arrayOf("name"),
            "key = ?",
            arrayOf(key),
            null, null, null
        )
        val value = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow("name"))
        } else {
            ""
        }
        cursor.close()
        return value ?: ""
    }

    /**
     * type
     * 1 = MGPS
     * 2 = Light on/off
     * 3 = Light Intensity
     * 4 = Temp
     * 5 = RH
     * 6 = S_IOT_TIMER
     */
    fun updateAllDeviceSettings1(rawString: String) {
        try {
            db.beginTransaction() // Start transaction for batch update

            // ✅ Step 1: Parse the raw string into key-value pairs
            val keyValuePairs = rawString
                .removePrefix("{") // Remove starting {
                .removeSuffix("}") // Remove ending }
                .split(",") // Split by comma
                .mapNotNull {
                    val parts = it.split(":")
                    if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
                }

            // ✅ Step 2: Update or Insert into DB
            for ((key, value) in keyValuePairs) {
                val contentValues = ContentValues().apply {
                    put("value", value)
                }

                val whereClause = "key = ?"
                val whereArgs = arrayOf(key)

                val rowsAffected = db.update("device_setting", contentValues, whereClause, whereArgs)

                if (rowsAffected == 0) {
                    // Insert new key if not exists
                    Log.d("DB_UPDATE", "No row found for key: $key, inserting new entry.")
                    val insertValues = ContentValues().apply {
                        put("key", key)
                        put("value", value)
                        put("type", -1)
                        put("name", "")
                        put("order_ds", -1)
                    }
                    db.insert("device_setting", null, insertValues)
                }

                Log.d("DB_UPDATE", "Updated key: $key with value: $value")
            }

            db.setTransactionSuccessful() // Commit transaction
        } catch (e: Exception) {
            Log.e("DB_UPDATE", "Error updating database: ${e.message}")
        } finally {
            db.endTransaction() // End transaction
        }
    }


    fun getDeviceSettings(): String {
        val cursor = db.rawQuery("SELECT key, value, type FROM device_setting WHERE order_ds != -1 ORDER BY order_ds", null)
        val keyValueList = mutableListOf<String>()

        while (cursor.moveToNext()) {
//            val key = cursor.getString(0) // key_name
//            val value = cursor.getString(1) // key_value

            val key = cursor.getString(cursor.getColumnIndexOrThrow("key"))
            val value = cursor.getString(cursor.getColumnIndexOrThrow("value"))
            val type = cursor.getInt(cursor.getColumnIndexOrThrow("type"))

            // Dynamically detect and convert to Integer or keep as String
            val formattedValue = when {
                (type == 3 || type == 4 || type == 5) && value.toIntOrNull() != null ->
                    String.format("%03d", value.toInt())

                type == 6 -> String.format("%04d", value.toInt())
                value.toIntOrNull() != null -> value.toInt()
                //value.toDoubleOrNull() != null -> value.toDouble()
                else -> value  // Keep as String
            }

//            if (key == "S_Sensor_10_NO_NC_SETTING") {
//                keyValueList.add("$key:0")
//            } else {
//                keyValueList.add("$key:$formattedValue")
//            }

            keyValueList.add("$key:$formattedValue")

        }
        cursor.close()

        return "{,${keyValueList.joinToString(",")}}" // Convert back to original format
    }


    fun deleteAllData() {

        val tTheme = "DELETE FROM $TABLE_THEME"

        db.execSQL(tTheme)
    }


    //json format
//    fun updateAllDeviceSettings(jsonString: String) {
//        try {
//            val correctedJson = jsonString.trim().removePrefix("{,").removePrefix("{")
//            val jsonObject = JSONObject(correctedJson)
//
//            db.beginTransaction() // Start transaction for batch update
//
//            for (key in jsonObject.keys()) {
//                val value = jsonObject.getString(key) // Get value as string
//
//                val contentValues = ContentValues().apply {
//                    put("value", value)
//                }
//
//                val whereClause = "key = ?"
//                val whereArgs = arrayOf(key)
//
//                val rowsAffected = db.update("device_setting", contentValues, whereClause, whereArgs)
//
//                if (rowsAffected > 0) {
//                    Log.d("DB_UPDATE", "Updated key: $key with value: $value")
//                } else {
//                    Log.d("DB_UPDATE", "No row found for key: $key, inserting new entry.")
//                }
//            }
//
//            db.setTransactionSuccessful() // Commit transaction
//        } catch (e: Exception) {
//            Log.e("DB_UPDATE", "Error updating database: ${e.message}")
//        } finally {
//            db.endTransaction() // End transaction
//        }
//    }
}