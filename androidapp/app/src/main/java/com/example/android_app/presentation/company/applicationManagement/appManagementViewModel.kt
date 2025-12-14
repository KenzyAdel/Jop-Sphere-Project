package com.example.android_app.presentation.company.applicationManagement

import android.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.services.ApplicantService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.android_app.data.services.ApplicationService
import com.google.firebase.auth.FirebaseAuth

class AppManagementViewModel : ViewModel() {

    private val applicationService = ApplicationService()
    private val auth = FirebaseAuth.getInstance()
    private val companyId: String
        get() = auth.currentUser?.uid ?: "" 

    private val _uiState = MutableStateFlow(AppManagementUiState())
    val uiState: StateFlow<AppManagementUiState> = _uiState.asStateFlow()

    // Store the current jobId for status updates
    private var currentJobId: String = ""

     fun fetchApplicants(jobID: String) {
        currentJobId = jobID // Store jobId for later use
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            if (companyId.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in as Company") }
                return@launch
            }

            // Fetch applications for this company
            println("DEBUG: Fetching applications for CompanyId=$companyId")
            val result = applicationService.getAllApplicationsForJob(companyId, jobID)

            if (result.isSuccess) {
                val applications = result.getOrNull() ?: emptyList()
                val uiApplicants = applications.map { application ->
                    TempApplicantUiItem(
                        id = application.id, // This is the applicantId
                        fullName = application.name,
                        email = application.email,
                        phone = application.phone,
                        linkedIn = application.linkedIn,
                        portfolio = application.cvLink, // Mapping CV Link to Portfolio for now
                        status = application.status
                    )
                }
                _uiState.update { it.copy(isLoading = false, applicants = uiApplicants) }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to fetch applications"
                    ) 
                }
            }
        }
    }

    /**
     * Updates the status of an applicant's application in Firebase
     * Following MVVM pattern: UI -> ViewModel -> Service (Firebase)
     * 
     * @param applicationId The ID of the application to update
     * @param newStatus The new status to set (e.g., "Accepted", "Rejected", "Interview", etc.)
     */
    fun updateApplicationStatus(applicationId: String, newStatus: String) {
        viewModelScope.launch {
            println("DEBUG ViewModel: updateApplicationStatus called")
            println("DEBUG ViewModel: applicationId=$applicationId, newStatus=$newStatus")
            println("DEBUG ViewModel: companyId=$companyId")
            println("DEBUG ViewModel: currentJobId=$currentJobId")
            
            if (companyId.isEmpty()) {
                println("DEBUG ViewModel: ERROR - companyId is empty")
                _uiState.update { it.copy(errorMessage = "User not logged in as Company") }
                return@launch
            }

            if (currentJobId.isEmpty()) {
                println("DEBUG ViewModel: ERROR - currentJobId is empty")
                _uiState.update { it.copy(errorMessage = "Job ID not found") }
                return@launch
            }

            println("DEBUG: Updating application status - AppId=$applicationId, Status=$newStatus")
            
            // Call the ApplicationService to update status in Firebase
            val result = applicationService.updateApplicationStatus(
                companyId = companyId,
                jobId = currentJobId,
                applicationId = applicationId,
                status = newStatus
            )

            if (result.isSuccess) {
                // Update the local UI state to reflect the change immediately
                _uiState.update { currentState ->
                    val updatedApplicants = currentState.applicants.map { applicant ->
                        if (applicant.id == applicationId) {
                            applicant.copy(status = newStatus)
                        } else {
                            applicant
                        }
                    }
                    currentState.copy(applicants = updatedApplicants)
                }
                println("DEBUG: Application status updated successfully")
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to update status"
                _uiState.update { it.copy(errorMessage = errorMsg) }
                println("DEBUG: Error updating status - $errorMsg")
            }
        }
    }
}