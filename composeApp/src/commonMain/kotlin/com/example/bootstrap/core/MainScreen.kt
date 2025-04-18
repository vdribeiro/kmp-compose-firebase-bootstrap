package com.example.bootstrap.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bootstrap.ui.Store

@Composable
internal fun MainScreen(store: Store<Action, State>) {
    val storeState by store.stateFlow.collectAsState()
}
