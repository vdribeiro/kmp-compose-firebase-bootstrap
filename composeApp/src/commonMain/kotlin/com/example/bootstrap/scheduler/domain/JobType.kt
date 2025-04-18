package com.example.bootstrap.scheduler.domain

/**
 * Indicates what kind of job the Scheduler will execute.
 */
enum class JobType {
    SYNC_USERS;

    companion object {
        private val map = JobType.entries.associateBy(JobType::name)
        fun fromString(name: String): JobType? = map[name.uppercase()]
    }
}
