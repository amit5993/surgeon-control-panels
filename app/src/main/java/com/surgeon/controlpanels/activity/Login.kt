package com.surgeon.controlpanels.activity

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.surgeon.controlpanels.common.Constant
import com.surgeon.controlpanels.common.closeLoader
import com.surgeon.controlpanels.common.getIsEntranceApp
import com.surgeon.controlpanels.common.openLoader
import com.surgeon.controlpanels.common.setIsEntranceApp
import com.surgeon.controlpanels.common.setOTDetails
import com.surgeon.controlpanels.common.showCustomToastLayout
import com.surgeon.controlpanels.databinding.ActivityLoginBinding
import com.surgeon.controlpanels.model.setup.SetupResponse
import org.json.JSONException
import org.json.JSONObject


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        init()

        binding.btnSubmit.setOnClickListener {
            onTapSubmit()
        }

    }

    private fun init() {

//        binding.etSerialNumber.setSingleLine(true)
//        binding.etSerialNumber.imeOptions = EditorInfo.IME_ACTION_NEXT
//        binding.etUniqueCode.setSingleLine(true)
//        binding.etUniqueCode.imeOptions = EditorInfo.IME_ACTION_DONE


        val appTypes = listOf("main", "entrance")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, appTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAppType.adapter = adapter


    }

    @SuppressLint("HardwareIds")
    private fun onTapSubmit() {

//        if (binding.etSerialNumber.text.toString().trim().isEmpty()) {
//            showCustomToastLayout(activity, "Please enter serial number")
//            return
//        }
        if (binding.etUniqueCode.text.toString().trim().isEmpty()) {
            showCustomToastLayout(activity, "Please enter unique code")
            return
        }
        val selectedAppType = binding.spinnerAppType.selectedItem.toString()
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        val request = JSONObject()
        request.put("device_id", androidId)
        request.put("unique_code", binding.etUniqueCode.text.toString().trim())
        request.put("app_type", selectedAppType)

        setUpDevice(request)
    }

    private fun setUpDevice(request: JSONObject) {

        openLoader()

        AndroidNetworking.post(Constant.BASE_URL)
            .addJSONObjectBody(request)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val status = response.optInt("status")
                        if (status == 200) {
                            response.optJSONObject("result")?.let { result ->
                                setOTDetails(result.toString())

                                val setupResponse = Gson().fromJson(result.toString(), SetupResponse::class.java)
                                setIsEntranceApp(setupResponse.app_type != "main")
                                nextIntent()
                            }
                        } else {
                            showCustomToastLayout(activity, response.optString("msg"))
                        }

                        //showCustomToastLayout(activity, data)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        showCustomToastLayout(activity, e.message)
                    }

                    closeLoader()
                }

                override fun onError(error: ANError) {
                    showCustomToastLayout(activity, "API Error : ${error.message} error code - ${error.errorCode}")
                    error.printStackTrace()
                    closeLoader()
                }
            })
    }

    private fun nextIntent() {
        if (getIsEntranceApp()) {
            startActivity(Intent(this, Entrance2::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

}