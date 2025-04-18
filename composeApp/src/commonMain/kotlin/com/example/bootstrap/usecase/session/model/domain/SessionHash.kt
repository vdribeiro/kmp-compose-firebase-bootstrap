package com.example.bootstrap.usecase.session.model.domain

internal data class SessionHash(
    val userId: String,
    val hash: String,
    val token: String?
)
