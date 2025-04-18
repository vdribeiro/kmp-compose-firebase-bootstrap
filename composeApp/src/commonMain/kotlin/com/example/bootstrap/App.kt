package com.example.bootstrap

import androidx.compose.runtime.Composable
import com.example.bootstrap.core.Action
import com.example.bootstrap.core.Core
import com.example.bootstrap.core.MainScreen
import com.example.bootstrap.core.NavigationStore
import com.example.bootstrap.core.State
import com.example.bootstrap.ui.Store
import com.example.bootstrap.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        MainScreen(store = navigation)
    }
}

private val navigation: Store<Action, State> = with(Core.INSTANCE) {
    NavigationStore(
        toggle = toggle,
        dispatcher = dispatcher,
        userId = userId,
        useCases = useCases
    )
}
