package com.example.android_app.presentation.applicant.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.services.CompanyService
import com.example.android_app.data.services.JobService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ApplicantHomeViewModel(
    private val applicantId: String = "test_applicant_001", // TODO: Replace with actual logged-in user ID
    private val jobService: JobService = JobService(),
    private val companyService: CompanyService = CompanyService()
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _state = MutableStateFlow(ApplicantHomeState())
    val state: StateFlow<ApplicantHomeState> = _state.asStateFlow()

    private var cachedJobs: List<com.example.android_app.data.models.Job> = emptyList()

    init {
        loadJobs()
        loadSavedJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. Fetch all companies first to create a map of ID -> Company Name
            val companiesResult = companyService.getAllCompanies()
            val jobsResult = jobService.getAllJobs()

            if (jobsResult.isSuccess && companiesResult.isSuccess) {
                val companies = companiesResult.getOrNull() ?: emptyList()
                val jobs = jobsResult.getOrNull() ?: emptyList()

                // Cache jobs for later lookup
                cachedJobs = jobs

                // Create a map for quick lookup: CompanyId -> CompanyName
                val companyNameMap = companies.associate { it.id to it.name }

                // 2. Map Domain Job to UI JobItem
                val uiJobs = jobs.map { job ->
                    JobItem(
                        id = job.id,
                        title = job.title,
                        company = companyNameMap[job.companyId] ?: "Unknown Company",
                        location = job.location,
                        salary = job.salary
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        jobs = uiJobs,
                        allJobsCached = uiJobs // Save full list for filtering
                    )
                }
            } else {
                // Handle Error
                val error = jobsResult.exceptionOrNull()?.message
                    ?: companiesResult.exceptionOrNull()?.message
                    ?: "Unknown error occurred"

                _state.update { it.copy(isLoading = false, errorMessage = error) }
            }
        }
    }

    fun onFilterSelected(filterType: String) {
        _state.update { currentState ->
            val filteredList = if (filterType == "All" || filterType == "Filter by") {
                currentState.allJobsCached
            } else {
                // Assuming 'JobType' in Firestore matches the filter strings
                currentState.allJobsCached.filter { jobItem ->
                    // Note: JobItem doesn't currently have 'type',
                    // you might need to add 'type' to JobItem to filter accurately.
                    // For now, checking if description or title contains the filter
                    // or strictly mapping back to domain logic.
                    true // Placeholder: Implement specific filtering logic based on JobItem fields
                }
            }
            currentState.copy(selectedFilter = filterType, jobs = filteredList)
        }
    }

    fun onSaveJob(job: JobItem) {
        viewModelScope.launch {
            val jobId = job.id
            val isSaved = _state.value.savedJobIds.contains(jobId)

            try {
                if (isSaved) {
                    // Unsave the job
                    db.collection("Applicant")
                        .document(applicantId)
                        .collection("SavedJobs")
                        .document(jobId)
                        .delete()
                        .await()

                    // Update local state
                    _state.update { it.copy(savedJobIds = it.savedJobIds - jobId) }
                    println("Unsaved job: ${job.title}")
                } else {
                    // Save the job
                    val domainJob = cachedJobs.find { it.id == jobId }
                    if (domainJob != null) {
                        val savedJobData = hashMapOf(
                            "JobId" to jobId,
                            "CompanyId" to domainJob.companyId,
                            "SavedAt" to System.currentTimeMillis()
                        )

                        db.collection("Applicant")
                            .document(applicantId)
                            .collection("SavedJobs")
                            .document(jobId)
                            .set(savedJobData)
                            .await()

                        // Update local state
                        _state.update { it.copy(savedJobIds = it.savedJobIds + jobId) }
                        println("Saved job: ${job.title}")
                    } else {
                        println("Error: Job not found in cache")
                    }
                }
            } catch (e: Exception) {
                println("Error saving/unsaving job: ${e.message}")
            }
        }
    }

    private fun loadSavedJobs() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("Applicant")
                    .document(applicantId)
                    .collection("SavedJobs")
                    .get()
                    .await()

                val savedJobIds = snapshot.documents.map { it.id }.toSet()
                _state.update { it.copy(savedJobIds = savedJobIds) }
            } catch (e: Exception) {
                println("Error loading saved jobs: ${e.message}")
            }
        }
    }

    fun onLogout() {
        // Handle logout logic (clear session, firebase auth signout, etc.)
    }
}