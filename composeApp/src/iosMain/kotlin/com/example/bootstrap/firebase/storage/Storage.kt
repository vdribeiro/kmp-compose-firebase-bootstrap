package com.example.bootstrap.firebase.storage

import cocoapods.FirebaseStorage.FIRStorage
import cocoapods.FirebaseStorage.FIRStorageReference
import com.example.bootstrap.file.getNameAndExtensionFromUrl
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.toByteArray
import com.example.bootstrap.toNSData
import com.example.bootstrap.tryAwait
import com.example.bootstrap.tryAwaitWithResult
import platform.Foundation.NSURL

internal actual val Firebase.storage: Storage get() = com.example.bootstrap.firebase.storage.Storage(storage = FIRStorage.storage())

internal actual class Storage(private val storage: FIRStorage) {
    actual var maxUploadRetryTimeMillis: Long
        get() = storage.maxUploadRetryTime().toLong()
        set(value) {
            storage.setMaxUploadRetryTime(maxUploadRetryTime = value.toDouble())
        }

    actual var maxDownloadRetryTimeMillis: Long
        get() = storage.maxDownloadRetryTime().toLong()
        set(value) {
            storage.setMaxDownloadRetryTime(maxDownloadRetryTime = value.toDouble())
        }

    actual val root: Reference get() = com.example.bootstrap.firebase.storage.Reference(reference = storage.reference())

    actual fun child(path: String): Reference = com.example.bootstrap.firebase.storage.Reference(reference = storage.referenceWithPath(path = path))

    actual fun url(url: String): Reference = com.example.bootstrap.firebase.storage.Reference(reference = storage.referenceForURL(url = url))
}

internal actual class Reference(private val reference: FIRStorageReference) {
    actual val name: String get() = reference.name()
    actual val path: String get() = reference.fullPath()
    actual val root: Reference get() = com.example.bootstrap.firebase.storage.Reference(reference = reference.root())
    actual val parent: Reference? get() = reference.parent()?.let { com.example.bootstrap.firebase.storage.Reference(reference = it) }
    actual fun child(path: String): Reference = com.example.bootstrap.firebase.storage.Reference(reference = reference.child(path = path))
    actual suspend fun url(): String? = reference.tryAwaitWithResult { downloadURLWithCompletion(completion = it) }?.absoluteString

    actual suspend fun list(): List<Reference> = reference.tryAwaitWithResult { listAllWithCompletion(completion = it) }?.items()?.mapNotNull {
        (it as FIRStorageReference).run { com.example.bootstrap.firebase.storage.Reference(reference = it) }
    } ?: emptyList()

    actual suspend fun delete(): Boolean =
        reference.tryAwait { deleteWithCompletion(completion = it) }

    actual suspend fun uploadBytes(bytes: ByteArray): Boolean =
        reference.tryAwait { putData(uploadData = bytes.toNSData()) }

    actual suspend fun uploadFile(path: String): Boolean =
        reference.tryAwait { putFile(fileURL = NSURL.URLWithString(URLString = path) ?: return false) }

    actual suspend fun downloadBytes(maxSize: Long): ByteArray? =
        reference.tryAwaitWithResult { dataWithMaxSize(maxSize = maxSize, completion = it) }?.toByteArray()

    actual suspend fun downloadFile(): String? = try {
        val pair = getNameAndExtensionFromUrl(url = path) ?: throw Throwable(message = "Invalid URL")
        val file = NSURL(fileURLWithPath = "${pair.first}.${pair.second}")
        reference.tryAwait { writeToFile(fileURL = file) }
        file.absoluteString
    } catch (throwable: Throwable) {
        null
    }
}
