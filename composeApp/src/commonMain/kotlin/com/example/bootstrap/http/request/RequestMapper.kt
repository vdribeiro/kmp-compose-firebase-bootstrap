package com.example.bootstrap.http.request

import com.example.bootstrap.http.body.Body

internal fun RequestPart.toRequest(
    path: String,
    headerMap: HashMap<String, String> = hashMapOf(),
    queryMap: Map<String, String> = emptyMap(),
    body: Body? = null,
    timeout: Long? = null
): Request = Request(
    id = id,
    utc = utc,
    path = path,
    headerMap = headerMap,
    queryMap = queryMap,
    body = body,
    timeout = timeout
)
