package com.example.bootstrap.scheduler.runner

import com.example.bootstrap.scheduler.domain.ScheduledJob
import com.example.bootstrap.scheduler.result.JobResult

internal interface Runner {

    suspend fun run(scheduledJob: ScheduledJob): JobResult
}
