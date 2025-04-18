package com.example.bootstrap.usecase.session.model.result

internal sealed class UserResult {
    data class Success(val user: User): UserResult()
    data object Error: UserResult()
}
