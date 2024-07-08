package com.example.app_consultabar.Services

import com.example.app_consultabar.Models.Productos
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import com.google.gson.Gson

class WebSocketManager(private val onMessageReceived: (String) -> Unit) {
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder().url("ws://192.168.1.140:8081/websocket").build()
        val client = OkHttpClient()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                println("WebSocket connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("WebSocket closed: $code / $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                println("WebSocket failed: ${t.message}")
            }
        })
    }

    fun send(tableId: Long, productos: List<Productos>) {
        val data = mapOf("tableId" to tableId, "productos" to productos)
        val json = Gson().toJson(data)
        webSocket?.send(json)
    }

    fun close() {
        webSocket?.close(1000, "Connection closed")
    }

}





