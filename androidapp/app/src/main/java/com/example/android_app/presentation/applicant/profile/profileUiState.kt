package com.example.android_app.presentation.applicant.profile

data class ProfileUiState(
    val id: Int = 0,
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val linkedin: String = "",
    val portfolio: String = "",
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

sealed interface ProfileIntent {
    data class Load(val id: Int) : ProfileIntent
    data class FullNameChange(val value: String) : ProfileIntent
    data class EmailChange(val value: String) : ProfileIntent
    data class PhoneChange(val value: String) : ProfileIntent
    data class LinkedinChange(val value: String) : ProfileIntent
    data class PortfolioChange(val value: String) : ProfileIntent
    object EnableEditing : ProfileIntent
    object DisableEditing : ProfileIntent
    object Save : ProfileIntent
}
