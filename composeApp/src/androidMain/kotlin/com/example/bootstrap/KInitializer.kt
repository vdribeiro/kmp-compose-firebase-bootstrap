package com.example.bootstrap

import android.content.Context
import androidx.startup.Initializer

internal lateinit var appContext: Context
    private set

class KInitializer: Initializer<Unit> {
    override fun create(context: Context) {
        appContext = context.applicationContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
