package com.example.bootstrap.scheduler.domain

import com.example.bootstrap.datetime.DateTime
import com.example.bootstrap.security.generateUuid

/**
 * Request to the Scheduler to execute a Job.
 */
internal data class JobRequest(
    val id: String = generateUuid(),
    val utc: DateTime = DateTime(),
    val userId: String? = null,
    val type: JobType,
    val params: Map<String, String> = emptyMap()
)
