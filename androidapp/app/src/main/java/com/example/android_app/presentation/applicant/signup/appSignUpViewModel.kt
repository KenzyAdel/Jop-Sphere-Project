package com.example.android_app.presentation.applicant.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.models.Applicant
import com.example.android_app.data.services.ApplicantService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AppSignUpViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val applicantService = ApplicantService()
    private val _uiState = MutableStateFlow(AppSignUpUiState())
    val uiState: StateFlow<AppSignUpUiState> = _uiState.asStateFlow()

    // --- State Update Functions ---
    fun onFullNameChange(newValue: String) {
        _uiState.update { it.copy(fullName = newValue, signUpError = null) }
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

    fun onPhoneChange(newValue: String) {
        _uiState.update { it.copy(phone = newValue) }
    }

    fun onLinkedInChange(newValue: String) {
        _uiState.update { it.copy(linkedIn = newValue) }
    }

    fun onCVLinkChange(newValue: String) {
        _uiState.update { it.copy(CVLink = newValue) }
    }

    // --- Sign Up Logic ---
    fun onSignUpClick() {
        val state = _uiState.value

        // 1. Basic Validation
        if (state.fullName.isBlank() || state.email.isBlank() || state.password.isBlank()) {
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
                val applicant = Applicant(
                    name = state.fullName,
                    email = state.email,
                    phone = state.phone,
                    linkedin = state.linkedIn,
                    cvLink = state.CVLink,
                    id = userId
                )

                // Step 4: Save to Firestore AND CHECK THE RESULT
                val result = applicantService.createApplicant(applicant)

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