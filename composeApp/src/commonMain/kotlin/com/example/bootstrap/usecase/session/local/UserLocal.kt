package com.example.bootstrap.usecase.session.local

import kotlinx.coroutines.flow.Flow

internal interface UserLocal {

    /**
     * Observe logged user id from the database.
     */
    fun observeLoggedUserId(): Flow<String?>

    /**
     * Get user token from the database given its [id].
     */
    fun getUserToken(id: String): String?
}
