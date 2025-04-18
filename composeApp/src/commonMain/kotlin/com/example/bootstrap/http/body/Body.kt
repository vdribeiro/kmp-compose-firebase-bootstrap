package com.example.bootstrap.http.body

internal sealed class Body {

    internal data class Raw(val raw: String): Body()

    internal data class Form(val map: Map<String, Any?>): Body()

    internal data class Multipart(val map: Map<String, Any?>): Body()
}
