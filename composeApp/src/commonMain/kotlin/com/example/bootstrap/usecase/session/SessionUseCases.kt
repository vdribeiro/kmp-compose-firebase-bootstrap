package com.example.bootstrap.usecase.session

import com.example.bootstrap.scheduler.domain.JobRequest
import com.example.bootstrap.scheduler.result.JobResult
import com.example.bootstrap.usecase.session.model.params.Credentials
import com.example.bootstrap.usecase.session.model.result.User
import com.example.bootstrap.usecase.session.model.result.UserResult

internal interface SessionUseCases {

    /**
     * Sync users from the api.
     */
    suspend fun syncUsers()

    /**
     * Sync users from the api by [jobRequest].
     */
    suspend fun syncUsers(jobRequest: JobRequest): JobResult

    /**
     * Get user given its [id].
     */
    suspend fun getUserById(id: String): User?

    /**
     * Create user with [credentials].
     */
    suspend fun createUserWithEmailAndPassword(credentials: Credentials): UserResult

    /**
     * Login with [credentials].
     */
    suspend fun signInWithEmailAndPassword(credentials: Credentials): UserResult

    /**
     * Upsert user by [jobRequest].
     */
    suspend fun upsertUser(jobRequest: JobRequest): JobResult

    /**
     * Update user [password].
     */
    suspend fun updateUserPassword(password: String): UserResult

    /**
     * Update user [name].
     */
    suspend fun updateUserName(name: String?): UserResult

    /**
     * Update user [photoUrl].
     */
    suspend fun updateUserPhotoUrl(photoUrl: String?): UserResult

    /**
     * Logout current user.
     */
    suspend fun signOut()
}
