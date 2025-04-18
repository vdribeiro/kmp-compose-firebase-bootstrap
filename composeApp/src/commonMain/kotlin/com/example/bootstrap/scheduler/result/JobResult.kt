package com.example.bootstrap.scheduler.result

internal sealed class JobResult {
    data object Success: JobResult()
    data object Error: JobResult()
    data object Retry: JobResult()
    data object NoInternet: JobResult()
}
