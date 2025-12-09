package com.example.android_app.presentation.company.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.models.Company
import com.example.android_app.data.services.CompanyService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class companySignUpViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val companyService = CompanyService()
    private val _uiState = MutableStateFlow(companySignUpUiState())
    val uiState: StateFlow<companySignUpUiState> = _uiState.asStateFlow()

    // --- State Update Functions ---
    fun onCompanyNameChange(newValue: String) {
        _uiState.update { it.copy(companyName = newValue, signUpError = null) }
    }

    fun onEmailChange(newValue: String) {
        _uiState.update { it.copy(email = newValue, signUpError = null) }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update { it.copy(password = newValue, signUpError = null) }
    }

    fun onConfirmPasswordChange(newValue: String) {
        _uiState.update { it.copy(confirmPassword = newValue, signUpError = null) }
    }

    fun onWebsiteChange(newValue: String) {
        _uiState.update { it.copy(website = newValue) }
    }

    fun onPhoneChange(newValue: String) {
        _uiState.update { it.copy(phone = newValue) }
    }

    fun onIndustryChange(newValue: String) {
        _uiState.update { it.copy(industry = newValue) }
    }

    fun onLinkedInChange(newValue: String) {
        _uiState.update { it.copy(linkedIn = newValue) }
    }

    // --- Sign Up Logic ---
    fun onSignUpClick() {
        val state = _uiState.value

        // 1. Basic Validation
        if (state.companyName.isBlank() || state.email.isBlank() || state.password.isBlank() ||state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(signUpError = "Please fill in all required fields.") }
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(signUpError = "Passwords do not match.") }
            return
        }

        if (state.password.length < 6) {
            _uiState.update { it.copy(signUpError = "Password must be at least 6 characters.") }
            return
        }

        // 2. Perform Firebase Registration
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, signUpError = null) }

            try {
                // Step 1: Create the user account using the service
                val user = auth.createUserWithEmailAndPassword(state.email, state.password).await()
                // Step 3: Create the Company object with the new user's UID
                val company = Company(
                    name = state.companyName,
                    email = state.email,
                    website = state.website,
                    phone = state.phone,
                    industry = state.industry,
                    linkedin = state.linkedIn
                )

                // Step 4: Save the company data to Firestore using the service
                companyService.createCompany(company)

                // Step 5: Update UI to reflect success
                _uiState.update { it.copy(isLoading = false, isSignUpSuccess = true) }

            } catch (e: Exception) {
                // Handle any errors from the service calls
                val errorMsg = when (e) {
                    is FirebaseAuthUserCollisionException -> "An account with this email already exists."
                    else -> e.message ?: "Sign up failed. Please try again."
                }
                _uiState.update { it.copy(isLoading = false, signUpError = errorMsg) }
            }
                }
        }
}
