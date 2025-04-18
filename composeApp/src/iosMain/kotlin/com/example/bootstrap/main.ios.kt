package com.example.bootstrap

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainView(): UIViewController = ComposeUIViewController { App() }
