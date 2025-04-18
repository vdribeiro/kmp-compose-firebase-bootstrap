package com.example.bootstrap.file

internal fun getNameAndExtensionFromUrl(url: String): Pair<String, String>? {
    val lastIndex = url.lastIndexOf(char = '/') + 1
    val last = if (lastIndex in url.indices) url.substring(startIndex = lastIndex) else url
    val split = last.split(".")
    return Pair(
        first = split.getOrNull(index = 0) ?: return null,
        second = split.getOrNull(index = 1) ?: return null
    )
}
