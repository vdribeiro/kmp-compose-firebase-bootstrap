package com.example.bootstrap.usecase.session.local

import com.example.bootstrap.database.getFlow
import database.AppDatabase
import kotlinx.coroutines.flow.Flow

internal class UserDao(
    database: AppDatabase
): UserLocal {

    private val sessionDao = database.sessionQueries

    override fun observeLoggedUserId(): Flow<String?> =
        sessionDao.getLoggedUserId().getFlow { list -> list.firstOrNull() }

    override fun getUserToken(id: String): String? =
        sessionDao.getUserToken(userId = id).executeAsOneOrNull()?.token
}
