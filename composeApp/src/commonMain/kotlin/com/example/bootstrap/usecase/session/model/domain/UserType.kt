package com.example.bootstrap.usecase.session.model.domain

enum class UserType {
    ADMIN,
    EDITOR;

    companion object {
        private val map = entries.associateBy(UserType::name)
        fun fromString(name: String): UserType? = map[name.uppercase()]
    }
}
