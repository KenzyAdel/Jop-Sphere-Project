package com.example.android_app.presentation.applicant.savedJobs

import com.example.android_app.data.entities.SavedJobsEntity

data class savedJobsUiState(
    val isLoading: Boolean = false,
    val savedJobs: List<SavedJobsEntity> = emptyList(),
    val error: String? = null
)
