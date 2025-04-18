package com.example.bootstrap.scheduler.runner

import com.example.bootstrap.scheduler.domain.ScheduledJob
import com.example.bootstrap.scheduler.mapper.toJobRequest
import com.example.bootstrap.scheduler.result.JobResult
import com.example.bootstrap.usecase.UseCases

internal class JobRunner(
    private val useCases: UseCases
): Runner {

    override suspend fun run(scheduledJob: ScheduledJob): JobResult = try {
        with(useCases) {
            val jobRequest = scheduledJob.toJobRequest()
            when (scheduledJob.type) {
                //region user
                com.example.bootstrap.scheduler.domain.JobType.SYNC_USERS -> session.syncUsers(jobRequest = jobRequest)
                com.example.bootstrap.scheduler.domain.JobType.UPSERT_USER -> session.upsertUser(jobRequest = jobRequest)
                //endregion
            }
        }
    } catch (throwable: Throwable) {
        JobResult.Error
    }
}
