package com.example.quizbanktest.network.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class SocketApplication {
    companion object {
        private lateinit var room : String
        private lateinit var socket: Socket
        private lateinit var socket_private:Socket
        private lateinit var heartbeatScheduler: ScheduledExecutorService
        private var heartbeatFuture: ScheduledFuture<*>? = null

        fun get(): Socket {
            try {
                socket = IO.socket("http://3.26.243.228/")
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return socket
        }
        fun get_private(): Socket {
            try {
                socket_private = IO.socket("http://3.26.243.228/private")
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return socket_private
        }
        fun startHeartbeat(socket: Socket) {
            heartbeatScheduler = Executors.newSingleThreadScheduledExecutor()
            heartbeatFuture = heartbeatScheduler.scheduleAtFixedRate({
                Log.e("heart","send")
                socket.emit("heartbeat", JSONObject())
            }, 0, 50, TimeUnit.SECONDS)
        }

        fun stopHeartbeat() {
            heartbeatFuture?.cancel(false)
            heartbeatScheduler.shutdown()
        }

    }
}