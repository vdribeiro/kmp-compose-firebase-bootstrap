package com.example.bootstrap.usecase.session.model.result

internal sealed class UsersResult {
    data class Success(val users: List<User>): UsersResult()
    data object Error: UsersResult()
}
