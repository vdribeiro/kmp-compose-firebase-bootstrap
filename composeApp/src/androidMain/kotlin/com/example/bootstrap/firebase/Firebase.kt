package com.example.bootstrap.firebase

import com.google.firebase.FirebaseApp

internal actual val Firebase.app: App get() = App(app = FirebaseApp.getInstance())

internal actual class App(private val app: FirebaseApp) {
    actual val name: String get() = app.name

    actual val options: Options
        get() = with(app.options) {
            Options(
                apiKey = apiKey,
                applicationId = applicationId,
                databaseUrl = databaseUrl,
                gaTrackingId = gaTrackingId,
                gcmSenderId = gcmSenderId,
                projectId = projectId,
                storageBucket = storageBucket,
            )
        }

    actual fun delete() = app.delete()
}
