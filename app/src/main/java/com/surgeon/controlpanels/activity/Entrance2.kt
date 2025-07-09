package com.surgeon.controlpanels.activity

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.common.Constant
import com.surgeon.controlpanels.common.SeekArc
import com.surgeon.controlpanels.common.SeekArc.OnSeekArcChangeListener
import com.surgeon.controlpanels.common.convertHumidity
import com.surgeon.controlpanels.common.getEntranceAppBaseUrl
import com.surgeon.controlpanels.common.getGradient
import com.surgeon.controlpanels.common.getIsEntranceApp
import com.surgeon.controlpanels.common.getIsFirstRhToday
import com.surgeon.controlpanels.common.setEntranceAppBaseUrl
import com.surgeon.controlpanels.common.setIsFirstRhToday
import com.surgeon.controlpanels.common.showCustomToastLayout
import com.surgeon.controlpanels.databinding.ActivityEntrance2Binding
import com.surgeon.controlpanels.databinding.ActivityEntranceBinding
import com.surgeon.controlpanels.databinding.DialogSocketSettingsBinding
import com.surgeon.controlpanels.db.DbHelper
import com.surgeon.controlpanels.dialog.AutoDismissDialog
import com.surgeon.controlpanels.websocket.MyWebSocketListener
import com.surgeon.controlpanels.websocket.WebSocketEventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date


class Entrance2 : AppCompatActivity(), OnClickListener, WebSocketEventListener {

    lateinit var activity: Activity
    private lateinit var binding: ActivityEntrance2Binding
    private lateinit var webSocketListener: MyWebSocketListener
    private lateinit var dbHelper: DbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityEntrance2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        dbHelper = DbHelper(activity)

        init()
        initAction()
        onSwitchListener()
//        updateData()

    }

    private fun init() {


        val sdf = SimpleDateFormat("dd MMM yyyy")
        val currentDate = sdf.format(Date())
        binding.tvTodayDate.text = currentDate


    }


    private fun initAction() {

//        binding.imgClose.setOnClickListener(this)
        binding.imgSetting.setOnClickListener(this)

    }

//    private fun onClickConnect() {
//        val socketUrl = binding.etSocketUrl.text.toString()
//        if (socketUrl.isNotEmpty()) {
//            initSocket(socketUrl)
//        } else {
////            binding.tvSocketStatus.text = "Please enter a valid URL"
//            showCustomToastLayout(activity, "Please enter a valid URL")
//        }
//    }

    private fun initSocket(socketUrl: String) {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(socketUrl)
            .build()

        webSocketListener = MyWebSocketListener(this, client, request)
        webSocketListener.start()

    }

    private fun sendMessage(message: String) {

        val r = JSONObject()
        r.put("frameData", message)
        r.put("receivedFrom", "app")

        try {
            val flag = webSocketListener.sendRequest(r.toString())
            Log.e("MyWebSocketListener", "sendMessage - $r")
            if (flag) {
                Log.e("MyWebSocketListener", "sendMessage - $r")
                showCustomToastLayout(activity, "Request send")
//                binding.tvStatus.text = "Request send"
            } else {
                Log.e("MyWebSocketListener", "sendMessage - fails")
                showCustomToastLayout(activity, "Request fail")
//                binding.tvStatus.text = "Request fail"
            }
        } catch (e: Exception) {
            println()
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgClose -> {
                finish()
            }

            R.id.imgSetting -> {
                showSocketSettingsDialog()
            }

        }
    }
//
//    private fun getStatusString(status: Int): String {
//        return when (status) {
//            0 -> "UPCOMING"
//            1 -> "IN PROGRESS"
//            2 -> "COMPLETED"
//            3 -> "CANCELLED"
//            else -> "Unknown"
//        }
//    }

    private fun onSwitchListener() {
        binding.swOnOffDefumigation.setOnCheckedChangeListener { _, isChecked ->
            defumigationModeOnOff(isChecked)
        }
        binding.swOnOffNight.setOnCheckedChangeListener { _, isChecked ->
            nightModeOnOff(isChecked)
        }
        binding.swOnOffSystem.setOnCheckedChangeListener { _, isChecked ->
            systemModeOnOff(isChecked)
        }
    }

