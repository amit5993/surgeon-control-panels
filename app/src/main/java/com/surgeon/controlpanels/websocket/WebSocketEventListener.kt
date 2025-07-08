package com.surgeon.controlpanels.websocket

interface WebSocketEventListener {
    fun onConnected()
    fun onMessageReceived(message: String)
    fun onFailure(message: String)
}
