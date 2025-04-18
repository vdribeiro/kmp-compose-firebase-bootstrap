package com.example.bootstrap.http.client

import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.firestore.firestore
import com.example.bootstrap.http.body.Body
import com.example.bootstrap.http.request.Request
import com.example.bootstrap.http.response.Response

@Suppress("UNCHECKED_CAST")
internal class FirebaseClient: HttpClient {
    override suspend fun <T> get(request: Request): Response<T> {
        val path = request.path.split("/")
        val collection = path[0]
        return when (val documentId = path.getOrNull(1)) {
            null -> Response(headers = emptyMap(), body = Firebase.firestore.getCollection(collection) as T)
            else -> Response(headers = emptyMap(), body = Firebase.firestore.getDocument(collection, documentId) as T)
        }
    }

    override suspend fun <T> post(request: Request): Response<T> {
        val path = request.path.split("/")
        val collection = path[0]
        val documentId = path[1]
        val documentData = request.body as Body.Form
        Firebase.firestore.setDocument(collection, documentId, documentData.map)
        return Response(headers = emptyMap(), body = documentData.map as T)
    }

    override suspend fun <T> patch(request: Request): Response<T> = post(request)

    override suspend fun <T> delete(request: Request): Response<T> {
        val path = request.path.split("/")
        val collection = path[0]
        val documentId = path[1]
        Firebase.firestore.removeDocument(collection, documentId)
        return Response(headers = emptyMap(), body = Unit as T)
    }
}