//    private fun light8and9OnOff(flag: Boolean) {
//        dbHelper.updateDeviceSetting("S_Light_8_ON_OFF", if (flag) "1" else "0")
//        dbHelper.updateDeviceSetting("S_Light_9_ON_OFF", if (flag) "1" else "0")
//        sendMessage(dbHelper.getDeviceSettings())
//    }

    private fun defumigationModeOnOff(flag: Boolean) {
        dbHelper.updateDeviceSetting("S_Light_9_ON_OFF", if (flag) "1" else "0")
        sendMessage(dbHelper.getDeviceSettings())
    }

    private fun nightModeOnOff(flag: Boolean) {
        dbHelper.updateDeviceSetting("S_Light_8_ON_OFF", if (flag) "1" else "0")
        sendMessage(dbHelper.getDeviceSettings())
    }

    private fun systemModeOnOff(flag: Boolean) {
        dbHelper.updateDeviceSetting("S_Light_10_ON_OFF", if (flag) "1" else "0")
        sendMessage(dbHelper.getDeviceSettings())
    }


    private fun updateData() {
        Log.e("UpdateData", "Updated")
        val cPressureSignBit = dbHelper.getDeviceSettingValue("C_PRESSURE_1_SIGN_BIT") ?: ""
        if (cPressureSignBit.isNotEmpty()) {

            try {
                val p = cPressureSignBit.toInt()
                if (p == 1) {
                    binding.tvPressure.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen1));
                } else {
                    binding.tvPressure.setTextColor(ContextCompat.getColor(activity, R.color.colorRed));
                }

            } catch (e: Exception) {
                println()
            }

        }

        val cPressure = dbHelper.getDeviceSettingValue("C_PRESSURE_1") ?: ""
        binding.tvPressure.text = "${pasacalFormate(cPressure)} pascal"

        val dbTemp = dbHelper.getDeviceSettingValue("C_OT_TEMP") ?: ""
        val tempValue = dbTemp.takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.div(10) ?: 0.0
        binding.tvTemp.text = "${tempValue}${Constant.degreeSymbol}"

        val dbRH = dbHelper.getDeviceSettingValue("C_RH") ?: ""
        val rhValue = dbRH.takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.div(10) ?: 0.0
        binding.tvRH.text = "${rhValue}%"

        val nightMode = dbHelper.getDeviceSettingValue("S_Light_8_ON_OFF") ?: ""
        binding.swOnOffNight.isChecked = nightMode == "1"

        val defumigationMode = dbHelper.getDeviceSettingValue("S_Light_9_ON_OFF") ?: ""
        binding.swOnOffDefumigation.isChecked = defumigationMode == "1"

        val systemMode = dbHelper.getDeviceSettingValue("S_Light_10_ON_OFF") ?: ""
        binding.swOnOffSystem.isChecked = systemMode == "1"

    }

    private fun pasacalFormate(input: String): String {
        if (input.length >= 2) {
            val formatted = input.dropLast(1).padStart(2, '0') + "." + input.last()
            Log.d("FormattedValue", formatted)
            return formatted
        }
        return input
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onMessageReceived(message: String) {
        try {
            //dbHelper.updateAllDeviceSettings1(JSONObject(message).getString("frameData"))
            if (message.startsWith("{,")) {
                dbHelper.updateAllDeviceSettings1(message)

                runOnUiThread {
                    updateData()
                }
            }
        } catch (e: Exception) {
            println()
        }

    }


    private fun showSocketSettingsDialog() {
        val d = Dialog(activity)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(true)
        d.setContentView(R.layout.dialog_socket_settings)

        val binding = DialogSocketSettingsBinding.inflate(layoutInflater)
        d.setContentView(binding.root)

        d.window?.apply {
            attributes.windowAnimations = R.style.DialogAnimation
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (Resources.getSystem().displayMetrics.widthPixels * 0.6).toInt()
            //val height = (Resources.getSystem().displayMetrics.heightPixels * 0.85).toInt()
            setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        }


        if (getEntranceAppBaseUrl().isNotEmpty()) {
            binding.etSocketUrl.setText(getEntranceAppBaseUrl())
        }

        binding.imgClose.setOnClickListener {
            d.dismiss()
        }

        // Set click listeners
        binding.btnConnect.setOnClickListener {
            val socketUrl = binding.etSocketUrl.text.toString()
            if (socketUrl.isNotEmpty()) {
                binding.etSocketUrl.setText(socketUrl)
                initSocket(socketUrl)
                setEntranceAppBaseUrl(socketUrl)
                d.dismiss()
            } else {
                showCustomToastLayout(activity, "Please enter a valid URL")
            }
        }

        d.show()

    }

    override fun onConnected() {
        runOnUiThread {
//        binding.tvSocketStatus.text = "Connected"
            showCustomToastLayout(activity, "Connected")
        }
    }


    override fun onFailure(message: String) {
        runOnUiThread {
//        binding.tvSocketStatus.text = message
            showCustomToastLayout(activity, message)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}