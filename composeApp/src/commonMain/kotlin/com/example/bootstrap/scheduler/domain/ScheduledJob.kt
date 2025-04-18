package com.example.bootstrap.scheduler.domain

import com.example.bootstrap.datetime.DateTime

internal data class ScheduledJob(
    val id: String,
    val utc: DateTime,
    val userId: String?,
    val type: JobType,
    val params: Map<String, String>,
    val state: JobState,
    val attempt: Int,
    val nextId: String?
)
