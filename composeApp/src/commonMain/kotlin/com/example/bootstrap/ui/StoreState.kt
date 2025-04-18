package com.example.bootstrap.ui

internal data class StoreState<State>(
    val userId: String? = null,
    val working: Boolean = false,
    val state: State
)
