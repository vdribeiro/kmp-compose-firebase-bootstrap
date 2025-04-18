package com.example.bootstrap.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bootstrap.ui.Store

@Composable
internal fun MainScreen(store: Store<Action, State>) {
    val storeState by store.stateFlow.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)


}
