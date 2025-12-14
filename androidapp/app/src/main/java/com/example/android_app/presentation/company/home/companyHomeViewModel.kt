package com.example.android_app.presentation.company.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.services.JobService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompanyHomeViewModel : ViewModel() {
    private val jobService = JobService()
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(CompanyHomeUiState())
    val uiState: StateFlow<CompanyHomeUiState> = _uiState.asStateFlow()

    init {
        loadJobs()
    }

    fun loadJobs() {
        val companyId = auth.currentUser?.uid
        if (companyId == null) {
            _uiState.update { it.copy(errorMessage = "User not logged in") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = jobService.getAllJobsForCompany(companyId)
            
            if (result.isSuccess) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        jobs = result.getOrElse { emptyList() }
                    ) 
                }
            } else {
                 _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to load jobs"
                    ) 
                }
            }
        }
    }

    fun deleteJob(jobId: String) {
        val companyId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            // Optimistic update or just loading? Let's show loading
             _uiState.update { it.copy(isLoading = true) }
             
             val result = jobService.deleteJob(companyId, jobId)
             
             if (result.isSuccess) {
                 // Refresh list
                 loadJobs()
             } else {
                 _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to delete job"
                    ) 
                }
             }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
