package com.example.bootstrap.scheduler.mapper

import com.example.bootstrap.database.ScheduledJobSchema
import com.example.bootstrap.http.request.RequestPart
import com.example.bootstrap.scheduler.domain.JobRequest
import com.example.bootstrap.scheduler.domain.JobState
import com.example.bootstrap.scheduler.domain.JobType
import com.example.bootstrap.scheduler.domain.ScheduledJob
import kotlinx.serialization.json.Json

//region prototype

internal fun JobRequest.toDomain(): ScheduledJob =
    ScheduledJob(
        id = id,
        utc = utc,
        userId = userId,
        type = type,
        params = params,
        state = JobState.PENDING,
        attempt = 0,
        nextId = null
    )

internal fun ScheduledJob.toJobRequest(): JobRequest =
    JobRequest(
        id = id,
        utc = utc,
        type = type,
        params = params
    )

internal fun JobRequest.toRequestPart(): RequestPart =
    RequestPart(
        id = id,
        utc = utc
    )

//endregion

//region database

internal fun ScheduledJobSchema.toDomain(): ScheduledJob? {
    return ScheduledJob(
        id = id,
        utc = utc,
        type = JobType.fromString(type) ?: return null,
        userId = userId,
        params = Json.decodeFromString(params),
        state = state,
        attempt = attempt,
        nextId = nextId
    )
}

internal fun ScheduledJob.toSchema(): ScheduledJobSchema =
    ScheduledJobSchema(
        id = id,
        utc = utc,
        type = type.name,
        action = type.action.ordinal,
        tag = type.group.name,
        userId = userId,
        params = Json.encodeToString(params),
        state = state,
        attempt = attempt,
        nextId = nextId
    )

//endregion
