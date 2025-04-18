package com.example.bootstrap.usecase.session.remote

import com.example.bootstrap.http.request.RequestPart
import com.example.bootstrap.usecase.session.model.domain.User
import com.example.bootstrap.usecase.session.remote.result.UserResult
import com.example.bootstrap.usecase.session.remote.result.UsersResult

internal interface SessionRemote {

    /**
     * Get users from the API given the [requestPart].
     */
    suspend fun getUsers(requestPart: RequestPart): UsersResult

    /**
     * Get user from the API given the [requestPart] and user [id].
     */
    suspend fun getUser(requestPart: RequestPart, id: String): UserResult

    /**
     * Post a user to the API given its [requestPart] and [user].
     */
    suspend fun postUser(requestPart: RequestPart, user: User): UserResult
}
