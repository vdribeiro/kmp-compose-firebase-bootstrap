package com.example.bootstrap.http.request

import com.example.bootstrap.datetime.DateTime
import com.example.bootstrap.http.body.Body
import com.example.bootstrap.security.generateUuid

/**
 * HTTP Request with request identifier [id], timestamp [utc], [path] for the endpoint url, [headerMap] for the key-value mapping of headers,
 * [queryMap] for the key-value mapping of the query parameters, [body] for the data payload as form or multipart and an optional [timeout].
 */
internal data class Request(
    val id: String = generateUuid(),
    val utc: DateTime = DateTime(),
    val path: String,
    val headerMap: HashMap<String, String> = hashMapOf(),
    val queryMap: Map<String, String> = emptyMap(),
    val body: Body? = null,
    val timeout: Long? = null
)
