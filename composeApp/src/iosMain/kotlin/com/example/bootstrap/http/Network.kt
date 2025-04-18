package com.example.bootstrap.http

import com.example.bootstrap.flow.Dispatcher
import kotlinx.coroutines.flow.Flow

internal actual class Network {

    actual fun monitor(dispatcher: Dispatcher): Flow<Boolean> {
        TODO("Not yet implemented")
    }
}
