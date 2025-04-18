package com.example.bootstrap.scheduler

import com.example.bootstrap.flow.Dispatcher
import com.example.bootstrap.flow.launch
import com.example.bootstrap.http.Network
import com.example.bootstrap.scheduler.domain.JobAction
import com.example.bootstrap.scheduler.domain.JobGroup
import com.example.bootstrap.scheduler.domain.JobGroupMap
import com.example.bootstrap.scheduler.domain.JobRequest
import com.example.bootstrap.scheduler.domain.JobState
import com.example.bootstrap.scheduler.domain.ScheduledJob
import com.example.bootstrap.scheduler.local.SchedulerLocal
import com.example.bootstrap.scheduler.mapper.toDomain
import com.example.bootstrap.scheduler.result.JobResult
import com.example.bootstrap.scheduler.runner.Runner
import com.example.bootstrap.toggle.FeatureToggle
import com.example.bootstrap.toggle.ToggleKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Engine responsible for Job execution.
 */
internal class JobScheduler(
    private val toggle: FeatureToggle,
    private val dispatcher: Dispatcher,
    private val schedulerDao: SchedulerLocal,
    private val jobRunner: Runner
): Scheduler {

    private val network = Network()
    private val pipelineMap = JobGroupMap()

    // V1 properties
    private val mutex = Mutex(false)
    private var newJobsSubmitted = false

    init {
        dispatcher.default.launch {
            rescheduleInterruptedJobs()
            if (toggle.getBoolean(ToggleKey.SCHEDULER) == true) {
                runPipelines()
                monitorNetwork()
            } else dispatchPendingJobs()
        }
    }

    private fun rescheduleInterruptedJobs() =
        schedulerDao.getRunningJobs().forEach {
            schedulerDao.upsertScheduledJob(scheduledJob = it.copy(state = JobState.PENDING))
        }

    private fun runPipelines() =
        JobGroup.entries.forEach { runPipeline(group = it) }

    private fun runPipeline(group: JobGroup) =
        dispatcher.io.launch {
            if (!pipelineMap.isBusy(key = group)) {
                schedulerDao.getFirstScheduledJobOfGroup(group = group)?.let {
                    dispatchJob(scheduledJob = it)
                }
            }
        }

    private suspend fun monitorNetwork() =
        network.monitor(dispatcher = dispatcher).collect { connected -> if (connected) runPipelines() }

    override suspend fun insert(jobRequest: JobRequest) {
        val scheduledJob = jobRequest.toDomain()
        schedulerDao.queueScheduledJob(scheduledJob = scheduledJob)
        if (toggle.getBoolean(ToggleKey.SCHEDULER) == true) {
            runPipeline(group = scheduledJob.type.group)
        } else dispatchPendingJobs()
    }

    private suspend fun dispatchJob(scheduledJob: ScheduledJob) {
        if (toggle.getBoolean(ToggleKey.SCHEDULER) == false) {
            val job = schedulerDao.getJob(scheduledJob.id) ?: return
            if (job.state != JobState.PENDING) return
        }

        pipelineMap.enable(key = scheduledJob.type.group)

        if (scheduledJob.state != JobState.PENDING) {
            dispatchNextJob(scheduledJob = scheduledJob)
            return
        }

        val runningJob = scheduledJob.copy(
            state = JobState.RUNNING,
            attempt = scheduledJob.attempt + 1
        )
        schedulerDao.upsertScheduledJob(scheduledJob = runningJob)

        when (jobRunner.run(scheduledJob = runningJob)) {
            JobResult.Success -> {
                val finishedJob = runningJob.copy(state = JobState.COMPLETED)
                schedulerDao.upsertScheduledJob(scheduledJob = finishedJob)
                dispatchNextJob(scheduledJob = scheduledJob)
            }

            JobResult.NoInternet -> {
                val finishedJob = runningJob.copy(state = JobState.PENDING)
                schedulerDao.upsertScheduledJob(scheduledJob = finishedJob)
                pipelineMap.disable(key = scheduledJob.type.group)
            }

            JobResult.Error -> {
                val finishedJob = runningJob.copy(state = JobState.FAILED)
                schedulerDao.upsertScheduledJob(scheduledJob = finishedJob)
                dispatchNextJob(scheduledJob = scheduledJob)
            }

            is JobResult.Retry -> when {
                isTooManyAttempts(scheduledJob = runningJob) -> {
                    val finishedJob = runningJob.copy(state = JobState.FAILED)
                    schedulerDao.upsertScheduledJob(scheduledJob = finishedJob)
                    dispatchNextJob(scheduledJob = scheduledJob)
                }

                else -> {
                    val finishedJob = runningJob.copy(state = JobState.PENDING)
                    schedulerDao.upsertScheduledJob(scheduledJob = finishedJob)
                    delay(timeMillis = 5000)
                    dispatchJob(scheduledJob = finishedJob)
                }
            }
        }
    }

    private suspend fun dispatchNextJob(scheduledJob: ScheduledJob) {
        if (toggle.getBoolean(ToggleKey.SCHEDULER) == false) return
        val nextJob = schedulerDao.getNextScheduledJob(id = scheduledJob.id)
        if (nextJob == null) pipelineMap.disable(key = scheduledJob.type.group)
        else dispatchJob(scheduledJob = nextJob)
    }

    private fun dispatchPendingJobs() = dispatcher.io.launch {
        newJobsSubmitted = true
        mutex.withLock {
            newJobsSubmitted = false
            schedulerDao.getPendingJobs().forEach { dispatchJob(it) }
        }
    }

    private fun isTooManyAttempts(scheduledJob: ScheduledJob): Boolean =
        (scheduledJob.type.action == JobAction.WRITE && scheduledJob.attempt > MAX_WRITE_RETRY_COUNT) ||
                (scheduledJob.type.action == JobAction.READ && scheduledJob.attempt > MAX_READ_RETRY_COUNT)

    companion object {
        private const val MAX_WRITE_RETRY_COUNT = 10
        private const val MAX_READ_RETRY_COUNT = 2
    }
}
