package com.example.bootstrap.http

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.bootstrap.appContext
import com.example.bootstrap.flow.Dispatcher
import com.example.bootstrap.flow.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal actual class Network {

    actual fun monitor(dispatcher: Dispatcher): Flow<Boolean> = flow {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                dispatcher.default.launch { emit(value = true) }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                dispatcher.default.launch { emit(value = false) }
            }
        }

        try {
            val connectivityManager = appContext.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
            connectivityManager.requestNetwork(networkRequest, networkCallback)
        } catch (_: Throwable) {
        }
    }
}