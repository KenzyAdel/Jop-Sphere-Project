package com.example.android_app.presentation.company.applicationManagement

data class AppManagementUiState(
    val applicants: List<TempApplicantUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)