package com.surgeon.controlpanels.websocket

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.Response
import okio.ByteString

class MyWebSocketListener(
    private val eventListener: WebSocketEventListener,
    private val client: OkHttpClient,
    private val request: Request,
    private val reconnectDelay: Long = 3000L // 3 seconds
) : WebSocketListener() {

    private val TAG = "MyWebSocketListener"
    private var webSocket: WebSocket? = null

    // Call this to start the connection
    fun start() {
        webSocket = client.newWebSocket(request, this)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.e(TAG, "onOpen - $response")
        // Optionally cancel any pending reconnection attempts here.
        eventListener.onConnected()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.e(TAG, "onMessage - $text")
        eventListener.onMessageReceived(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.e(TAG, "onMessage - $bytes")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        Log.e(TAG, "onClosing - $reason")

        eventListener.onFailure(reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(TAG, "onFailure - Throwable: ${t.localizedMessage}, Response: $response")
        // Attempt to reconnect after a delay
        //reconnectWebSocket()
        try {
            if (response != null) {
                eventListener.onFailure(response.message)
            } else {
                eventListener.onFailure(t.localizedMessage)
            }
        } catch (e: Exception) {
            println()
        }

    }

    private fun reconnectWebSocket() {
        Log.e(TAG, "Attempting to reconnect WebSocket in $reconnectDelay ms...")
        // Schedule a reconnection attempt on the main thread (or any appropriate thread)
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                // Create a new connection using the same client and request.
                start()
            } catch (e: Exception) {
                Log.e(TAG, "Reconnect failed: ${e.localizedMessage}")
            }
        }, reconnectDelay)
    }

    // Method to send a request message over the WebSocket
    fun sendRequest(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }

    fun closeWebSocket() {
        if (webSocket != null) {
            webSocket?.close(1000, "Closing connection")
        }
    }
}

