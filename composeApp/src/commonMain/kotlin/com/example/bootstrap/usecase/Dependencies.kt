package com.example.bootstrap.usecase

import com.example.bootstrap.file.Files
import com.example.bootstrap.flow.Dispatcher
import com.example.bootstrap.http.client.HttpClient
import com.example.bootstrap.scheduler.JobScheduler
import com.example.bootstrap.scheduler.local.SchedulerDao
import com.example.bootstrap.scheduler.local.SchedulerLocal
import com.example.bootstrap.scheduler.runner.JobRunner
import com.example.bootstrap.scheduler.runner.Runner
import com.example.bootstrap.toggle.FeatureToggle
import com.example.bootstrap.usecase.session.SessionGateway
import com.example.bootstrap.usecase.session.SessionUseCases
import com.example.bootstrap.usecase.session.local.SessionDao
import com.example.bootstrap.usecase.session.local.SessionLocal
import com.example.bootstrap.usecase.session.remote.SessionApi
import com.example.bootstrap.usecase.session.remote.SessionRemote
import database.AppDatabase
import kotlinx.coroutines.flow.Flow

internal class Dependencies(
    private val toggle: FeatureToggle,
    private val dispatcher: Dispatcher,
    private val storage: Files,
    private val database: AppDatabase,
    private val httpClient: HttpClient,
    private val userId: Flow<String?>
): UseCases {

    //region database

    private val schedulerDao: SchedulerLocal by lazy { SchedulerDao(database = database) }

    private val sessionDao: SessionLocal by lazy { SessionDao(database = database) }

    //endregion

    //region api

    private val sessionApi: SessionRemote by lazy { SessionApi(httpClient = httpClient) }

    //endregion

    //region scheduler

    private val jobRunner: Runner by lazy { JobRunner(useCases = this) }

    private val scheduler: JobScheduler by lazy {
        JobScheduler(
            toggle = toggle,
            dispatcher = dispatcher,
            schedulerDao = schedulerDao,
            jobRunner = jobRunner
        )
    }

    //endregion

    //region use cases

    override val session: SessionUseCases by lazy {
        SessionGateway(
            userId = userId,
            storage = storage,
            scheduler = scheduler,
            sessionApi = sessionApi,
            sessionDao = sessionDao
        )
    }

    //endregion
}
