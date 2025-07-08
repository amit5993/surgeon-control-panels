package com.surgeon.controlpanels.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.surgeon.controlpanels.common.Constant
import com.surgeon.controlpanels.websocket.MyWebSocketServer

class WebSocketService : Service() {

    private lateinit var server: MyWebSocketServer

    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "SEND_TO_ALL_CLIENTS" -> {
                    val message = intent.getStringExtra("message") ?: return
                    server.sendMessageToAllClients(message)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        server = MyWebSocketServer(
            Constant.SERVER_PORT,
            onMessage = { message ->
                Log.d("WebSocketService", "Message: $message")
                sendMessageToActivity("WEBSOCKET_MESSAGE", message)
            },
            onLog = { log ->
                Log.d("WebSocketService", log)
                sendMessageToActivity("WEBSOCKET_LOG", log)
            }
        )
        server.start()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            commandReceiver, IntentFilter("SEND_TO_ALL_CLIENTS")
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
        Log.d("WebSocketService", "WebSocket server stopped")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(commandReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun sendMessageToActivity(action: String, data: String) {
        val intent = Intent(action)
        intent.putExtra("data", data)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}