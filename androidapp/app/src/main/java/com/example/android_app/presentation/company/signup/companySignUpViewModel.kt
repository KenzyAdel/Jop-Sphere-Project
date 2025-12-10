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
        // Inside companySignUpViewModel.kt

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, signUpError = null) }

            try {
                // Step 1: Create the user account
                val user = auth.createUserWithEmailAndPassword(state.email, state.password).await()
                val userId = user.user?.uid

                if (userId == null) {
                    throw IllegalStateException("Firebase user creation failed: UID not found.")
                }

                // Step 3: Create the Company object
                val company = Company(
                    name = state.companyName,
                    email = state.email,
                    website = state.website,
                    phone = state.phone,
                    industry = state.industry,
                    linkedin = state.linkedIn,
                    id = userId
                )

                // Step 4: Save to Firestore AND CHECK THE RESULT
                val result = companyService.createCompany(company)

                result.onSuccess {
                    // Step 5: Update UI only on success
                    _uiState.update { it.copy(isLoading = false, isSignUpSuccess = true) }
                }.onFailure { exception ->
                    // If Firestore fails, throw the exception so the catch block below handles it
                    throw exception
                }

            } catch (e: Exception) {
                // Now this will actually catch Firestore errors
                val errorMsg = when (e) {
                    is FirebaseAuthUserCollisionException -> "An account with this email already exists."
                    else -> e.message ?: "Sign up failed. Please try again."
                }
                _uiState.update { it.copy(isLoading = false, signUpError = errorMsg) }
            }
        }
        }
}
