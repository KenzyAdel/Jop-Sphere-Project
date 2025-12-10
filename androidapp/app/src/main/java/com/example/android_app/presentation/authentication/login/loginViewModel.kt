package com.example.android_app.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class loginViewModel : ViewModel() {

    // Get Firebase Auth Instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(loginUiState())
    val uiState: StateFlow<loginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, loginError = null) } // Clear error on edit
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, loginError = null) } // Clear error on edit
    }

    fun onLoginClick() {
        val currentEmail = _uiState.value.email.trim()
        val currentPassword = _uiState.value.password

        // Basic Validation
        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _uiState.update { it.copy(loginError = "Email and Password cannot be empty") }
            return
        }

        viewModelScope.launch {
            // 1. Set loading state
            _uiState.update { it.copy(isLoading = true, loginError = null) }

            try {
                // 2. Firebase Authentication Call (using await() for cleaner sequential logic)
                val authResult = auth.signInWithEmailAndPassword(currentEmail, currentPassword).await()
                val userId = authResult.user?.uid

                if (userId != null) {
                    val db = FirebaseFirestore.getInstance()

                    // 3. Check if the user exists in the "company" collection
                    println("===========================1===============================")
                    val companyDoc = db.collection("Company").document(userId).get().await()
                    println("Company Document: $companyDoc")

                    if (companyDoc.exists()) {
                        // ---> User is a Company
                        println("===========================2===============================")

                        _uiState.update {
                            it.copy(isLoading = false, isLoginSuccess = true)
                        }
                    } else {
                        // 4. If not a company, check the "applicants" collection
                        println("===========================3===============================")
                        val applicantDoc = db.collection("Applicant").document(userId).get().await()

                        if (applicantDoc.exists()) {
                            println("===========================4===============================")

                            // ---> User is an Applicant
                            _uiState.update {
                                it.copy(isLoading = false, isLoginSuccess = true)
                            }
                        } else {
                            // User is authenticated but has no profile document
                            _uiState.update {
                                it.copy(isLoading = false, loginError = "User profile not found.")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // 5. Handle Login Failed
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidUserException -> "No account found with this email."
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect password or malformed email."
                    else -> e.message ?: "Authentication failed."
                }

                _uiState.update {
                    it.copy(isLoading = false, loginError = errorMessage)
                }
            }
        }
    }
}