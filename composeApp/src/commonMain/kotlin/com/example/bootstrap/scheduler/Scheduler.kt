package com.example.bootstrap.scheduler

import com.example.bootstrap.scheduler.domain.JobRequest

internal interface Scheduler {

    /**
     * Insert a [jobRequest].
     */
    suspend fun insert(jobRequest: JobRequest)
}
