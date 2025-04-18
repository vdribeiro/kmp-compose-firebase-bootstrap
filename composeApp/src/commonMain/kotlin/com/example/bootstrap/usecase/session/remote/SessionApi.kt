package com.example.bootstrap.usecase.session.remote

import com.example.bootstrap.http.body.Body
import com.example.bootstrap.http.client.HttpClient
import com.example.bootstrap.http.request.RequestPart
import com.example.bootstrap.http.request.toRequest
import com.example.bootstrap.logger.Logger
import com.example.bootstrap.usecase.session.model.domain.User
import com.example.bootstrap.usecase.session.model.toApi
import com.example.bootstrap.usecase.session.model.toDomain
import com.example.bootstrap.usecase.session.remote.result.UserResult
import com.example.bootstrap.usecase.session.remote.result.UsersResult

internal class SessionApi(
    private val httpClient: HttpClient
): SessionRemote {

    override suspend fun getUsers(requestPart: RequestPart): UsersResult = try {
        val request = requestPart.toRequest(
            path = "user"
        )
        val response = httpClient.get<List<Map<String, Any>>>(request = request)

        val users = response.body.mapNotNull { it.toDomain() }
        UsersResult.Success(users = users)
    } catch (throwable: Throwable) {
        Logger.error(message = throwable.message.orEmpty())
        UsersResult.Error(error = throwable.message.orEmpty())
    }

    override suspend fun getUser(requestPart: RequestPart, id: String): UserResult = try {
        val request = requestPart.toRequest(
            path = "user/$id"
        )
        val response = httpClient.get<Map<String, Any>?>(request = request)

        val user = response.body?.toDomain() ?: throw Throwable("Invalid data")
        UserResult.Success(user = user)
    } catch (throwable: Throwable) {
        Logger.error(message = throwable.message.orEmpty())
        UserResult.Error(error = throwable.message.orEmpty())
    }

    override suspend fun postUser(requestPart: RequestPart, user: User): UserResult = try {
        val request = requestPart.toRequest(
            path = "user/${user.id}",
            body = Body.Form(map = user.toApi())
        )
        val response = httpClient.post<Map<String, Any>>(request = request)

        val newUser = response.body.toDomain() ?: throw Throwable("Invalid data")
        UserResult.Success(user = newUser)
    } catch (throwable: Throwable) {
        Logger.error(message = throwable.message.orEmpty())
        UserResult.Error(error = throwable.message.orEmpty())
    }
}
