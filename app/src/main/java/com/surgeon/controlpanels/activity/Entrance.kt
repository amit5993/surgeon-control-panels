package com.surgeon.controlpanels.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.common.Constant
import com.surgeon.controlpanels.common.OTStatus
import com.surgeon.controlpanels.common.TimeDifferenceUpdater
import com.surgeon.controlpanels.common.getCurrentMode
import com.surgeon.controlpanels.common.getIsEntranceLogin
import com.surgeon.controlpanels.common.getOTModeLabel
import com.surgeon.controlpanels.common.showCustomToastLayout
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
    private var minutesValue = 0
    private lateinit var dbHelper: DbHelper
    private var countdownTimer: CountDownTimer? = null
    private var timeUpdater: TimeDifferenceUpdater? = null
    private var timeLeftInMillis: Long = 180000

    private val mqttMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val message = it.getStringExtra("message") ?: ""
                updateData()
            }
        }
    }

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

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mqttMessageReceiver, IntentFilter("com.surgeon.controlpanels.WEB_SOCKET_MESSAGE")
        )

        init()
        initAction()
        initSocket()
        //initMode()
        //initSpinner()
        updateData()

        //startCountdown()

        Log.e("EntranceActivity", getCurrentMode().toString())

    }

    private fun init() {

        binding.btnStart.visibility = View.GONE
        binding.btnSkip.visibility = View.GONE

        if (getIsEntranceLogin()) {
            binding.btnLogout.visibility = View.VISIBLE
        } else {
            binding.btnLogout.visibility = View.GONE
        }

    }


    private fun initAction() {

        binding.imgClose.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
        binding.btnSkip.setOnClickListener(this)

    }

    private fun initSocket() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(Constant.SOCKET_URL)
            .build()

        webSocketListener = MyWebSocketListener(this, client, request)
        webSocketListener.start()
    }

    private fun showMainView(statusText: String) {
        binding.llMain.visibility = View.VISIBLE
        binding.btnSkip.visibility = View.GONE
        binding.tvOTStatus.visibility = View.VISIBLE
        binding.tvOTStatus.text = statusText
        binding.llSurgeryDetails.visibility = View.GONE
        binding.btnStart.text = getOTModeLabel(activity)
        binding.btnStart.visibility = View.VISIBLE
    }

    private fun showCleaningMode() {
        binding.llMain.visibility = View.VISIBLE
        binding.tvOTStatus.visibility = View.VISIBLE
        binding.tvOTStatus.text =
            "Operating Theatre is currently in Cleaning Mode. Please wait until cleaning is complete before initiating any new procedures."
        binding.llMinutes.visibility = View.GONE
        binding.btnStart.visibility = View.GONE
        binding.btnSkip.visibility = View.GONE
        binding.llSurgeryDetails.visibility = View.GONE
    }

    private fun showCountdownMode(currentMode: String) {
        binding.btnSkip.visibility = View.GONE
        binding.llMain.visibility = View.VISIBLE
        binding.tvOTStatus.visibility = View.VISIBLE
        binding.tvOTStatus.text = "Countdown for ${currentMode.capitalize()} completion"
        binding.llMinutes.visibility = View.GONE
        binding.btnStart.visibility = View.GONE
        binding.llSurgeryDetails.visibility = View.GONE
    }

    private fun showDefaultMode() {
        binding.llMain.visibility = View.VISIBLE
        binding.btnSkip.visibility = View.GONE
        binding.tvOTStatus.visibility = View.GONE
        binding.llSurgeryDetails.visibility = View.GONE
        binding.btnStart.text = getOTModeLabel(activity)
        binding.btnStart.visibility = View.VISIBLE
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
                showCustomToastLayout(activity, "Request send")
            } else {
                Log.e("MyWebSocketListener", "sendMessage - fails")
                showCustomToastLayout(activity, "Request fail")
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mqttMessageReceiver)
        super.onDestroy()

        timeUpdater?.stop()

    }


    override fun onMessageReceived(message: String) {
        dbHelper.updateAllDeviceSettings1(JSONObject(message).getString("frameData"))
    }

    override fun onBackPressed() {
        if (getCurrentMode()!! == OTStatus.DEFUMIGATION
        //|| getCurrentMode()!! == OTStatus.DISINFECTION
        ) {
            showCustomToastLayout(activity, "You can close the app before the process finishes.")
        } else {
            super.onBackPressed()
        }


    }

}