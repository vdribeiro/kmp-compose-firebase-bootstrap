package com.example.bootstrap.firebase.firestore

import com.example.bootstrap.firebase.Firebase

/**
 * Entry point of Firebase Firestore.
 */
internal expect val Firebase.firestore: Firestore

internal expect class Firestore {

    /**
     * Disable network access.
     * While the network is disabled, all calls will return results from cache,
     * and any write operations will be queued until network usage is re-enabled.
     */
    suspend fun disableNetwork(): Boolean

    /**
     * Enable network access.
     * Queued write operations will be sent.
     */
    suspend fun enableNetwork(): Boolean

    /**
     * Get all documents of a [collection].
     */
    suspend fun getCollection(collection: String): List<Map<String, Any?>>

    /**
     * Get a document of a [collection] given its [documentId].
     */
    suspend fun getDocument(collection: String, documentId: String): Map<String, Any?>?

    /**
     * Upsert a [document] with [documentId] into a [collection].
     */
    suspend fun setDocument(collection: String, documentId: String, document: Map<String, Any?>): Boolean

    /**
     * Remove a document from a [collection] given its [documentId].
     */
    suspend fun removeDocument(collection: String, documentId: String): Boolean
}
