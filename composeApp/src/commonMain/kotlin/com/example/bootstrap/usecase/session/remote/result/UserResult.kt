package com.example.bootstrap.usecase.session.remote.result

import com.example.bootstrap.usecase.session.model.domain.User

internal sealed class UserResult {
    data class Success(val user: User): UserResult()
    data class Error(val error: String): UserResult()
}
