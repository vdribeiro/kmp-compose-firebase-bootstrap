package com.example.bootstrap.usecase.session

import com.example.bootstrap.datetime.DateTime
import com.example.bootstrap.file.Files
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.auth.auth
import com.example.bootstrap.http.request.RequestPart
import com.example.bootstrap.http.toStringMap
import com.example.bootstrap.scheduler.Scheduler
import com.example.bootstrap.scheduler.domain.JobRequest
import com.example.bootstrap.scheduler.domain.JobType
import com.example.bootstrap.scheduler.mapper.toRequestPart
import com.example.bootstrap.scheduler.result.JobResult
import com.example.bootstrap.security.generateUuid
import com.example.bootstrap.usecase.session.local.SessionLocal
import com.example.bootstrap.usecase.session.model.domain.SessionHash
import com.example.bootstrap.usecase.session.model.domain.User
import com.example.bootstrap.usecase.session.model.params.Credentials
import com.example.bootstrap.usecase.session.model.result.UserResult
import com.example.bootstrap.usecase.session.model.toApi
import com.example.bootstrap.usecase.session.model.toDomain
import com.example.bootstrap.usecase.session.model.toHash
import com.example.bootstrap.usecase.session.model.toResult
import com.example.bootstrap.usecase.session.model.toUser
import com.example.bootstrap.usecase.session.remote.SessionRemote
import com.example.bootstrap.usecase.session.remote.result.UsersResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import com.example.bootstrap.usecase.session.model.result.User as ResultUser

internal class SessionGateway(
    private val userId: Flow<String?>,
    private val storage: Files,
    private val scheduler: Scheduler,
    private val sessionApi: SessionRemote,
    private val sessionDao: SessionLocal
): SessionUseCases {

    override suspend fun syncUsers() {
        scheduler.insert(
            jobRequest = JobRequest(
                userId = userId.firstOrNull(),
                type = JobType.SYNC_USERS
            )
        )
    }

    override suspend fun syncUsers(jobRequest: JobRequest): JobResult {
        val requestPart = jobRequest.toRequestPart()
        return when (val result = sessionApi.getUsers(requestPart = requestPart)) {
            is UsersResult.Error -> JobResult.Retry
            is UsersResult.Success -> JobResult.Success.also {
                sessionDao.syncUsers(users = result.users)
            }
        }
    }

    override suspend fun getUserById(id: String): ResultUser? =
        sessionDao.getUserById(id = id)?.toResult(storage = storage)

    override suspend fun createUserWithEmailAndPassword(credentials: Credentials): UserResult {
        if (!credentials.isValid()) return UserResult.Error

        val firebaseUser = Firebase.auth.createUserWithEmailAndPassword(
            email = credentials.email,
            password = credentials.password
        ) ?: return UserResult.Error
        val user = firebaseUser.toUser()
        val sessionHash = SessionHash(
            userId = user.id,
            hash = credentials.toHash(),
            token = firebaseUser.getIdToken()
        )

        sessionDao.upsertUser(user = user)
        sessionDao.upsertSessionHash(sessionHash = sessionHash)
        upsertUser(user = user)

        return UserResult.Success(user = user.toResult(storage = storage))
    }

    override suspend fun signInWithEmailAndPassword(credentials: Credentials): UserResult {
        if (!credentials.isValid()) return UserResult.Error

        val firebaseUser = Firebase.auth.signInWithEmailAndPassword(
            email = credentials.email,
            password = credentials.password
        )
        val user = if (firebaseUser == null) {
            // Offline sign in
            sessionDao.getUserByHash(hash = credentials.toHash())
        } else {
            sessionDao.getUserById(id = firebaseUser.id) ?: when (val result =
                sessionApi.getUser(requestPart = RequestPart(), id = firebaseUser.id)) {
                is com.example.bootstrap.usecase.session.remote.result.UserResult.Error -> firebaseUser.toUser()
                is com.example.bootstrap.usecase.session.remote.result.UserResult.Success -> result.user
            }.also {
                val sessionHash = SessionHash(
                    userId = it.id,
                    hash = credentials.toHash(),
                    token = firebaseUser.getIdToken()
                )
                sessionDao.upsertSessionHash(sessionHash = sessionHash)
            }
        }?.copy(modifiedAt = DateTime()) ?: return UserResult.Error

        sessionDao.updateLoggedUser(user = user)
        upsertUser(user = user)

        return UserResult.Success(user = user.toResult(storage = storage))
    }

    private suspend fun upsertUser(user: User) {
        val jobId = generateUuid()
        sessionDao.upsertUser(user = user.copy(jobId = jobId))
        scheduler.insert(
            jobRequest = JobRequest(
                id = jobId,
                userId = userId.firstOrNull(),
                type = JobType.UPSERT_USER,
                params = user.toApi().toStringMap()
            )
        )
    }

    override suspend fun upsertUser(jobRequest: JobRequest): JobResult {
        val requestPart = jobRequest.toRequestPart()
        val user = jobRequest.params.toDomain() ?: return JobResult.Error
        return when (val result = sessionApi.postUser(requestPart = requestPart, user = user)) {
            is com.example.bootstrap.usecase.session.remote.result.UserResult.Error -> JobResult.Retry
            is com.example.bootstrap.usecase.session.remote.result.UserResult.Success -> JobResult.Success.also {
                sessionDao.tryUpsertUser(user = result.user.copy(jobId = jobRequest.id))
            }
        }
    }

    override suspend fun updateUserPassword(password: String): UserResult {
        val currentUser = Firebase.auth.currentUser
        val id = currentUser?.id ?: return UserResult.Error
        val user = sessionDao.getUserById(id = id)?.copy(
            modifiedAt = DateTime()
        ) ?: return UserResult.Error

        upsertUser(user = user)

        val result = if (currentUser.updatePassword(password = password)) {
            UserResult.Success(user = user.toResult(storage = storage))
        } else UserResult.Error

        val sessionHash = SessionHash(
            userId = user.id,
            hash = Credentials(email = id, password = password).toHash(),
            token = currentUser.getIdToken()
        )
        sessionDao.upsertSessionHash(sessionHash = sessionHash)

        signOut()
        return result
    }

    override suspend fun updateUserName(name: String?): UserResult {
        val currentUser = Firebase.auth.currentUser
        val id = currentUser?.id ?: return UserResult.Error
        val user = sessionDao.getUserById(id = id)?.copy(
            name = name,
            modifiedAt = DateTime()
        ) ?: return UserResult.Error

        upsertUser(user = user)

        return if (currentUser.updateName(name = name)) {
            UserResult.Success(user = user.toResult(storage = storage))
        } else UserResult.Error
    }

    override suspend fun updateUserPhotoUrl(photoUrl: String?): UserResult {
        val currentUser = Firebase.auth.currentUser
        val id = currentUser?.id ?: return UserResult.Error
        val user = sessionDao.getUserById(id = id)?.copy(
            photoUrl = photoUrl,
            modifiedAt = DateTime()
        ) ?: return UserResult.Error

        upsertUser(user = user)

        return if (currentUser.updatePhotoUrl(photoUrl = photoUrl)) {
            UserResult.Success(user = user.toResult(storage = storage))
        } else UserResult.Error
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
        sessionDao.updateLoggedUser(user = null)
    }
}
