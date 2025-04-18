package com.example.bootstrap.usecase.session.remote.result

import com.example.bootstrap.usecase.session.model.domain.User

internal sealed class UsersResult {
    data class Success(val users: List<User>): UsersResult()
    data class Error(val error: String): UsersResult()
}
