package com.example.android_app.presentation.company.home

import com.example.android_app.data.models.Job

data class CompanyHomeUiState(
    val jobs: List<Job> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
