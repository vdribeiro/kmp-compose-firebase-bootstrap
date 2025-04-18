package com.example.bootstrap.database

/**
 * Helper class to map relationships.
 */
internal class MapperMap: MutableMap<String, MutableSet<String>> by HashMap() {

    fun addRelationship(key: String, value: String?) = value?.let {
        put(key, (get(key) ?: mutableSetOf()).apply { add(it) })
    }

    fun getRelationship(key: String) = get(key)?.firstOrNull()

    fun getRelationshipList(key: String) = get(key) ?: emptyList()
}
