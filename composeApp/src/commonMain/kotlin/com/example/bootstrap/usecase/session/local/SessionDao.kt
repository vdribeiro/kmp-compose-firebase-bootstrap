package com.example.bootstrap.usecase.session.local

import com.example.bootstrap.usecase.session.model.domain.SessionHash
import com.example.bootstrap.usecase.session.model.domain.User
import com.example.bootstrap.usecase.session.model.toDomain
import com.example.bootstrap.usecase.session.model.toSchema
import database.AppDatabase

internal class SessionDao(
    database: AppDatabase
): SessionLocal {

    private val userDao = database.userQueries
    private val sessionDao = database.sessionQueries

    override fun getUserById(id: String): User? =
        userDao.getUser(id = id).executeAsOneOrNull()?.toDomain()

    override fun getUserByHash(hash: String): User? =
        sessionDao.getUserBySessionHash(hash = hash).executeAsOneOrNull()?.toDomain()

    override fun syncUsers(users: List<User>) =
        userDao.transaction {
            userDao.truncateUsers()
            users.forEach { upsertUser(it) }
        }

    override fun upsertUser(user: User) =
        userDao.upsertUser(User = user.toSchema())

    override fun tryUpsertUser(user: User) =
        userDao.transaction {
            val localUser = userDao.getUser(id = user.id).executeAsOneOrNull()
            if (localUser?.jobId == user.jobId) upsertUser(user = user.copy(jobId = null))
        }

    override fun upsertSessionHash(sessionHash: SessionHash) =
        sessionDao.upsertSessionHash(SessionHash = sessionHash.toSchema())

    override fun updateLoggedUser(user: User?) =
        sessionDao.upsertSession(userId = user?.id)
}
