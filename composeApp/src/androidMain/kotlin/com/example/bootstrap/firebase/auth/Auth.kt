package com.example.bootstrap.firebase.auth

import androidx.core.net.toUri
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.tryAwait
import com.example.bootstrap.firebase.tryAwaitWithResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal actual val Firebase.auth: Auth get() = Auth(auth = FirebaseAuth.getInstance())

internal actual class Auth(private val auth: FirebaseAuth) {
    actual val currentUser: User? get() = auth.currentUser?.let { User(user = it) }

    actual val user: Flow<User?>
        get() = callbackFlow {
            val listener = FirebaseAuth.IdTokenListener { auth ->
                trySend(element = auth.currentUser?.let { User(user = it) })
            }
            auth.addIdTokenListener(listener)
            awaitClose { auth.removeIdTokenListener(listener) }
        }

    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): User? =
        auth.createUserWithEmailAndPassword(email, password).tryAwaitWithResult()?.user?.let { User(user = it) }

    actual suspend fun signInWithEmailAndPassword(email: String, password: String): User? =
        auth.signInWithEmailAndPassword(email, password).tryAwaitWithResult()?.user?.let { User(user = it) }

    actual suspend fun signOut() = auth.signOut()
}

internal actual class User(private val user: FirebaseUser) {
    actual val id: String get() = user.uid
    actual val email: String? get() = user.email
    actual val name: String? get() = user.displayName
    actual val photoUrl: String? get() = user.photoUrl?.toString()
    actual val lastSignInAt: Long? get() = user.metadata?.lastSignInTimestamp

    actual suspend fun getIdToken(forceRefresh: Boolean): String? =
        user.getIdToken(forceRefresh).tryAwaitWithResult()?.token

    actual suspend fun reload(): Boolean = user.reload().tryAwait()

    actual suspend fun delete(): Boolean = user.delete().tryAwait()

    actual suspend fun updateName(name: String?): Boolean =
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        ).tryAwait()

    actual suspend fun updatePhotoUrl(photoUrl: String?): Boolean =
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUrl?.toUri())
                .build()
        ).tryAwait()

    actual suspend fun updateEmail(email: String): Boolean =
        user.updateEmail(email).tryAwait()

    actual suspend fun updatePassword(password: String): Boolean =
        user.updatePassword(password).tryAwait()
}
