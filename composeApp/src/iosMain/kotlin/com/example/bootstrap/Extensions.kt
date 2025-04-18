package com.example.bootstrap

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).apply {
    usePinned { memcpy(__dst = it.addressOf(index = 0), __src = bytes, __n = length) }
}

@OptIn(ExperimentalForeignApi::class)
internal fun ByteArray.toNSData(): NSData = memScoped {
    NSData.create(bytes = allocArrayOf(elements = this@toNSData), length = size.toULong())
}

/**
 * Awaits the completion of the task without blocking a thread.
 * If the Job of the current coroutine is cancelled it returns false.
 */
internal suspend inline fun <T> T.tryAwait(
    function: T.(callback: (NSError?) -> Unit) -> Unit
): Boolean = try {
    with(receiver = CompletableDeferred<Unit>()) {
        function { error ->
            error?.let {
                completeExceptionally(exception = Throwable(message = error.localizedDescription))
            } ?: complete(value = Unit)
        }
        await()
    }
    true
} catch (_: Throwable) {
    false
}

/**
 * Awaits the completion of the task without blocking a thread.
 * If the Job of the current coroutine is cancelled it returns null.
 */
internal suspend inline fun <T, reified R> T.tryAwaitWithResult(
    function: T.(callback: (R?, NSError?) -> Unit) -> Unit
): R? = try {
    with(receiver = CompletableDeferred<R?>()) {
        function { result, error ->
            error?.let {
                completeExceptionally(exception = Throwable(message = error.localizedDescription))
            } ?: complete(value = result)
        }
        await()
    }
} catch (_: Throwable) {
    null
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal fun <T, R> T.throwError(
    block: T.(errorPointer: CPointer<ObjCObjectVar<NSError?>>) -> R?
): R? = memScoped {
    val errorPointer: CPointer<ObjCObjectVar<NSError?>> = alloc<ObjCObjectVar<NSError?>>().ptr
    val result = block(errorPointer)
    errorPointer.pointed.value?.let { throw Throwable(message = it.localizedDescription) }
    return result
}
