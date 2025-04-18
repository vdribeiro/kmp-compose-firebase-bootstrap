package com.example.bootstrap.usecase.session.local

import com.example.bootstrap.usecase.session.model.domain.SessionHash
import com.example.bootstrap.usecase.session.model.domain.User

internal interface SessionLocal {

    /**
     * Get user from the database given its [id].
     */
    fun getUserById(id: String): User?

    /**
     * Get user from the database given its session [hash].
     */
    fun getUserByHash(hash: String): User?

    /**
     * Sync [users] in the database.
     */
    fun syncUsers(users: List<User>)

    /**
     * Upsert in the database the given [user].
     */
    fun upsertUser(user: User)

    /**
     * Upsert in the database the given [user] if its job id matches.
     */
    fun tryUpsertUser(user: User)

    /**
     * Upsert in the database the given [sessionHash].
     */
    fun upsertSessionHash(sessionHash: SessionHash)

    /**
     * Update in the database the last logged [user].
     */
    fun updateLoggedUser(user: User?)
}
