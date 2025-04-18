package com.example.bootstrap.file

import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.storage.storage
import com.example.bootstrap.logger.Logger

internal class Storage: Files {

    companion object {
        private const val TAG = "Storage"
        /**
         * Remote to local file path mapping.
         */
        private val map = ConcurrentMap<String, String>()
    }

    /**
     * Get file from [url]. Fetch from remote if it does not exist locally.
     */
    override suspend fun getFile(url: String): File? {
        try {
            val cachedPath = map.synchronizedGet(key = url)
            val cachedFile = cachedPath?.let { File(path = it) }
            if (cachedFile?.exists() == true) {
                return cachedFile
            }

            val downloadedPath = Firebase.storage.url(url).downloadFile()
            val downloadedFile = downloadedPath?.let { File(path = it) }
            if (downloadedFile?.exists() == true) {
                map.synchronizedPut(key = url, value = downloadedPath)
                return downloadedFile
            }
        } catch (throwable: Throwable) {
            Logger.error(tag = TAG, message = "Invalid url: $url")
        }
        return null
    }
}
