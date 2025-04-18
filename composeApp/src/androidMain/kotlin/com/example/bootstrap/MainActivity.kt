package com.example.bootstrap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity: ComponentActivity() {
    private fun setupSplashScreen() {
        var keepSplashScreenOn = true
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(1500)
                keepSplashScreenOn = false
            }
        }
        installSplashScreen().setKeepOnScreenCondition { keepSplashScreenOn }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}
