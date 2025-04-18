package com.example.bootstrap.firebase

/**
 * Entry point of Firebase.
 */
internal object Firebase

/**
 * Entry point of Firebase App.
 */
internal expect val Firebase.app: App

internal expect class App {
    val name: String
    val options: Options
    fun delete()
}

internal data class Options(
    /**
     * API key used for authenticating requests.
     */
    val apiKey: String,
    /**
     * Google App ID that is used to uniquely identify an instance of an app.
     */
    val applicationId: String,
    /**
     * The database root URL.
     */
    val databaseUrl: String? = null,
    /**
     * The tracking ID used to configure Google Analytics.
     */
    val gaTrackingId: String? = null,
    /**
     * The Project Number from the Google Developer's console used to configure Google Cloud Messaging.
     */
    val gcmSenderId: String? = null,
    /**
     * The Google Cloud project ID.
     */
    val projectId: String? = null,
    /**
     * The Google Cloud Storage bucket name.
     */
    val storageBucket: String? = null,
)
