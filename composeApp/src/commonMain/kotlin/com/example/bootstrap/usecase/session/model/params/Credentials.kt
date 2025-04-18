package com.example.bootstrap.usecase.session.model.params

internal data class Credentials(
    val email: String,
    val password: String
) {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()

    fun isValid(): Boolean = with(email) { isNotBlank() && matches(emailRegex) } && with(password) { isNotBlank() && length >= 8 }
}
