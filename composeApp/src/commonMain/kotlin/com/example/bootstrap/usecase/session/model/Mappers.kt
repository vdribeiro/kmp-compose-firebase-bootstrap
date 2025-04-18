package com.example.bootstrap.usecase.session.model

import com.example.bootstrap.database.SessionHashSchema
import com.example.bootstrap.database.UserSchema
import com.example.bootstrap.datetime.DateTime
import com.example.bootstrap.datetime.toDateTime
import com.example.bootstrap.file.Files
import com.example.bootstrap.http.getDateTime
import com.example.bootstrap.http.getString
import com.example.bootstrap.security.getHashString
import com.example.bootstrap.usecase.session.model.domain.SessionHash
import com.example.bootstrap.usecase.session.model.domain.User
import com.example.bootstrap.usecase.session.model.domain.UserType
import com.example.bootstrap.usecase.session.model.params.Credentials
import com.example.bootstrap.firebase.auth.User as FirebaseUser
import com.example.bootstrap.usecase.session.model.result.User as UserResult

//region prototype

internal fun FirebaseUser.toUser(): User =
    User(
        id = id,
        email = email,
        jobId = null,
        name = name,
        photoUrl = photoUrl,
        type = UserType.EDITOR,
        modifiedAt = DateTime(),
        lastSignInAt = lastSignInAt?.toDateTime()
    )

internal fun Credentials.toHash(): String =
    getHashString(value = email + password)

//endregion

//region database

internal fun UserSchema.toDomain(): User =
    User(
        id = id,
        email = email,
        jobId = jobId,
        name = name,
        photoUrl = photoUrl,
        type = type,
        modifiedAt = modifiedAt,
        lastSignInAt = lastSignInAt
    )

internal fun User.toSchema(): UserSchema =
    UserSchema(
        id = id,
        email = email,
        jobId = jobId,
        name = name,
        photoUrl = photoUrl,
        type = type,
        modifiedAt = modifiedAt,
        lastSignInAt = lastSignInAt
    )

internal fun SessionHash.toSchema(): SessionHashSchema =
    SessionHashSchema(
        userId = userId,
        hash = hash,
        token = token
    )

//endregion

//region api

internal fun Map<String, Any?>.toDomain(): User? {
    return User(
        id = getString(key = "id") ?: return null,
        email = getString(key = "email"),
        jobId = null,
        name = getString(key = "name"),
        photoUrl = getString(key = "photoUrl"),
        type = getString(key = "type")?.let { UserType.fromString(it) } ?: return null,
        modifiedAt = getDateTime(key = "modifiedAt") ?: return null,
        lastSignInAt = getDateTime(key = "lastSignInAt")
    )
}

internal fun User.toApi(): Map<String, Any?> =
    buildMap {
        put(key = "id", value = id)
        put(key = "email", value = email)
        put(key = "name", value = name)
        put(key = "photoUrl", value = photoUrl)
        put(key = "type", value = type.name)
        put(key = "modifiedAt", value = modifiedAt.toString())
        put(key = "lastSignInAt", value = lastSignInAt?.toString())
    }

//endregion

//region result

internal suspend fun User.toResult(storage: Files): UserResult =
    UserResult(
        id = id,
        email = email,
        name = name,
        photo = photoUrl?.let { storage.getFile(url = it) },
        type = type,
        modifiedAt = modifiedAt,
        lastSignInAt = lastSignInAt
    )

//endregion
