package com.example.android_app.presentation.company.applicationManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.models.Application
import com.example.android_app.data.services.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * State for managing application data
 */
sealed class ApplicationsState {
    object Idle : ApplicationsState()
    object Loading : ApplicationsState()
    data class Success(val applications: List<Application>) : ApplicationsState()
    data class Error(val message: String) : ApplicationsState()
}

/**
 * ViewModel for managing job applications
 * Demonstrates how to use Firebase services in a real application
 */
class ApplicationManagementViewModel : ViewModel() {
    
    private val firebaseManager = FirebaseManager.getInstance()
    
    // State for all applications
    private val _applicationsState = MutableStateFlow<ApplicationsState>(ApplicationsState.Idle)
    val applicationsState: StateFlow<ApplicationsState> = _applicationsState.asStateFlow()
    
    // State for operation results
    private val _operationResult = MutableStateFlow<String?>(null)
    val operationResult: StateFlow<String?> = _operationResult.asStateFlow()
    
    /**
     * Load all applications for a specific job
     */
    fun loadApplicationsForJob(companyId: String, jobId: String) {
        viewModelScope.launch {
            _applicationsState.value = ApplicationsState.Loading
            
            firebaseManager.applications.getAllApplicationsForJob(companyId, jobId)
                .onSuccess { applications ->
                    _applicationsState.value = ApplicationsState.Success(applications)
                }
                .onFailure { error ->
                    _applicationsState.value = ApplicationsState.Error(
                        error.message ?: "Failed to load applications"
                    )
                }
        }
    }
    
    /**
     * Load all applications for a company
     */
    fun loadApplicationsForCompany(companyId: String) {
        viewModelScope.launch {
            _applicationsState.value = ApplicationsState.Loading
            
            firebaseManager.applications.getAllApplicationsForCompany(companyId)
                .onSuccess { applications ->
                    _applicationsState.value = ApplicationsState.Success(applications)
                }
                .onFailure { error ->
                    _applicationsState.value = ApplicationsState.Error(
                        error.message ?: "Failed to load applications"
                    )
                }
        }
    }
    
    /**
     * Filter applications by status
     */
    fun loadApplicationsByStatus(companyId: String, jobId: String, status: String) {
        viewModelScope.launch {
            _applicationsState.value = ApplicationsState.Loading
            
            firebaseManager.applications.getApplicationsByStatus(companyId, jobId, status)
                .onSuccess { applications ->
                    _applicationsState.value = ApplicationsState.Success(applications)
                }
                .onFailure { error ->
                    _applicationsState.value = ApplicationsState.Error(
                        error.message ?: "Failed to filter applications"
                    )
                }
        }
    }
    
    /**
     * Update application status
     */
    fun updateApplicationStatus(
        companyId: String,
        jobId: String,
        applicationId: String,
        newStatus: String
    ) {
        viewModelScope.launch {
            firebaseManager.applications.updateApplicationStatus(
                companyId,
                jobId,
                applicationId,
                newStatus
            ).onSuccess {
                _operationResult.value = "Application status updated to $newStatus"
                // Reload applications to reflect the change
                loadApplicationsForJob(companyId, jobId)
            }.onFailure { error ->
                _operationResult.value = "Error: ${error.message}"
            }
        }
    }
    
    /**
     * Delete an application
     */
    fun deleteApplication(companyId: String, jobId: String, applicationId: String) {
        viewModelScope.launch {
            firebaseManager.applications.deleteApplication(companyId, jobId, applicationId)
                .onSuccess {
                    _operationResult.value = "Application deleted successfully"
                    // Reload applications
                    loadApplicationsForJob(companyId, jobId)
                }
                .onFailure { error ->
                    _operationResult.value = "Error: ${error.message}"
                }
        }
    }
    
    /**
     * Accept an application
     */
    fun acceptApplication(companyId: String, jobId: String, applicationId: String) {
        updateApplicationStatus(companyId, jobId, applicationId, "Accepted")
    }
    
    /**
     * Reject an application
     */
    fun rejectApplication(companyId: String, jobId: String, applicationId: String) {
        updateApplicationStatus(companyId, jobId, applicationId, "Rejected")
    }
    
    /**
     * Schedule interview for an application
     */
    fun scheduleInterview(companyId: String, jobId: String, applicationId: String) {
        updateApplicationStatus(companyId, jobId, applicationId, "Interview Scheduled")
    }
    
    /**
     * Clear operation result
     */
    fun clearOperationResult() {
        _operationResult.value = null
    }
    
    /**
     * Get application count by status
     */
    fun getApplicationCountByStatus(applications: List<Application>, status: String): Int {
        return applications.count { it.status == status }
    }
    
    /**
     * Get statistics for applications
     */
    fun getApplicationStatistics(applications: List<Application>): Map<String, Int> {
        return mapOf(
            "Total" to applications.size,
            "Pending" to getApplicationCountByStatus(applications, "Pending"),
            "Accepted" to getApplicationCountByStatus(applications, "Accepted"),
            "Rejected" to getApplicationCountByStatus(applications, "Rejected"),
            "Interview Scheduled" to getApplicationCountByStatus(applications, "Interview Scheduled")
        )
    }
}
