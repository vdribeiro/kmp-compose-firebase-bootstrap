package com.example.bootstrap.firebase.storage

import com.example.bootstrap.firebase.Firebase

/**
 * Entry point of Firebase Storage.
 */
internal expect val Firebase.storage: Storage

internal expect class Storage {
    /**
     * Maximum time in milliseconds to retry an upload if a failure occurs.
     */
    var maxUploadRetryTimeMillis: Long

    /**
     * Maximum time in milliseconds to retry a download if a failure occurs.
     */
    var maxDownloadRetryTimeMillis: Long

    /**
     * Get root path.
     */
    val root: Reference
    /**
     * Get directory from child [path].
     */
    fun child(path: String): Reference
    /**
     * Get path directory from [url].
     * Warning: URL string should be escaped!
     */
    fun url(url: String): Reference
}

internal expect class Reference {
    /**
     * Get file name.
     */
    val name: String
    /**
     * Get file path.
     */
    val path: String
    /**
     * Get root path.
     */
    val root: Reference
    /**
     * Get parent directory, or null if in root.
     */
    val parent: Reference?
    /**
     * Get child [path] directory.
     */
    fun child(path: String): Reference
    /**
     * File url.
     */
    suspend fun url(): String?

    /**
     * List directories.
     */
    suspend fun list(): List<Reference>

    /**
     * Delete this file.
     * Returns true if successful, false otherwise.
     */
    suspend fun delete(): Boolean

    /**
     * Upload [bytes] from memory.
     * Returns true if successful, false otherwise.
     */
    suspend fun uploadBytes(bytes: ByteArray): Boolean

    /**
     * Upload file from disk given its absolute [path].
     * Returns true if successful, false otherwise.
     */
    suspend fun uploadFile(path: String): Boolean

    /**
     * Download bytes to memory given a [maxSize] of bytes to allocate to the buffer to protect memory overflow.
     * Returns the byte array if successful, null otherwise.
     */
    suspend fun downloadBytes(maxSize: Long): ByteArray?

    /**
     * Download file to disk.
     * Returns the file absolute [path] if successful, null otherwise.
     */
    suspend fun downloadFile(): String?
}
