package com.example.bootstrap.usecase.session.model.domain

import com.example.bootstrap.datetime.DateTime

internal data class User(
    val id: String,
    val email: String?,
    val jobId: String?,
    val name: String?,
    val photoUrl: String?,
    val type: UserType,
    val modifiedAt: DateTime,
    val lastSignInAt: DateTime?
)
