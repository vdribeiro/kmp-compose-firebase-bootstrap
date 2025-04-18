package com.example.bootstrap.firebase.firestore

import cocoapods.FirebaseFirestore.FIRDocumentSnapshot
import cocoapods.FirebaseFirestore.FIRFirestore
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.tryAwait
import com.example.bootstrap.tryAwaitWithResult

internal actual val Firebase.firestore: Firestore get() = com.example.bootstrap.firebase.firestore.Firestore(FIRFirestore.firestore())

internal actual class Firestore(private val firestore: FIRFirestore) {

    actual suspend fun disableNetwork(): Boolean = firestore.tryAwait { disableNetworkWithCompletion(completion = it) }

    actual suspend fun enableNetwork(): Boolean = firestore.tryAwait { enableNetworkWithCompletion(completion = it) }

    actual suspend fun getCollection(collection: String): List<Map<String, Any?>> =
        firestore.collectionWithPath(collectionPath = collection)
            .tryAwaitWithResult { getDocumentsWithCompletion(completion = it) }?.documents?.mapNotNull {
                (it as FIRDocumentSnapshot).run { withId(map = it.data(), id = it.documentID) }
            } ?: emptyList()

    actual suspend fun getDocument(collection: String, documentId: String): Map<String, Any?>? =
        firestore.collectionWithPath(collectionPath = collection)
            .documentWithPath(documentPath = documentId).tryAwaitWithResult {
                getDocumentWithCompletion(completion = it)
            }?.let { withId(map = it.data(), id = it.documentID) }

    actual suspend fun setDocument(collection: String, documentId: String, document: Map<String, Any?>): Boolean =
        firestore.collectionWithPath(collectionPath = collection)
            .documentWithPath(documentPath = documentId).tryAwait {
                setData(documentData = withoutId(map = document))
            }

    actual suspend fun removeDocument(collection: String, documentId: String): Boolean =
        firestore.collectionWithPath(collectionPath = collection)
            .documentWithPath(documentPath = documentId).tryAwait {
                deleteDocument()
            }

    @Suppress("UNCHECKED_CAST")
    private fun withId(map: Map<Any?, *>?, id: String): Map<String, Any?>? =
        map?.toMutableMap()?.apply { put(key = "id", value = id) } as Map<String, Any?>?

    @Suppress("UNCHECKED_CAST")
    private fun withoutId(map: Map<String, Any?>): Map<Any?, *> =
        map.toMutableMap().apply { remove(key = "id") } as Map<Any?, *>
}
