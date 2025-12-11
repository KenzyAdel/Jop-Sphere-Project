package com.example.android_app.presentation.applicant.savedJobs


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.entities.SavedJobsEntity
import com.example.android_app.data.repositories.SavedJobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class savedJobsViewModel(
    private val repository: SavedJobRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(savedJobsUiState(isLoading = true))
    val uiState: StateFlow<savedJobsUiState> = _uiState

    init {
        loadSavedJobs()
    }

    private fun loadSavedJobs() {
        viewModelScope.launch {
            repository.getSavedJobs()
                .catch { e -> _uiState.value = savedJobsUiState(error = e.message) }
                .collect { jobs ->
                    _uiState.value = savedJobsUiState(isLoading = false, savedJobs = jobs)
                }
        }
    }

    fun removeJob(job: SavedJobsEntity) {
        viewModelScope.launch {
            repository.removeJob(job)
        }
    }

    fun applyJob(job: SavedJobsEntity, onNavigate: (String) -> Unit) {
        // Just navigate to the job details screen, passing job id
        onNavigate(job.id)
    }
    fun saveJobFromFirebase(jobId: String) {
        viewModelScope.launch {
            repository.saveJob(jobId)
        }
    }
}
