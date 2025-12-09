package com.example.android_app.presentation.applicant.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.DAOs.applicantDao
import com.example.android_app.data.entities.applicantEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val dao: applicantDao) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.Load -> load(intent.id)
            is ProfileIntent.FullNameChange -> _state.value = _state.value.copy(fullName = intent.value)
            is ProfileIntent.EmailChange -> _state.value = _state.value.copy(email = intent.value)
            is ProfileIntent.PhoneChange -> _state.value = _state.value.copy(phone = intent.value)
            is ProfileIntent.LinkedinChange -> _state.value = _state.value.copy(linkedin = intent.value)
            is ProfileIntent.PortfolioChange -> _state.value = _state.value.copy(portfolio = intent.value)
            ProfileIntent.EnableEditing -> _state.value = _state.value.copy(isEditing = true)
            ProfileIntent.DisableEditing -> _state.value = _state.value.copy(isEditing = false)
            ProfileIntent.Save -> save()
        }
    }

    private fun load(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val user = dao.getApplicant(id)
            if (user != null) {
                _state.value = _state.value.copy(
                    id = user.id,
                    fullName = user.fullName,
                    email = user.email,
                    phone = user.phone ?: "",
                    linkedin = user.linkedin ?: "",
                    portfolio = user.portfolio ?: "",
                    isLoading = false
                )
            } else {
                _state.value = _state.value.copy(isLoading = false, error = "User not found")
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val user = applicantEntity(
                id = _state.value.id,
                fullName = _state.value.fullName,
                email = _state.value.email,
                password = "",
                phone = _state.value.phone,
                linkedin = _state.value.linkedin,
                portfolio = _state.value.portfolio
            )
            dao.updateApplicant(user)
            _state.value = _state.value.copy(isLoading = false, success = true, isEditing = false)
        }
    }
}
