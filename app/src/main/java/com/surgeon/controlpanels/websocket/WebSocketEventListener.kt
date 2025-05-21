package com.surgeon.controlpanels.websocket

interface WebSocketEventListener {
    fun onMessageReceived(message: String)
}
