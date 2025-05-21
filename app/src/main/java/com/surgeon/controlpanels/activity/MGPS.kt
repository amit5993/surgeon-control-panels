package com.surgeon.controlpanels.activity

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.surgeon.controlpanels.adapter.MGPSListAdapter
import com.surgeon.controlpanels.databinding.ActivityMgpsBinding
import com.surgeon.controlpanels.db.DbHelper
import com.surgeon.controlpanels.model.dbsokect.DeviceSettingModel
import com.surgeon.controlpanels.viewmodel.DeviceSettingViewModel


class MGPS : AppCompatActivity() {

    lateinit var activity: Activity
    private lateinit var binding: ActivityMgpsBinding

    //    val list: ArrayList<ParameterData> = ArrayList()
    var list: ArrayList<DeviceSettingModel> = ArrayList()
    private lateinit var adapter: MGPSListAdapter
    private lateinit var dbHelper: DbHelper
    private lateinit var deviceSettingViewModel: DeviceSettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityMgpsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        dbHelper = DbHelper(activity)
        deviceSettingViewModel = ViewModelProvider(this)[DeviceSettingViewModel::class.java]

        init()

    }

    private fun init() {
        binding.imgClose.setOnClickListener {
            finish()
        }

        list = dbHelper.getDeviceSettingsByType(1)

//        var listData : ArrayList<MGPSData> = ArrayList()
//        listData = AllList.initTMGPSList()

        adapter = MGPSListAdapter(activity, list)
        binding.rvGas.layoutManager = GridLayoutManager(this, 3)
        adapter.registerInterface(object : MGPSListAdapter.ClickPatientInterface {
            override fun clickMGPS(dataS: DeviceSettingModel, position: Int) {

            }
        })
        binding.rvGas.adapter = adapter

        deviceSettingViewModel.deviceSettings.observe(this) { it ->
            list = dbHelper.getDeviceSettingsByType(1)
            adapter.updateData(list)
        }

    }
}