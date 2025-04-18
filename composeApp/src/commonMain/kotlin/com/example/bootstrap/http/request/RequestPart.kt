package com.example.bootstrap.http.request

import com.example.bootstrap.datetime.DateTime
import com.example.bootstrap.security.generateUuid

internal data class RequestPart(
    val id: String = generateUuid(),
    val utc: DateTime = DateTime()
)
