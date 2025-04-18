package com.example.bootstrap.scheduler.local

import com.example.bootstrap.scheduler.domain.JobGroup
import com.example.bootstrap.scheduler.domain.ScheduledJob

internal interface SchedulerLocal {

    /**
     * Get the running [ScheduledJob] list from the database.
     */
    fun getRunningJobs(): List<ScheduledJob>

    /**
     * Get the pending [ScheduledJob] list from the database.
     */
    fun getPendingJobs(): List<ScheduledJob>

    /**
     * Get the first [ScheduledJob] of a given [group] from the database.
     */
    fun getFirstScheduledJobOfGroup(group: JobGroup): ScheduledJob?

    /**
     * Get the next [ScheduledJob] in the chain from the database given its [id].
     */
    fun getNextScheduledJob(id: String): ScheduledJob?

    /**
     * Get job by [id].
     */
    fun getJob(id: String): ScheduledJob?

    /**
     * Queue in the database the given [scheduledJob].
     */
    fun queueScheduledJob(scheduledJob: ScheduledJob)

    /**
     * Upsert in the database the given [scheduledJob].
     */
    fun upsertScheduledJob(scheduledJob: ScheduledJob)
}
