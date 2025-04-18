package com.example.bootstrap.firebase.storage

import com.example.bootstrap.file.getNameAndExtensionFromUrl
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.tryAwait
import com.example.bootstrap.firebase.tryAwaitWithResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileInputStream

internal actual val Firebase.storage: Storage get() = Storage(storage = FirebaseStorage.getInstance())

internal actual class Storage(private val storage: FirebaseStorage) {

    actual var maxUploadRetryTimeMillis: Long
        get() = storage.maxUploadRetryTimeMillis
        set(value) {
            storage.maxUploadRetryTimeMillis = value
        }

    actual var maxDownloadRetryTimeMillis: Long
        get() = storage.maxDownloadRetryTimeMillis
        set(value) {
            storage.maxDownloadRetryTimeMillis = value
        }

    actual val root: Reference get() = Reference(reference = storage.reference)

    actual fun child(path: String): Reference = Reference(reference = storage.getReference(path))

    actual fun url(url: String): Reference = Reference(reference = storage.getReferenceFromUrl(url))
}

@Suppress("BlockingMethodInNonBlockingContext")
internal actual class Reference(private val reference: StorageReference) {
    actual val name: String get() = reference.name
    actual val path: String get() = reference.path
    actual val root: Reference get() = Reference(reference = reference.root)
    actual val parent: Reference? get() = reference.parent?.let { Reference(reference = it) }
    actual fun child(path: String): Reference = Reference(reference = reference.child(path))
    actual suspend fun url(): String? = reference.downloadUrl.tryAwaitWithResult()?.toString()

    actual suspend fun list(): List<Reference> =
        reference.listAll().tryAwaitWithResult()?.items?.map { Reference(reference = it) } ?: emptyList()

    actual suspend fun delete(): Boolean =
        reference.delete().tryAwait()

    actual suspend fun uploadBytes(bytes: ByteArray): Boolean =
        reference.putBytes(bytes).tryAwait()

    actual suspend fun uploadFile(path: String): Boolean =
        reference.putStream(FileInputStream(File(path))).tryAwait()

    actual suspend fun downloadBytes(maxSize: Long): ByteArray? =
        reference.getBytes(maxSize).tryAwaitWithResult()

    actual suspend fun downloadFile(): String? = try {
        val pair = getNameAndExtensionFromUrl(url = path) ?: throw Throwable(message = "Invalid URL")
        val file = File.createTempFile(pair.first, ".${pair.second}")
        reference.getFile(file).tryAwaitWithResult()
        file.absolutePath
    } catch (throwable: Throwable) {
        null
    }
}
