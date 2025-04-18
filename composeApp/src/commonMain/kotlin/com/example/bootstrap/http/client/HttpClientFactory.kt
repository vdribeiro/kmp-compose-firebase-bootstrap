package com.example.bootstrap.http.client

internal object HttpClientFactory {

    fun getHttpClient(token: (String) -> String?): HttpClient = FirebaseClient()
}

internal const val DEFAULT_TIMEOUT = 60_000L
