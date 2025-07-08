package com.surgeon.controlpanels.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.common.Constant
import com.surgeon.controlpanels.common.getIsEntranceApp
import com.surgeon.controlpanels.databinding.ActivityEntranceBinding
import com.surgeon.controlpanels.db.DbHelper
import com.surgeon.controlpanels.websocket.MyWebSocketListener
import com.surgeon.controlpanels.websocket.WebSocketEventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class Entrance : AppCompatActivity(), OnClickListener, WebSocketEventListener {

    lateinit var activity: Activity
    private lateinit var binding: ActivityEntranceBinding
    private lateinit var webSocketListener: MyWebSocketListener
    private lateinit var dbHelper: DbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityEntranceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        dbHelper = DbHelper(activity)

        init()
        initAction()
        updateData()

    }

    private fun init() {

        if (getIsEntranceApp()) {

        } else {

        }

    }


    private fun initAction() {

        binding.imgClose.setOnClickListener(this)
        binding.btnConnect.setOnClickListener(this)

    }

    private fun onClickConnect() {
        val socketUrl = binding.etSocketUrl.text.toString()
        if (socketUrl.isNotEmpty()) {
            initSocket(socketUrl)
        } else {
            binding.tvSocketStatus.text = "Please enter a valid URL"
        }
    }

    private fun initSocket(socketUrl: String) {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(socketUrl)
            .build()

        webSocketListener = MyWebSocketListener(this, client, request)
        webSocketListener.start()

    }

    private fun updateData() {
        Log.e("UpdateData", "Updated")
        val cPressure = dbHelper.getDeviceSettingValue("C_PRESSURE_1") ?: ""
        if (cPressure.isNotEmpty()) {

            try {
                val p = cPressure.toInt()
                if (p > 0) {
                    binding.tvPressure.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen1));
                } else {
                    binding.tvPressure.setTextColor(ContextCompat.getColor(activity, R.color.colorRed));
                }

            } catch (e: Exception) {
                println()
            }

            binding.tvPressure.text = "$cPressure pascal"
        }

        val dbTemp = dbHelper.getDeviceSettingValue("C_OT_TEMP") ?: ""
        val tempValue = dbTemp.takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.div(10) ?: 0.0

        binding.tvTemp.text = "${tempValue}${Constant.degreeSymbol}"
    }

    private fun light8and9OnOff(flag: Boolean) {
        dbHelper.updateDeviceSetting("S_Light_8_ON_OFF", if (flag) "1" else "0")
        dbHelper.updateDeviceSetting("S_Light_9_ON_OFF", if (flag) "1" else "0")
        sendMessage(dbHelper.getDeviceSettings())
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
                //showCustomToastLayout(activity, "Request send")
                binding.tvStatus.text = "Request send"
            } else {
                Log.e("MyWebSocketListener", "sendMessage - fails")
                //showCustomToastLayout(activity, "Request fail")
                binding.tvStatus.text = "Request fail"
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

            R.id.btnConnect -> {
                onClickConnect()
            }

        }
    }

    private fun getStatusString(status: Int): String {
        return when (status) {
            0 -> "UPCOMING"
            1 -> "IN PROGRESS"
            2 -> "COMPLETED"
            3 -> "CANCELLED"
            else -> "Unknown"
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onMessageReceived(message: String) {
        //dbHelper.updateAllDeviceSettings1(JSONObject(message).getString("frameData"))
        if (message.startsWith("{,")) {
            dbHelper.updateAllDeviceSettings1(message)

            runOnUiThread {
                binding.tvStatus.text = message
            }
        }

    }


    override fun onConnected() {
        binding.tvSocketStatus.text = "Connected"
    }


    override fun onFailure(message: String) {
        binding.tvSocketStatus.text = message
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}