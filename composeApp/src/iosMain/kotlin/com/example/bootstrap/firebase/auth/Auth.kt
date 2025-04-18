package com.example.bootstrap.firebase.auth

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRUser
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.throwError
import com.example.bootstrap.tryAwait
import com.example.bootstrap.tryAwaitWithResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSURL

internal actual val Firebase.auth: Auth get() = com.example.bootstrap.firebase.auth.Auth(auth = FIRAuth.auth())

internal actual class Auth(private val auth: FIRAuth) {
    actual val currentUser: User? get() = auth.currentUser?.let { User(user = it) }

    actual val user: Flow<User?>
        get() = callbackFlow {
            val handle = auth.addIDTokenDidChangeListener { _, user ->
                trySend(element = user?.let { User(user = it) })
            }
            awaitClose { auth.removeIDTokenDidChangeListener(listenerHandle = handle) }
        }

    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): User? =
        auth.tryAwaitWithResult { createUserWithEmail(email = email, password = password, completion = it) }?.user?.let {
            User(
                user = it
            )
        }

    actual suspend fun signInWithEmailAndPassword(email: String, password: String): User? =
        auth.tryAwaitWithResult { signInWithEmail(email = email, password = password, completion = it) }?.user?.let {
            User(
                user = it
            )
        }

    actual suspend fun signOut() {
        auth.throwError { signOut(error = it) }
    }
}

internal actual class User(private val user: FIRUser) {
    actual val id: String get() = user.uid
    actual val email: String? get() = user.email
    actual val name: String? get() = user.displayName
    actual val photoUrl: String? get() = user.photoURL?.absoluteString
    actual val lastSignInAt: Long? get() = user.metadata.lastSignInDate?.timeIntervalSinceReferenceDate?.toLong()

    actual suspend fun getIdToken(forceRefresh: Boolean): String? =
        user.tryAwaitWithResult { getIDTokenForcingRefresh(forceRefresh = forceRefresh, completion = it) }

    actual suspend fun reload(): Boolean =
        user.tryAwait { reloadWithCompletion(completion = it) }

    actual suspend fun delete(): Boolean =
        user.tryAwait { deleteWithCompletion(completion = it) }

    actual suspend fun updateName(name: String?): Boolean =
        user.tryAwait {
            profileChangeRequest().apply {
                setDisplayName(displayName = name)
            }.commitChangesWithCompletion(completion = it)
        }

    actual suspend fun updatePhotoUrl(photoUrl: String?): Boolean =
        user.tryAwait {
            profileChangeRequest().apply {
                setPhotoURL(photoURL = photoUrl?.let { url -> NSURL.URLWithString(URLString = url) })
            }.commitChangesWithCompletion(completion = it)
        }

    actual suspend fun updateEmail(email: String): Boolean =
        user.tryAwait { updateEmail(email = email, completion = it) }

    actual suspend fun updatePassword(password: String): Boolean =
        user.tryAwait { updatePassword(password = password, completion = it) }
}
