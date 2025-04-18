package com.example.bootstrap.core

import com.example.bootstrap.database.DatabaseFactory
import com.example.bootstrap.file.Files
import com.example.bootstrap.file.Storage
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.config.remoteConfig
import com.example.bootstrap.flow.CoroutineDispatcher
import com.example.bootstrap.flow.Dispatcher
import com.example.bootstrap.flow.launch
import com.example.bootstrap.http.client.HttpClient
import com.example.bootstrap.http.client.HttpClientFactory
import com.example.bootstrap.toggle.FeatureFlag
import com.example.bootstrap.toggle.FeatureToggle
import com.example.bootstrap.toggle.ToggleKey
import com.example.bootstrap.usecase.Dependencies
import com.example.bootstrap.usecase.UseCases
import com.example.bootstrap.usecase.session.local.UserDao
import com.example.bootstrap.usecase.session.local.UserLocal
import database.AppDatabase
import kotlinx.coroutines.flow.Flow

internal class Core private constructor() {

    companion object {
        val INSTANCE: Core by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { Core() }
    }

    private val storage: Files = Storage()

    private val database: AppDatabase = DatabaseFactory.getDatabase()

    private val userDao: UserLocal = UserDao(database = database)

    private val httpClient: HttpClient = HttpClientFactory.getHttpClient { userDao.getUserToken(it) }

    val userId: Flow<String?> = userDao.observeLoggedUserId()

    val dispatcher: Dispatcher = CoroutineDispatcher()

    val toggle: FeatureToggle = FeatureFlag()

    val useCases: UseCases = Dependencies(
        toggle = toggle,
        dispatcher = dispatcher,
        storage = storage,
        database = database,
        httpClient = httpClient,
        userId = userId
    )

    init {
        // TODO - init remote config here? on session login? or not at all?
        dispatcher.io.launch {
            Firebase.remoteConfig.setDefaults(
                mapOf(
                    ToggleKey.SCHEDULER.key to false,
                )
            )
        }
    }
}
