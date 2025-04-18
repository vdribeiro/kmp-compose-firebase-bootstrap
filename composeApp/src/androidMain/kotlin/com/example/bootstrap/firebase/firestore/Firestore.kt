package com.example.bootstrap.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.tryAwait
import com.example.bootstrap.firebase.tryAwaitWithResult

internal actual val Firebase.firestore: Firestore get() = Firestore(firestore = FirebaseFirestore.getInstance())

internal actual class Firestore(private val firestore: FirebaseFirestore) {

    actual suspend fun disableNetwork(): Boolean = firestore.disableNetwork().tryAwait()

    actual suspend fun enableNetwork(): Boolean = firestore.enableNetwork().tryAwait()

    actual suspend fun getCollection(collection: String): List<Map<String, Any?>> =
        firestore.collection(collection).get().tryAwaitWithResult()?.documents?.mapNotNull {
            withId(map = it.data, id = it.id)
        } ?: emptyList()

    actual suspend fun getDocument(collection: String, documentId: String): Map<String, Any?>? =
        firestore.collection(collection).document(documentId).get().tryAwaitWithResult()?.let {
            withId(map = it.data, id = it.id)
        }

    actual suspend fun setDocument(collection: String, documentId: String, document: Map<String, Any?>): Boolean =
        firestore.collection(collection).document(documentId).set(withoutId(document)).tryAwait()

    actual suspend fun removeDocument(collection: String, documentId: String): Boolean =
        firestore.collection(collection).document(documentId).delete().tryAwait()

    private fun withId(map: Map<String, Any?>?, id: String): Map<String, Any?>? =
        map?.toMutableMap()?.apply { put(key = "id", value = id) }

    private fun withoutId(map: Map<String, Any?>): Map<String, Any?> =
        map.toMutableMap().apply { remove(key = "id") }
}
