package com.surgeon.controlpanels.websocket

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class MyWebSocketServer(
    port: Int,
    private val onMessage: (String) -> Unit,
    private val onLog: (String) -> Unit
) : WebSocketServer(InetSocketAddress(port)) {

    private val connectedClients = mutableListOf<WebSocket>()

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        conn?.let {
            connectedClients.add(it)
            val msg = "Client connected: ${it.remoteSocketAddress}"
            Log.d("WebSocket", msg)
            onLog(msg)
        }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        conn?.let {
            connectedClients.remove(it)
            val msg = "Client disconnected: ${it.remoteSocketAddress}"
            Log.d("WebSocket", msg)
            onLog(msg)
        }
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        if (message != null && conn != null) {
            Log.d("WebSocket", "Received: $message")
            onMessage(message)

            // Broadcast the message to all other connected clients
            broadcastMessage(message, sender = conn)
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        val msg = "Error: ${ex?.message}"
        Log.e("WebSocket", msg)
        onLog(msg)
    }

    override fun onStart() {
        val msg = "WebSocket Server started on port: $port"
        Log.d("WebSocket", msg)
        onLog(msg)
    }

    // Broadcast message to all clients except the sender
    private fun broadcastMessage(message: String, sender: WebSocket) {
        synchronized(connectedClients) {
            connectedClients.forEach { client ->
                if (client != sender && client.isOpen) {
                    client.send(message)
                    val logMsg = "Broadcasted to ${client.remoteSocketAddress}: $message"
                    Log.d("WebSocket", logMsg)
                    onLog(logMsg)
                }
            }
        }
    }

    // Optional: Send to all clients manually
    fun sendMessageToAllClients(message: String) {
        if (connectedClients.isEmpty()) {
            onLog("No clients connected")
        }
        connectedClients.forEach { client ->
            if (client.isOpen) {
                client.send(message)
                val logMsg = "Sent to ${client.remoteSocketAddress}: $message"
                Log.d("WebSocket", logMsg)
                onLog(logMsg)
            }
        }
    }
}
