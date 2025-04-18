package com.example.bootstrap.firebase

import cocoapods.FirebaseCore.FIRApp
import com.example.bootstrap.firebase.Options

internal actual val Firebase.app: App get() = com.example.bootstrap.firebase.App(app = FIRApp.defaultApp()!!)

internal actual class App internal constructor(private val app: FIRApp) {
    actual val name: String get() = app.name

    actual val options: Options
        get() = with(app.options) {
            Options(
                apiKey = APIKey.orEmpty(),
                applicationId = bundleID,
                databaseUrl = databaseURL.orEmpty(),
                gaTrackingId = trackingID,
                gcmSenderId = GCMSenderID,
                projectId = projectID,
                storageBucket = storageBucket,
            )
        }

    actual fun delete() = app.deleteApp {}
}
