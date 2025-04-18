package com.example.bootstrap.usecase.session.model.result

import com.example.bootstrap.datetime.DateTime
import com.example.bootstrap.file.File
import com.example.bootstrap.usecase.session.model.domain.UserType

internal data class User(
    val id: String,
    val email: String?,
    val name: String?,
    val photo: File?,
    val type: UserType,
    val modifiedAt: DateTime,
    val lastSignInAt: DateTime?
) {
    val identifier: String? = name ?: email

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return id == (other as User).id
    }

    override fun hashCode(): Int = id.hashCode()
}
