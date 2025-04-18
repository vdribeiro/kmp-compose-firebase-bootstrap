package com.example.bootstrap.file

internal interface Files {

    /**
     * Get file from [url].
     * This firstly gets the file from the cache if it exists,
     * otherwise it downloads it and then writes to the cache.
     */
    suspend fun getFile(url: String): File?
}
