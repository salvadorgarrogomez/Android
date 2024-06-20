package com.example.app_consultabar.Services

import com.example.app_consultabar.Models.Productos
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import com.google.gson.Gson

class WebSocketManager(private val onMessageReceived: (String) -> Unit) {

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private val gson = Gson()

    fun connect() {
        val request = Request.Builder().url("ws://192.168.1.140:8081/websocket").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                println("WebSocket conectado")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                onMessageReceived(bytes.utf8())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("WebSocket cerrado: $code / $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                println("WebSocket ha fallado: ${t.message}")
            }
        })
    }

    fun send(tableId: Long, productos: List<Productos>) {
        val message = gson.toJson(mapOf("tableId" to tableId, "productos" to productos))
        webSocket.send(message)
    }

    fun close() {
        webSocket.close(1000, "La conexión se ha cerrado")
    }

}
