package com.example.bootstrap.scheduler.local

import com.example.bootstrap.scheduler.domain.JobAction
import com.example.bootstrap.scheduler.domain.JobGroup
import com.example.bootstrap.scheduler.domain.ScheduledJob
import com.example.bootstrap.scheduler.mapper.toDomain
import com.example.bootstrap.scheduler.mapper.toSchema
import database.AppDatabase

internal class SchedulerDao(
    database: AppDatabase
): SchedulerLocal {

    private val scheduledJobDao = database.scheduledJobQueries

    override fun getRunningJobs(): List<ScheduledJob> =
        scheduledJobDao.getRunningJobs().executeAsList().mapNotNull { it.toDomain() }

    override fun getPendingJobs(): List<ScheduledJob> =
        scheduledJobDao.getPendingJobs().executeAsList().mapNotNull { it.toDomain() }

    override fun getFirstScheduledJobOfGroup(group: JobGroup): ScheduledJob? =
        scheduledJobDao.getFirstScheduledJobOfGroup(group.name).executeAsOneOrNull()?.toDomain()

    override fun getNextScheduledJob(id: String): ScheduledJob? =
        scheduledJobDao.getNextScheduledJob(id = id).executeAsOneOrNull()?.toDomain()

    override fun getJob(id: String): ScheduledJob? =
        scheduledJobDao.getJob(id = id).executeAsOneOrNull()?.toDomain()

    override fun queueScheduledJob(scheduledJob: ScheduledJob) =
        scheduledJobDao.transaction {
            val jobs = scheduledJobDao.getActiveJobsOfGroup(group = scheduledJob.type.group.name).executeAsList()

            when (scheduledJob.type.action) {
                JobAction.WRITE -> {
                    val previousJob = jobs.lastOrNull { JobAction.entries[it.action] == JobAction.WRITE }
                    val nextJob = jobs.firstOrNull { JobAction.entries[it.action] == JobAction.READ }
                    val job = scheduledJob.toSchema()

                    previousJob?.let { scheduledJobDao.upsertScheduledJob(ScheduledJob = it.copy(nextId = scheduledJob.id)) }
                    scheduledJobDao.upsertScheduledJob(ScheduledJob = job.copy(nextId = nextJob?.id))
                }

                JobAction.READ -> {
                    val previousJob = jobs.lastOrNull()
                    val job = scheduledJob.toSchema()

                    previousJob?.let { scheduledJobDao.upsertScheduledJob(ScheduledJob = it.copy(nextId = scheduledJob.id)) }
                    scheduledJobDao.upsertScheduledJob(ScheduledJob = job)

                    // TODO - Cancel repeated jobs
                    //var repeatedJob: ScheduledJobSchema? = null
                    //var previousRepeatedJob: ScheduledJobSchema? = null
                    //var nextRepeatedJob: ScheduledJobSchema? = null
                    //val iterator = jobs.listIterator()
                    //while (iterator.hasNext() && repeatedJob == null) {
                    //    val iteratedJob = iterator.next()
                    //    if (iteratedJob.state == JobState.PENDING && JobType.fromString(iteratedJob.type) == scheduledJob.type) {
                    //        repeatedJob = job
                    //        nextRepeatedJob = if (iterator.hasNext()) iterator.next() else null
                    //    } else {
                    //        previousRepeatedJob = iteratedJob
                    //    }
                    //}
                    //previousRepeatedJob?.let { scheduledJobDao.upsertScheduledJob(ScheduledJob = it.copy(nextId = nextRepeatedJob?.id)) }
                    //repeatedJob?.let { scheduledJobDao.upsertScheduledJob(ScheduledJob = it.copy(nextId = null, state = JobState.CANCELED)) }
                }
            }
        }

    override fun upsertScheduledJob(scheduledJob: ScheduledJob) =
        scheduledJobDao.upsertScheduledJob(ScheduledJob = scheduledJob.toSchema())
}
