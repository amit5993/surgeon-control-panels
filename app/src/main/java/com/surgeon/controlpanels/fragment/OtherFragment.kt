package com.surgeon.controlpanels.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.common.Constant
import com.surgeon.controlpanels.common.showCustomToastLayout
import com.surgeon.controlpanels.databinding.FragmentOtherBinding
import com.surgeon.controlpanels.db.DbHelper
import com.surgeon.controlpanels.websocket.MyWebSocketListener
import com.surgeon.controlpanels.websocket.WebSocketEventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class OtherFragment : Fragment() {

    private lateinit var binding: FragmentOtherBinding
    private lateinit var activity: Activity
    private lateinit var dbHelper: DbHelper
    private lateinit var webSocketListener: MyWebSocketListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtherBinding.inflate(inflater, container, false)
        activity = requireActivity()
        dbHelper = DbHelper(activity)

        init()
//        initSocket()

        return binding.root
    }

    fun init() {
        val acValue = dbHelper.getDeviceSettingValue("S_Light_9_ON_OFF") ?: "0"
        val systemValue = dbHelper.getDeviceSettingValue("S_Light_10_ON_OFF") ?: "0"
        val hepaValue = dbHelper.getDeviceSettingValue("F_Sensor_8_FAULT_BIT") ?: "0"

        binding.swOnOffAc.isChecked = acValue == "1"
        binding.swOnOffSystem.isChecked = systemValue == "1"

        val (hepaText, hepaColor) = when (hepaValue) {
            "0" -> "HEALTHY" to R.color.colorGreen
            else -> "UNHEALTHY" to R.color.colorRed
        }

        binding.tvHepa.apply {
            text = hepaText
            setTextColor(ContextCompat.getColor(activity, hepaColor))
        }

        binding.swOnOffAc.setOnCheckedChangeListener { _, isChecked ->
            dbHelper.updateDeviceSetting("S_Light_9_ON_OFF", if (isChecked) "1" else "0")
            sendMessage(dbHelper.getDeviceSettings())
        }

//        binding.swOnOffSystem.setOnCheckedChangeListener { _, isChecked ->
//            dbHelper.updateDeviceSetting("S_Light_10_ON_OFF", if (isChecked) "1" else "0")
//            sendMessage(dbHelper.getDeviceSettings())
//        }

        binding.swOnOffSystem.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                // Show confirmation dialog when turning off
                showConfirmationDialog {
                    // This block executes when user confirms
                    dbHelper.updateDeviceSetting("S_Light_10_ON_OFF", "0")
                    sendMessage(dbHelper.getDeviceSettings())
                }
            } else {
                // Direct action when turning on (no confirmation needed)
                dbHelper.updateDeviceSetting("S_Light_10_ON_OFF", "1")
                sendMessage(dbHelper.getDeviceSettings())
            }
        }
    }



//    private fun initSocket() {
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url(Constant.SOCKET_URL)
//            .build()
//
//        webSocketListener = MyWebSocketListener(this, client, request)
//        webSocketListener.start()
//    }

    private fun sendMessage(message: String) {

        //val finalMsg = message.replace("\"","").replace("\n", "").replace(" ", "")
//            val finalMsg1 = "{frameData: '{,SR_WSL:200001,S_Sensor_1_NO_NC_SETTING:1,S_Sensor_2_NO_NC_SETTING:1,S_Sensor_3_NO_NC_SETTING:1,S_Sensor_4_NO_NC_SETTING:1,S_Sensor_5_NO_NC_SETTING:1,S_Sensor_6_NO_NC_SETTING:1,S_Sensor_7_NO_NC_SETTING:1,S_Sensor_8_NO_NC_SETTING:1,S_Sensor_9_NO_NC_SETTING:1,S_Sensor_10_NO_NC_SETTING:1,S_Light_1_ON_OFF:0,S_Light_2_ON_OFF:0,S_Light_3_ON_OFF:1,S_Light_4_ON_OFF:1,S_Light_5_ON_OFF:1,S_Light_6_ON_OFF:1,S_Light_7_ON_OFF:1,S_Light_8_ON_OFF:1,S_Light_9_ON_OFF:1,S_Light_10_ON_OFF:1,S_Light_1_Intensity:010,S_Light_2_Intensity:020,S_Light_3_Intensity:030,S_Light_4_Intensity:090,S_Light_5_Intensity:020,S_Light_6_Intensity:060,S_Light_7_Intensity:070,S_Light_8_Intensity:080,S_Light_9_Intensity:090,S_Light_10_Intensity:050,S_IOT_TIMER:0030,S_TEMP_SETPT:200,S_RH_SETPT:750}',  receivedFrom: 'app'}"
//        val finalMsg1 = "{frameData: '$message',  receivedFrom: 'app'}"

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

    private fun showConfirmationDialog(onConfirm: () -> Unit) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirm Action")
            .setMessage("Are you sure you want to turn off the system?")
            .setPositiveButton("Yes") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("No") { _, _ ->
                // Revert the switch state without triggering the listener
                binding.swOnOffSystem.isChecked = true
            }
            .setCancelable(false)
            .show()
    }

//    override fun onMessageReceived(message: String) {
//        dbHelper.updateAllDeviceSettings1(JSONObject(message).getString("frameData"))
//        //update db and notify all list
////        deviceSettingViewModel.updateAllDeviceSettings(message)
////        updateTempRhInMenu()
////
////        // Create and send broadcast
////        val intent = Intent("com.surgeon.controlpanels.WEB_SOCKET_MESSAGE")
////        intent.putExtra("message", message)
////        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//    }


}