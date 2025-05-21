package com.surgeon.controlpanels.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.surgeon.controlpanels.db.DbHelper
import com.surgeon.controlpanels.model.dbsokect.DeviceSettingModel
import org.json.JSONObject

class DeviceSettingViewModel(application: Application) : AndroidViewModel(application) {

    private val _deviceSettings = MutableLiveData<List<DeviceSettingModel>>()  // LiveData for UI updates
    val deviceSettings: LiveData<List<DeviceSettingModel>> = _deviceSettings

    private val dbHelper = DbHelper(application)

    // Update database and refresh LiveData
    fun updateAllDeviceSettings(message: String) {
        // Fetch the updated data and post it
        dbHelper.updateAllDeviceSettings1(JSONObject(message).getString("frameData"))
        _deviceSettings.postValue(ArrayList())
    }

}
