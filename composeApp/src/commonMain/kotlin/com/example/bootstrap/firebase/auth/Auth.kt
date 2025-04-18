package com.example.bootstrap.firebase.auth

import com.example.bootstrap.firebase.Firebase
import kotlinx.coroutines.flow.Flow

/**
 * Entry point of Firebase Auth.
 */
internal expect val Firebase.auth: Auth

internal expect class Auth {
    /**
     * Current user or null if the user does not exist or was invalidated or logged out.
     */
    val currentUser: User?
    /**
     * User flow for when there is a change in the authentication state or user token.
     */
    val user: Flow<User?>

    /**
     * Create a new user account with the given [email] address and [password].
     * If it succeeds the new user is returned, null otherwise.
     */
    suspend fun createUserWithEmailAndPassword(email: String, password: String): User?
    /**
     * Sign in with the given [email] address and [password].
     * If it succeeds the user is returned, null otherwise.
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): User?
    /**
     * Sign out current user.
     */
    suspend fun signOut()
}

internal expect class User {
    val id: String
    val email: String?
    val name: String?
    val photoUrl: String?
    val lastSignInAt: Long?

    /**
     * Fetches a Firebase Auth ID Token for the user.
     * The default behaviour is [forceRefresh] set to false so the service decides whether
     * it comes from the cache or remotely; if set to true it forces the latter.
     */
    suspend fun getIdToken(forceRefresh: Boolean = false): String?

    /**
     * Refresh the data of the current user. Returns true if successful, false otherwise.
     */
    suspend fun reload(): Boolean
    /**
     * Deletes the user from Firebase. Returns true if successful, false otherwise.
     */
    suspend fun delete(): Boolean

    /**
     * Update user name. Returns true if successful, false otherwise.
     */
    suspend fun updateName(name: String?): Boolean
    /**
     * Update user photo url. Returns true if successful, false otherwise.
     */
    suspend fun updatePhotoUrl(photoUrl: String?): Boolean

    /**
     * Update user email. Returns true if successful, false otherwise.
     */
    suspend fun updateEmail(email: String): Boolean
    /**
     * Update user password. Returns true if successful, false otherwise.
     */
    suspend fun updatePassword(password: String): Boolean
}
