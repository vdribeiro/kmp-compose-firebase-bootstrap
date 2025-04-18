package com.example.bootstrap.http

import com.example.bootstrap.flow.Dispatcher
import kotlinx.coroutines.flow.Flow

internal expect class Network() {

    fun monitor(dispatcher: Dispatcher): Flow<Boolean>
}
