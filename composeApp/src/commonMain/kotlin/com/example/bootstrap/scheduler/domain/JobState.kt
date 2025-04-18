package com.example.bootstrap.scheduler.domain

enum class JobState {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELED;
}
