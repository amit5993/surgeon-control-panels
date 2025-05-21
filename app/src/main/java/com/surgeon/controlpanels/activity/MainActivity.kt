package com.surgeon.controlpanels.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pusher.client.Pusher
import com.pusher.client.channel.Channel
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.adapter.LightAdapter
import com.surgeon.controlpanels.adapter.MusicAdapter
import com.surgeon.controlpanels.adapter.StopwatchAdapter
import com.surgeon.controlpanels.adapter.ViewPagerMenuAdapter
import com.surgeon.controlpanels.common.AllList
import com.surgeon.controlpanels.common.Constant
import com.surgeon.controlpanels.common.OTStatus
import com.surgeon.controlpanels.common.SeekArc
import com.surgeon.controlpanels.common.SeekArc.OnSeekArcChangeListener
import com.surgeon.controlpanels.common.Stopwatch
import com.surgeon.controlpanels.common.Utils
import com.surgeon.controlpanels.common.convertHumidity
import com.surgeon.controlpanels.common.formatStopwatchTime
import com.surgeon.controlpanels.common.getCurrentMode
import com.surgeon.controlpanels.common.getGradient
import com.surgeon.controlpanels.common.getGradientNormal
import com.surgeon.controlpanels.common.getIsFirstRhToday
import com.surgeon.controlpanels.common.getIsFirstTempToday
import com.surgeon.controlpanels.common.getSound
import com.surgeon.controlpanels.common.setIsFirstRhToday
import com.surgeon.controlpanels.common.setIsFirstTempToday
import com.surgeon.controlpanels.common.setSound
import com.surgeon.controlpanels.common.showCustomToastLayout
import com.surgeon.controlpanels.databinding.ActivityMainBinding
import com.surgeon.controlpanels.databinding.DialogAlertBinding
import com.surgeon.controlpanels.databinding.DialogGasBinding
import com.surgeon.controlpanels.databinding.DialogHumidityBinding
import com.surgeon.controlpanels.databinding.DialogLightBinding
import com.surgeon.controlpanels.databinding.DialogMusicBinding
import com.surgeon.controlpanels.databinding.DialogTempBinding
import com.surgeon.controlpanels.databinding.DialogTimerBinding
import com.surgeon.controlpanels.db.DbHelper
import com.surgeon.controlpanels.dialog.AutoDismissDialog
import com.surgeon.controlpanels.model.CallModel
import com.surgeon.controlpanels.model.LapModel
import com.surgeon.controlpanels.model.MenuModel
import com.surgeon.controlpanels.model.dbsokect.LightDataModel
import com.surgeon.controlpanels.viewmodel.DeviceSettingViewModel
import com.surgeon.controlpanels.websocket.MyWebSocketListener
import com.surgeon.controlpanels.websocket.WebSocketEventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity(), WebSocketEventListener, View.OnClickListener, ViewPagerMenuAdapter.ClickMenuInterfaceToPager {

    private lateinit var binding: ActivityMainBinding
    private lateinit var activity: Activity
    private var menuList: ArrayList<ArrayList<MenuModel>> = ArrayList()
    private var lapList = ArrayList<LapModel>()
    lateinit var viewPagerMenuAdapter: ViewPagerMenuAdapter
    private var currentTemp = 0.0
    private var seekBarTemp = 0.0

    private var currentHumidity = 0.0
    private var seekBarHumidity = 0.0

    private var patientId = 0
    private var patientIdForCleaning = 0

    private var mediaPlayer: MediaPlayer? = null
    private var musicList: List<String> = ArrayList()
    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var dbHelper: DbHelper


    var lightList: List<LightDataModel> = ArrayList()

    lateinit var webSocketListener: MyWebSocketListener

    private lateinit var deviceSettingViewModel: DeviceSettingViewModel

    private lateinit var pusher: Pusher
    private lateinit var channel: Channel

    private val REQUEST_CODE_READ_MEDIA_AUDIO = 100
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        dbHelper = DbHelper(activity)
        deviceSettingViewModel = ViewModelProvider(this)[DeviceSettingViewModel::class.java]

        val json = dbHelper.getDeviceSettings()
        Log.e("AllDataJson", json)
        dbHelper.updateAllDeviceSettings1(json)


        requestPermission()

        //reload()

        init()
        initAction()

        initSocket()

        //initPusher()

        setHepa()
        setSystemOnOffManage()
        initSystemListener()

    }

    private fun initSocket() {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(Constant.SOCKET_URL)
            .build()

        webSocketListener = MyWebSocketListener(this, client, request)
        webSocketListener.start()

    }

    private fun sendMessage(message: String, toastMsg: String = "") {

//val finalMsg = message.replace("\"","").replace("\n", "").replace(" ", "")
//val finalMsg1 = "{frameData: '{,SR_WSL:200001,S_Sensor_1_NO_NC_SETTING:1,S_Sensor_2_NO_NC_SETTING:1,S_Sensor_3_NO_NC_SETTING:1,S_Sensor_4_NO_NC_SETTING:1,S_Sensor_5_NO_NC_SETTING:1,S_Sensor_6_NO_NC_SETTING:1,S_Sensor_7_NO_NC_SETTING:1,S_Sensor_8_NO_NC_SETTING:1,S_Sensor_9_NO_NC_SETTING:1,S_Sensor_10_NO_NC_SETTING:1,S_Light_1_ON_OFF:0,S_Light_2_ON_OFF:0,S_Light_3_ON_OFF:1,S_Light_4_ON_OFF:1,S_Light_5_ON_OFF:1,S_Light_6_ON_OFF:1,S_Light_7_ON_OFF:1,S_Light_8_ON_OFF:1,S_Light_9_ON_OFF:1,S_Light_10_ON_OFF:1,S_Light_1_Intensity:010,S_Light_2_Intensity:020,S_Light_3_Intensity:030,S_Light_4_Intensity:090,S_Light_5_Intensity:020,S_Light_6_Intensity:060,S_Light_7_Intensity:070,S_Light_8_Intensity:080,S_Light_9_Intensity:090,S_Light_10_Intensity:050,S_IOT_TIMER:0030,S_TEMP_SETPT:200,S_RH_SETPT:750}',  receivedFrom: 'app'}"

        val r = JSONObject()
        r.put("frameData", message)
        r.put("receivedFrom", "app")

        val flag = webSocketListener.sendRequest(r.toString())

        val msg = if (toastMsg.isNotEmpty()) {
            "Request sent : $toastMsg"
        } else {
            "Request sent"
        }

        if (flag) {
            Log.e("MyWebSocketListener", "sendMessage - $r")
            showCustomToastLayout(activity, msg)
        } else {
            Log.e("MyWebSocketListener", "sendMessage - fails")
            showCustomToastLayout(activity, msg)
        }


    }

    private fun setMenuAdapter() {
        menuList = AllList.initMenuList()

        viewPagerMenuAdapter = ViewPagerMenuAdapter(activity, menuList)
        binding.viewPagerMenu.adapter = viewPagerMenuAdapter
        binding.viewPagerMenu.currentItem = 0
        viewPagerMenuAdapter.registerInterface(this)
        binding.tabLayout.setupWithViewPager(binding.viewPagerMenu, true)

        updateTempRhInMenu()
    }

    private fun init() {
//        val sdf = SimpleDateFormat("dd MMM yyyy")
//        val currentDate = sdf.format(Date())
//        binding.tvTodayDate.text = currentDate

        if (getSound()) {
            binding.imgMuteMGPS.setImageResource(R.drawable.ic_unmute)
        } else {
            binding.imgMuteMGPS.setImageResource(R.drawable.ic_mute)
        }
    }

    private fun initAction() {
        binding.imgRefresh.setOnClickListener(this)
        binding.imgSetting.setOnClickListener(this)
        binding.imgLogout.setOnClickListener(this)
        binding.previousButton.setOnClickListener(this)
        binding.playPauseButton.setOnClickListener(this)
        binding.nextButton.setOnClickListener(this)
        binding.imgMute.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
//        binding.btnPlayMPGS.setOnClickListener(this)
        binding.flMute.setOnClickListener(this)
    }

    private fun updateTempRhInMenu() {
        // Always run UI updates on the main thread
        runOnUiThread {
            try {
                // Retrieve updated values from the DB
                val dbTemp = dbHelper.getDeviceSettingValue("C_OT_TEMP") ?: ""
                val dbRH = dbHelper.getDeviceSettingValue("C_RH") ?: ""

                // Convert the values as needed
                val tempValue = dbTemp.takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.div(10) ?: 0.0
                val rhValue = dbRH.takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.div(10) ?: 0.0

                // Update current values
                currentTemp = tempValue
                currentHumidity = rhValue

                // Check if menuList is initialized and not empty
                if (menuList.isNotEmpty()) {
                    // Update your menu list with the new temperature and humidity
                    menuList.forEach { subList ->
                        subList.forEach { menuItem ->
                            when (menuItem.title) {
                                "Temp" -> menuItem.desc = "$tempValue${Constant.degreeSymbol}"
                                "Rh" -> menuItem.desc = "$rhValue${Constant.percentageSymbol}"
                            }
                        }
                    }

                    // Log for debugging
                    Log.d("MenuUpdate", "Menu list updated: ${menuList[0].size} items, Temp: $tempValue, RH: $rhValue")

                    // Make sure the adapter is initialized before notifying
                    if (::viewPagerMenuAdapter.isInitialized) {
                        viewPagerMenuAdapter.notifyDataSetChanged()
                    } else {
                        Log.e("MenuUpdate", "ViewPagerMenuAdapter not initialized")
                    }
                } else {
                    Log.e("MenuUpdate", "Menu list is empty")
                }
            } catch (e: Exception) {
                Log.e("MenuUpdate", "Error updating menu: ${e.message}", e)
            }
        }

        Log.d("MenuUpdate", "Menu list size: ${menuList.size}")
        if (menuList.isNotEmpty()) {
            Log.d("MenuUpdate", "First page items: ${menuList[0].size}")
        }

    }

    private fun setHepa() {
        val hepaValue = dbHelper.getDeviceSettingValue("F_Sensor_8_FAULT_BIT") ?: "0"
        Log.e("HEPA", hepaValue)
        val (hepaText, hepaColor) = when (hepaValue) {
            "0" -> "Hepa : Healthy" to R.color.colorGreen
            else -> "Hepa : Unhealthy" to R.color.colorRed
        }

        binding.tvHepa.apply {
            text = hepaText
            setTextColor(ContextCompat.getColor(activity, hepaColor))
        }
    }

    private fun setSystemOnOffManage() {

        val systemValue = dbHelper.getDeviceSettingValue("S_Light_10_ON_OFF") ?: "0"
        binding.swOnOffSystem.isChecked = systemValue == "1"

        if (systemValue == "0") {
            dbHelper.updateDeviceSetting("C_OT_TEMP", "00")
            dbHelper.updateDeviceSetting("C_RH", "00")
            dbHelper.updateDeviceSetting("C_PRESSURE_1", "00")
        }

    }

    private fun initSystemListener() {
        binding.swOnOffSystem.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                // Show confirmation dialog when turning off
                showConfirmationDialog {
                    // This block executes when user confirms
                    dbHelper.updateDeviceSetting("S_Light_10_ON_OFF", "0")
                    sendMessage(dbHelper.getDeviceSettings())

                    dbHelper.updateDeviceSetting("C_OT_TEMP", "00")
                    dbHelper.updateDeviceSetting("C_RH", "00")
                    dbHelper.updateDeviceSetting("C_PRESSURE_1", "00")
                }
            } else {
                // Direct action when turning on (no confirmation needed)
                dbHelper.updateDeviceSetting("S_Light_10_ON_OFF", "1")
                sendMessage(dbHelper.getDeviceSettings())
            }
        }
    }

    private fun showConfirmationDialog(onConfirm: () -> Unit) {
        AlertDialog.Builder(activity)
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgSetting -> {
                val intent = Intent(this, Setting::class.java)
                startActivity(intent)
            }

            R.id.previousButton -> {
                playPreviousMusic()
            }

            R.id.playPauseButton -> {
                playPauseMusic()
            }

            R.id.nextButton -> {
                playNextMusic()
            }

            R.id.imgMute -> {
                muteUnmuteMusic()
            }

            R.id.flMute -> {
                stopMPGSSound()
            }

        }
    }

    override fun clickMenu(dataS: MenuModel?, position: Int) {
        if (!Utils.canClick()) return

        when (dataS!!.click) {
            Constant.temp -> {
                showTempDialog()
            }

            Constant.hd -> {
                showHumidityDialog()
            }

            Constant.light -> {
                showLightDialog()
            }

            Constant.mgps -> {
                val intent = Intent(this, MGPS::class.java)
                startActivity(intent)
            }

            Constant.timer -> {
                showTimerDialog()
            }

            Constant.music -> {
                showMusicDialog()
            }

            Constant.entrance -> {
                val intent = Intent(this, Entrance::class.java)
                startActivity(intent)
            }

        }


    }

    override fun onResume() {
        super.onResume()

        val dTheme = dbHelper.getTheme(Constant.main)
        binding.llMain.setBackgroundDrawable(getGradientNormal(dTheme.color1, dTheme.color2))

        manageSurgeryStatusText()

        // Ensure menu is properly loaded
        if (!::viewPagerMenuAdapter.isInitialized || menuList.isEmpty()) {
            setMenuAdapter()
        } else {
            updateTempRhInMenu()
            viewPagerMenuAdapter.notifyDataSetChanged()
        }

    }

    private fun manageSurgeryStatusText() {
        if (getCurrentMode()!! == OTStatus.STARTED) {
            binding.tvSurgeryStatus.visibility = View.VISIBLE
        } else {
            binding.tvSurgeryStatus.visibility = View.GONE
        }
    }

    private fun showWarningDialog(context: Context, msg: String) {
        android.app.AlertDialog.Builder(context)
            .setTitle("Warning")
            .setMessage(msg)
            .setCancelable(false) // Prevents closing the dialog by tapping outside
            .setPositiveButton("Understood") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLightDialog() {
//        val d = Dialog(activity)
        val d = AutoDismissDialog(activity)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(false)

        val binding = DialogLightBinding.inflate(layoutInflater)
        d.setContentView(binding.root)

        d.window?.apply {
            attributes.windowAnimations = R.style.DialogAnimation
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (Resources.getSystem().displayMetrics.widthPixels * 0.7).toInt() // 70% width
            val height = (Resources.getSystem().displayMetrics.heightPixels * 0.9).toInt() // 70% width
            setLayout(width, height)
        }

        val dTheme = dbHelper.getTheme(Constant.light)
        binding.llMainLight.setBackgroundDrawable(getGradient(dTheme.color1, dTheme.color2))

        lightList = dbHelper.getAllLightData()

        var adapterList = LightAdapter(activity, lightList)
        binding.rvLight.adapter = adapterList

        val allLightFlag = lightList.any { it.onOffValue == "1" }
        binding.swOnOffAllLight.setChecked(allLightFlag)

        adapterList.registerInterface(object : LightAdapter.ClickLightInterface {
            override fun clickLightOnOff(dataS: LightDataModel, position: Int, isChecked: Boolean) {
                //todo light on/off here
                //sendRequest(dataS, isChecked)

                val isLightOnOff = if (isChecked) "1" else "0"
                dbHelper.updateLightSetting(position = (position + 1), onOffValue = isLightOnOff)
//                sendMessage(dbHelper.getDeviceSettingsAsJson())
                val toastMsg = if (isChecked) {
                    "Light ${position + 1} is ON"
                } else {
                    "Light ${position + 1} is OFF"
                }

                sendMessage(dbHelper.getDeviceSettings(), toastMsg)

                val allFlag = lightList.any { it.onOffValue == "1" }
                binding.swOnOffAllLight.setChecked(allFlag)

            }

            override fun updateIntensity(dataS: LightDataModel, position: Int, progress: Int) {
                //sendRequestForIntensity(dataS, position, progress)

                dbHelper.updateLightSetting(position = (position + 1), intensityValue = progress)
                val toastMsg = "Light ${position + 1} is Intensity = $progress"
//                sendMessage(dbHelper.getDeviceSettingsAsJson())
                sendMessage(dbHelper.getDeviceSettings(), toastMsg)

            }

        })

        binding.swOnOffAllLight.setOnClickListener {
            //todo
            val isChecked = binding.swOnOffAllLight.isChecked()
            binding.swOnOffAllLight.setChecked(!isChecked)

            for (i in 0 until lightList.size) {
                if (!isChecked) {
                    lightList[i].intensityKey = "1"
                    dbHelper.updateLightSetting(position = (i + 1), onOffValue = "1")
                } else {
                    lightList[i].intensityKey = "0"
                    dbHelper.updateLightSetting(position = (i + 1), onOffValue = "0")
                }

//                sendMessage(dbHelper.getDeviceSettings())
                Log.e("all-light", isChecked.toString())
            }

            val toastMsg = if (!isChecked) {
                "All Light ON"
            } else {
                "All Light OFF"
            }

            sendMessage(dbHelper.getDeviceSettings(), toastMsg)

            lightList = dbHelper.getAllLightData()
            adapterList = LightAdapter(activity, lightList)
            binding.rvLight.adapter = adapterList

        }

        binding.imgClose.setOnClickListener {
            d.dismiss()
        }

        d.show()
    }

    private fun showMusicDialog() {
        val d = Dialog(activity)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(true)
        val binding = DialogMusicBinding.inflate(layoutInflater)
        d.setContentView(binding.root)

//        d.window?.apply {
//            attributes.windowAnimations = R.style.DialogAnimation
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            val width = (Resources.getSystem().displayMetrics.widthPixels * 0.7).toInt() // 70% width
//            val height = (Resources.getSystem().displayMetrics.heightPixels * 0.7).toInt() // 70% width
//            setLayout(width, height)
//        }

        d.window?.apply {
            attributes.windowAnimations = R.style.DialogAnimation
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (Resources.getSystem().displayMetrics.widthPixels * 0.6).toInt()
            val height = (Resources.getSystem().displayMetrics.heightPixels * 0.85).toInt()
            setLayout(width, height)
        }

        val dTheme = dbHelper.getTheme(Constant.music)
        binding.llMainMusic.setBackgroundDrawable(getGradient(dTheme.color1, dTheme.color2))

        val musicAdapter = MusicAdapter(activity, musicList)
        musicAdapter.registerInterface(object : MusicAdapter.ClickMusicInterface {
            override fun clickMusic(dataS: String, position: Int) {
                currentIndex = position
                playMusic(dataS)
                d.dismiss()
            }
        })
        binding.rvMusicList.adapter = musicAdapter

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                musicAdapter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.imgClose.setOnClickListener {

            releasePlayer()

            d.dismiss()
        }

        d.show()
//        val window: Window = d.window!!
//        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

    }

    private fun loadMusicFiles(): List<String> {
        val musicList = mutableListOf<String>()
        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(android.provider.MediaStore.Audio.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
            while (it.moveToNext()) {
                val musicPath = it.getString(columnIndex)
                musicList.add(musicPath)
            }
        }

        return musicList
    }

    private fun playMusic(musicPath: String) {
        binding.llMusic.visibility = View.VISIBLE
        binding.imgMute.visibility = View.VISIBLE

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(musicPath)
                prepare()
                start()
                binding.sbMusic.max = duration.div(1000) ?: 0
                Log.e("MUSIC", duration.toString())
                updateSeekBar()
                setOnCompletionListener { playNextMusic() }
                binding.playPauseButton.setImageResource(R.drawable.ic_pause_vector)
                binding.tvTitle.text = musicPath.substring(musicPath.lastIndexOf("/") + 1)

                if (isMuted) {
                    setVolume(0f, 0f)
                } else {
                    setVolume(originalVolume, originalVolume)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private var isMuted = false
    private var originalVolume = 1.0f

    private fun muteUnmuteMusic() {
        isMuted = !isMuted
        try {
            if (isMuted) {
                binding.imgMute.setImageResource(R.drawable.ic_mute)
                mediaPlayer?.setVolume(0f, 0f)
            } else {
                binding.imgMute.setImageResource(R.drawable.ic_unmute)
                mediaPlayer?.setVolume(originalVolume, originalVolume)
            }
        } catch (e: Exception) {
            println()
        }

    }

    private fun playPauseMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                binding.playPauseButton.setImageResource(R.drawable.ic_play_vector)
            } else {
                it.start()
                binding.playPauseButton.setImageResource(R.drawable.ic_pause_vector)
                updateSeekBar()
            }
        }
    }

    private fun playNextMusic() {
        if (musicList.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % musicList.size
            playMusic(musicList[currentIndex])
        }
    }

    private fun playPreviousMusic() {
        if (musicList.isNotEmpty()) {
            currentIndex = if (currentIndex - 1 < 0) musicList.size - 1 else currentIndex - 1
            playMusic(musicList[currentIndex])
        }
    }

    private fun updateSeekBar() {


//        binding.sbMusic.max = mediaPlayer?.duration?.div(1000) ?: 0
//        Log.e("MUSIC", "Max - "+(mediaPlayer?.duration?.div(1000) ?: 0).toString())

        runnable = Runnable {
            binding.sbMusic.progress = mediaPlayer?.currentPosition?.div(1000) ?: 0
//            binding.currentTime.text = formatTime(mediaPlayer?.currentPosition?.div(1000) ?: 0)
//            binding.totalTime.text = formatTime(mediaPlayer?.duration?.div(1000) ?: 0)

//            Log.e("MUSIC", (mediaPlayer?.currentPosition?.div(1000) ?: 0).toString())

            binding.tvCurrentDuration.text = formatTime(mediaPlayer?.currentPosition?.div(1000) ?: 0)
            binding.tvTotalDuration.text = formatTime(mediaPlayer?.duration?.div(1000) ?: 0)

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)


//        mediaPlayer?.let {
//            binding.sbMusic.progress = it.currentPosition
//
//            Log.e("MUSIC", it.currentPosition.toString())
//
//            if (it.isPlaying) {
//                Handler(Looper.getMainLooper()).postDelayed({
//                    updateSeekBar()
//                }, 1000)
//            }
//        }
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    private fun releasePlayer() {
        binding.llMusic.visibility = View.INVISIBLE
        binding.imgMute.visibility = View.GONE
        try {
            if (mediaPlayer != null) {
                mediaPlayer?.release()
            }
            if (handler != null) {
                handler.removeCallbacks(runnable)
            }
        } catch (e: Exception) {
            println()
        }

    }


    private fun showTimerDialog() {
        val d = Dialog(activity)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(false)
        d.setContentView(R.layout.dialog_timer)

        val bindingTimer = DialogTimerBinding.inflate(layoutInflater)
        d.setContentView(bindingTimer.root)

        d.window?.apply {
            attributes.windowAnimations = R.style.DialogAnimation
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (Resources.getSystem().displayMetrics.widthPixels * 0.55).toInt()
            val height = (Resources.getSystem().displayMetrics.heightPixels * 0.85).toInt()
            setLayout(width, height)
        }

        val dTheme = dbHelper.getTheme(Constant.timer)
        bindingTimer.llMainTimer.setBackgroundDrawable(getGradient(dTheme.color1, dTheme.color2))

        val stopwatchAdapter = StopwatchAdapter(activity, lapList)
        bindingTimer.rvStopwatch.adapter = stopwatchAdapter

//        val updateListener = object : Stopwatch.UpdateListener {
//            override fun onUpdate(totalTime: Long, lapTime: Long, useLongerMSFormat: Boolean) {
//                activity.runOnUiThread {
//                    val t = totalTime.formatStopwatchTime(useLongerMSFormat)
//                    bindingTimer.stopwatchTime.text = t
//                    //notify
//                    val stopwatchAdapter = StopwatchAdapter(activity, lapList)
//                    bindingTimer.rvStopwatch.adapter = stopwatchAdapter
//
//                    binding.tvTodayDate.text = t
//
////                    menuList.forEach { subList ->
////                        subList.find { it.title == "Timer" }?.desc = t
////                    }
////                    viewPagerMenuAdapter.notifyDataSetChanged()
//
//                }
//            }
//
//            override fun onStateChanged(state: Stopwatch.State) {
//                activity.runOnUiThread {
//
//                    val drawableId = if (state == Stopwatch.State.RUNNING) R.drawable.ic_pause_vector else R.drawable.ic_play_vector
////                    val iconColor = if (requireContext().getProperPrimaryColor() == Color.WHITE) Color.BLACK else Color.WHITE
//                    bindingTimer.stopwatchPlayPause.setImageDrawable(getDrawable(drawableId))
//
//                    if (state == Stopwatch.State.RUNNING) {
//                        bindingTimer.stopwatchLap.visibility = View.VISIBLE
//                    } else {
//                        bindingTimer.stopwatchLap.visibility = View.INVISIBLE
//                    }
//
//                    if (state != Stopwatch.State.STOPPED) {
//                        bindingTimer.stopwatchReset.visibility = View.VISIBLE
//                    } else {
//                        bindingTimer.stopwatchReset.visibility = View.INVISIBLE
//                    }
//
//                }
//            }
//        }
//        Stopwatch.addUpdateListener(updateListener)


        val dialogUpdateListener = object : Stopwatch.UpdateListener {
            override fun onUpdate(totalTime: Long, lapTime: Long, useLongerMSFormat: Boolean) {
                activity.runOnUiThread {
                    val t = totalTime.formatStopwatchTime(useLongerMSFormat)
                    bindingTimer.stopwatchTime.text = t
                    //notify
                    val stopwatchAdapter = StopwatchAdapter(activity, lapList)
                    bindingTimer.rvStopwatch.adapter = stopwatchAdapter

                }
            }

            override fun onStateChanged(state: Stopwatch.State) {
                activity.runOnUiThread {

                    val drawableId = if (state == Stopwatch.State.RUNNING) R.drawable.ic_pause_vector else R.drawable.ic_play_vector
//                    val iconColor = if (requireContext().getProperPrimaryColor() == Color.WHITE) Color.BLACK else Color.WHITE
                    bindingTimer.stopwatchPlayPause.setImageDrawable(getDrawable(drawableId))

                    if (state == Stopwatch.State.RUNNING) {
                        bindingTimer.stopwatchLap.visibility = View.VISIBLE
                    } else {
                        bindingTimer.stopwatchLap.visibility = View.INVISIBLE
                    }

                    if (state != Stopwatch.State.STOPPED) {
                        bindingTimer.stopwatchReset.visibility = View.VISIBLE
                    } else {
                        bindingTimer.stopwatchReset.visibility = View.INVISIBLE
                    }

                }
            }
        }

        // Add both listeners
        Stopwatch.addUpdateListener(dialogUpdateListener)
        Stopwatch.addUpdateListener(mainUpdateListener)  // Add main listener when dialog opens

        bindingTimer.imgClose.setOnClickListener {
            Stopwatch.removeUpdateListener(dialogUpdateListener)
            d.dismiss()
        }

        bindingTimer.stopwatchPlayPause.setOnClickListener {
            Stopwatch.toggle(true)
        }

        bindingTimer.stopwatchReset.setOnClickListener {
            Stopwatch.reset()
            lapList = Stopwatch.laps
            val stopwatchAdapter = StopwatchAdapter(activity, lapList)
            bindingTimer.rvStopwatch.adapter = stopwatchAdapter
            binding.tvTimer.text = ""
        }

        bindingTimer.stopwatchLap.setOnClickListener {
            lapList = Stopwatch.lap()
//            Stopwatch.reset()
//            lapList = Stopwatch.laps
//            stopwatchAdapter.updateItems()
            val stopwatchAdapter = StopwatchAdapter(activity, lapList)
            bindingTimer.rvStopwatch.adapter = stopwatchAdapter

        }

        d.show()

    }

    private val mainUpdateListener = object : Stopwatch.UpdateListener {
        override fun onUpdate(totalTime: Long, lapTime: Long, useLongerMSFormat: Boolean) {
            activity.runOnUiThread {
                val t = totalTime.formatStopwatchTime(useLongerMSFormat)
                binding.tvTimer.text = "Timer : $t"
            }
        }

        override fun onStateChanged(state: Stopwatch.State) {
            // Not needed for the main activity updates
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showTempDialog() {
        val d = AutoDismissDialog(activity)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(true)
        val binding = DialogTempBinding.inflate(layoutInflater)
        d.setContentView(binding.root)
        d.window?.apply {
            attributes.windowAnimations = R.style.DialogAnimation
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (Resources.getSystem().displayMetrics.widthPixels * 0.6).toInt()
            val height = (Resources.getSystem().displayMetrics.heightPixels * 0.85).toInt()
            setLayout(width, height)
        }

        val dTheme = dbHelper.getTheme(Constant.temp)
        binding.llMain.setBackgroundDrawable(getGradient(dTheme.color1, dTheme.color2))

        binding.imgClose.setOnClickListener {
            d.dismiss()
        }

        val step = 0.5
        val min = 16
        val max = 35

//        binding.tvCurrentTemp.text = "${this.currentTemp}${Constant.degreeSymbol}"
//        binding.tvChangeTemp.text = "$currentTemp${Constant.degreeSymbol}"

//        if (temperatureRhList.size > 2) {
//            val temp = temperatureRhList[2].current_value.toDouble() / 10
//            binding.tvChangeTemp.text = "$temp${Constant.degreeSymbol}"
//            if (temperatureRhList[2].current_value == temperatureRhList[0].current_value) {
//                binding.llRequestedTemp.visibility = View.GONE
//            } else {
//                binding.llRequestedTemp.visibility = View.VISIBLE
//                binding.tvRequestedTemp.text = "$temp${Constant.percentageSymbol}"
//            }
//        }

        if (getIsFirstTempToday()) {
            binding.llRequestedTemp.visibility = View.GONE
        } else {
            binding.llRequestedTemp.visibility = View.VISIBLE

            val requestTemp = dbHelper.getDeviceSettingValue("S_TEMP_SETPT") ?: ""
            val requestTempValue = requestTemp.takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.div(10) ?: 0.0

            val achievedTemp = dbHelper.getDeviceSettingName("S_TEMP_SETPT") ?: ""

            //Log.e("CheckTempRequest", "requestTemp : $requestTemp \n requestTempValue : $requestTempValue \n achievedTemp : $achievedTemp")

            if (achievedTemp != null && achievedTemp.isNotEmpty()) {
                val achievedTempValue = achievedTemp.takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.div(10) ?: 0.0
                binding.tvRequestedTemp.text = "$achievedTempValue${Constant.degreeSymbol} Achieved"
                binding.tvRequestedTemp.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen))
            } else {
                binding.tvRequestedTemp.text = "$requestTempValue${Constant.degreeSymbol} Requested"
                binding.tvRequestedTemp.setTextColor(ContextCompat.getColor(activity, R.color.black))
            }
        }

//        if (requestTempValue == currentTemp) {
//            binding.llRequestedTemp.visibility = View.GONE
//        } else {
//            binding.llRequestedTemp.visibility = View.VISIBLE
//            binding.tvRequestedTemp.text = "$requestTempValue${Constant.degreeSymbol}"
//        }


        binding.btnSave.visibility = View.INVISIBLE
        binding.btnSave.setOnClickListener {
            currentTemp = seekBarTemp
//            binding.tvCurrentTemp.text = "$currentTemp${Constant.degreeSymbol}"
            binding.btnSave.visibility = View.INVISIBLE

            //sendRequestForTemperatureRh(2, (currentTemp * 10).toInt().toString())

            //Log.e("CheckTempRequest", "currentTemp : $currentTemp")
            dbHelper.updateDeviceSetting("S_TEMP_SETPT", (currentTemp * 10).toInt().toString())
            dbHelper.updateDeviceSettingName("S_TEMP_SETPT", "")
            sendMessage(dbHelper.getDeviceSettings())

            setIsFirstTempToday(false)

            //todo set this code in response
            //update menu view
            //get temp menu position and remove old data and add new data in same position
//            for (i in 0 until menuList.size) {
//                val subMenuList = menuList[i]
//                val index = subMenuList.indexOfFirst { it.click == Constant.temp }
//                if (index != -1) {
//                    subMenuList[index].desc = "$currentTemp${Constant.degreeSymbol}"
//                    menuList.removeAt(i)
//                    menuList.add(i, subMenuList)
//                    viewPagerMenuAdapter.notifyDataSetChanged()
//                    break
//                }
//            }


        }

        binding.seekBar.max = ((max - min) / step).toInt()

        if (currentTemp > min && currentTemp < max) {
            val progress = ((currentTemp - min) / step).toInt()
            binding.seekBar.progress = progress
            binding.tvChangeTemp.text = "$currentTemp${Constant.degreeSymbol}"
        } else {
            binding.tvChangeTemp.text = "0.0${Constant.degreeSymbol}"
        }

        binding.seekBar.setOnSeekArcChangeListener(object : OnSeekArcChangeListener {
            override fun onProgressChanged(seekArc: SeekArc?, progress: Int, fromUser: Boolean) {
                seekBarTemp = min + (progress * 0.5)
                binding.tvChangeTemp.text = "${(seekBarTemp)}${Constant.degreeSymbol}"

                if (seekBarTemp == currentTemp) {
                    binding.btnSave.visibility = View.INVISIBLE
                } else {
                    binding.btnSave.visibility = View.VISIBLE
                }
            }

            override fun onStartTrackingTouch(seekArc: SeekArc?) {}

            override fun onStopTrackingTouch(seekArc: SeekArc?) {}

        })

        d.show()

    }

    @SuppressLint("SetTextI18n")
    private fun showHumidityDialog() {
        val d = AutoDismissDialog(activity)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(true)
        d.setContentView(R.layout.dialog_humidity)

        val binding = DialogHumidityBinding.inflate(layoutInflater)
        d.setContentView(binding.root)

        d.window?.apply {
            attributes.windowAnimations = R.style.DialogAnimation
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (Resources.getSystem().displayMetrics.widthPixels * 0.6).toInt()
            val height = (Resources.getSystem().displayMetrics.heightPixels * 0.85).toInt()
            setLayout(width, height)
        }

        val dTheme = dbHelper.getTheme(Constant.hd)
        binding.llMainHD.setBackgroundDrawable(getGradient(dTheme.color1, dTheme.color2))

        binding.imgClose.setOnClickListener {
            d.dismiss()
        }

        val step = 0.5
        val min = 10
        val max = 90

//        binding.tvCurrentHumidity.text = "$currentHumidity${Constant.percentageSymbol}"
//        binding.tvChangeHumidity.text = "$currentHumidity${Constant.percentageSymbol}"


        binding.btnSaveHumidity.visibility = View.INVISIBLE
        binding.btnSaveHumidity.setOnClickListener {
//            currentHumidity = seekBarHumidity
            currentHumidity = convertHumidity(seekBarHumidity.toString())
//            binding.tvCurrentHumidity.text = "$currentHumidity${Constant.percentageSymbol}"
            binding.btnSaveHumidity.visibility = View.INVISIBLE

            //sendRequestForTemperatureRh(3, (currentHumidity * 10).toString())

            dbHelper.updateDeviceSetting("S_RH_SETPT", (currentHumidity * 10).toInt().toString())
            dbHelper.updateDeviceSettingName("S_RH_SETPT", "")
            sendMessage(dbHelper.getDeviceSettings())

            setIsFirstRhToday(false)

            //todo set this code in response
            //update menu view
//            for (i in 0 until menuList.size) {
//                val subMenuList = menuList[i]
//                val index = subMenuList.indexOfFirst { it.click == Constant.hd }
//                if (index != -1) {
//                    subMenuList[index].desc = "$currentHumidity${Constant.percentageSymbol}"
//                    menuList.removeAt(i)
//                    menuList.add(i, subMenuList)
//                    viewPagerMenuAdapter.notifyDataSetChanged()
//                    break
//                }
//            }


        }

        binding.sbHumidity.max = ((max - min) / step).toInt()


        if (currentHumidity > min && currentHumidity < max) {
            val progress = ((currentHumidity - min) / step).toInt()
            binding.sbHumidity.progress = progress
            binding.tvChangeHumidity.text = currentHumidity.toString()
            binding.tvChangeHumidity.text = "$currentHumidity${Constant.percentageSymbol}"
        } else {
            binding.tvChangeHumidity.text = "0.0${Constant.percentageSymbol}"
        }


        if (getIsFirstRhToday()) {
            binding.llRequestedHumidity.visibility = View.GONE
        } else {
            binding.llRequestedHumidity.visibility = View.VISIBLE

            val requestHumidity = dbHelper.getDeviceSettingValue("S_RH_SETPT") ?: ""
            val humidityRequestedValue = requestHumidity.takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.div(10) ?: 0.0

            val achievedHumidity = dbHelper.getDeviceSettingName("S_RH_SETPT") ?: ""

            if (achievedHumidity != null && achievedHumidity.isNotEmpty()) {
                val achievedHumidityValue = achievedHumidity.takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.div(10) ?: 0.0
                binding.tvRequestedHumidity.text = "$achievedHumidityValue${Constant.percentageSymbol} Achieved"
                binding.tvRequestedHumidity.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen))
            } else {
                binding.tvRequestedHumidity.text = "$humidityRequestedValue${Constant.percentageSymbol} Requested"
                binding.tvRequestedHumidity.setTextColor(ContextCompat.getColor(activity, R.color.black))
            }

        }

