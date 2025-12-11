package com.example.android_app.presentation.applicant.home

data class ApplicantHomeState(
    val isLoading: Boolean = false,
    val jobs: List<JobItem> = emptyList(), // The list displayed on screen
    val allJobsCached: List<JobItem> = emptyList(), // Cache for filtering purposes
    val errorMessage: String? = null,
    val selectedFilter: String = "All",
    val savedJobIds: Set<String> = emptySet() // Track which jobs are saved
)