//        if (humidityRequestedValue == currentHumidity) {
//            binding.llRequestedHumidity.visibility = View.GONE
//        } else {
//            binding.llRequestedHumidity.visibility = View.VISIBLE
//            binding.tvRequestedHumidity.text = "$humidityRequestedValue${Constant.percentageSymbol}"
//        }


//        if (temperatureRhList.size > 3) {
//            val rh = temperatureRhList[3].current_value.toDouble() / 10
//            if (rh > min && rh < max) {
//                val progress = ((rh - min) / step).toInt()
//                binding.sbHumidity.progress = progress
//                binding.tvChangeHumidity.text = rh.toString()
//                binding.tvChangeHumidity.text = "$rh${Constant.percentageSymbol}"
//
//                if (temperatureRhList[3].current_value == temperatureRhList[1].current_value) {
//                    binding.llRequestedHumidity.visibility = View.GONE
//                } else {
//                    binding.llRequestedHumidity.visibility = View.VISIBLE
//                    binding.tvRequestedHumidity.text = "$rh${Constant.percentageSymbol}"
//                }
//            }
//        }


        binding.sbHumidity.setOnSeekArcChangeListener(object : OnSeekArcChangeListener {
            override fun onProgressChanged(seekArc: SeekArc?, progress: Int, fromUser: Boolean) {
                seekBarHumidity = min + (progress * step)
                binding.tvChangeHumidity.text = "${(seekBarHumidity)}${Constant.percentageSymbol}"

                if (seekBarHumidity == currentHumidity) {
                    binding.btnSaveHumidity.visibility = View.INVISIBLE
                } else {
                    binding.btnSaveHumidity.visibility = View.VISIBLE
                }
            }

            override fun onStartTrackingTouch(seekArc: SeekArc?) {}

            override fun onStopTrackingTouch(seekArc: SeekArc?) {}

        })

        d.show()

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestReadMediaAudioPermission()
        } else {
            requestReadExternalStoragePermission()
        }
    }

    private fun requestReadMediaAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), REQUEST_CODE_READ_MEDIA_AUDIO
            )
        } else {
            musicList = loadMusicFiles()
        }
    }

    private fun requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_EXTERNAL_STORAGE
            )
        } else {
            musicList = loadMusicFiles()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_READ_MEDIA_AUDIO -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    musicList = loadMusicFiles()
                } else {
                    // Permission denied, handle accordingly
                }
            }

            REQUEST_CODE_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    musicList = loadMusicFiles()
                } else {
                    // Permission denied, handle accordingly
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        Stopwatch.removeUpdateListener(mainUpdateListener)
        webSocketListener.closeWebSocket()
    }

    private var mgpsMediaPlayer: MediaPlayer? = null

    private fun playMPGSSound() {
        mgpsMediaPlayer = MediaPlayer.create(this, R.raw.alarm_tone)

        mgpsMediaPlayer?.let { player ->
            if (!player.isPlaying) {
                player.start()
            }
        }
    }

    private fun pauseMPGSSound() {
        mgpsMediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                player.seekTo(0) // Reset to beginning
            }
        }
    }

    private fun stopMPGSSound() {

        if (getSound()) {
            setSound(false)
            binding.imgMuteMGPS.setImageResource(R.drawable.ic_mute)

            mgpsMediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
            //mgpsMediaPlayer = null

        } else {
            setSound(true)
            binding.imgMuteMGPS.setImageResource(R.drawable.ic_unmute)
        }


        //binding.flMute.visibility = View.GONE

    }

    override fun onMessageReceived(message: String) {
        // Update database
        deviceSettingViewModel.updateAllDeviceSettings(message)

        // Run UI updates on the main thread
        runOnUiThread {
            checkMGPSAlarm()
            updateTempRhInMenu()
            setHepa()
            setSystemOnOffManage()
            checkRequestedTempAchievedOrNot()
            checkRequestedRHAchievedOrNot()

            // Force adapter refresh
            if (::viewPagerMenuAdapter.isInitialized) {
                viewPagerMenuAdapter.notifyDataSetChanged()
            }
        }

        Log.d("WebSocket", "Message received, updating UI")

        // Create and send broadcast
        val intent = Intent("com.surgeon.controlpanels.WEB_SOCKET_MESSAGE")
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun checkRequestedRHAchievedOrNot() {
        val dbRh = dbHelper.getDeviceSettingValue("C_RH") ?: ""
        val requestHumidity = dbHelper.getDeviceSettingValue("S_RH_SETPT") ?: ""

        if (dbRh == requestHumidity) {
            dbHelper.updateDeviceSettingName("S_RH_SETPT", requestHumidity)
        }
    }

    private fun checkRequestedTempAchievedOrNot() {
        val dbTemp = dbHelper.getDeviceSettingValue("C_OT_TEMP") ?: ""
        val requestTemp = dbHelper.getDeviceSettingValue("S_TEMP_SETPT") ?: ""

        if (dbTemp == requestTemp) {
            dbHelper.updateDeviceSettingName("S_TEMP_SETPT", requestTemp)
        }
    }

    private fun checkMGPSAlarm() {
        val list = dbHelper.getDeviceSettingsByType(1)
        val index = list.indexOfFirst { it.value == "1" }
        Log.e("checkMGPSAlarm", "checkMGPSAlarm: $index")
        if (index != -1) {
            binding.flMute.visibility = View.VISIBLE
            if (getSound()) {
                playMPGSSound()
            }
        } else {
            binding.flMute.visibility = View.GONE
        }

    }

}
