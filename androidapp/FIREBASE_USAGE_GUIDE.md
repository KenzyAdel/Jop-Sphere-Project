# Firebase Services - Usage Guide

This document provides comprehensive examples of how to use the Firebase services in your Android app.

## Table of Contents
1. [Getting Started](#getting-started)
2. [Applicant Operations](#applicant-operations)
3. [Company Operations](#company-operations)
4. [Job Operations](#job-operations)
5. [Application Operations](#application-operations)
6. [Error Handling](#error-handling)

## Getting Started

### Initialize Firebase Manager

```kotlin
// In your ViewModel or Repository
private val firebaseManager = FirebaseManager.getInstance()
```

Or use individual services:

```kotlin
private val applicantService = ApplicantService()
private val companyService = CompanyService()
private val jobService = JobService()
private val applicationService = ApplicationService()
```

## Applicant Operations

### Create Applicant

```kotlin
viewModelScope.launch {
    val applicant = Applicant(
        cvLink = "https://example.com/cv.pdf",
        email = "john@example.com",
        linkedin = "https://linkedin.com/in/johndoe",
        password = "securepassword123",
        phone = "+1234567890"
    )
    
    firebaseManager.applicants.createApplicant(applicant)
        .onSuccess { id ->
            println("Applicant created with ID: $id")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get Applicant by ID

```kotlin
viewModelScope.launch {
    firebaseManager.applicants.getApplicant("applicant_id")
        .onSuccess { applicant ->
            if (applicant != null) {
                println("Found applicant: ${applicant.email}")
            } else {
                println("Applicant not found")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get All Applicants

```kotlin
viewModelScope.launch {
    firebaseManager.applicants.getAllApplicants()
        .onSuccess { applicants ->
            println("Found ${applicants.size} applicants")
            applicants.forEach { applicant ->
                println("- ${applicant.email}")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Update Applicant

```kotlin
viewModelScope.launch {
    val updatedApplicant = applicant.copy(
        phone = "+9876543210",
        cvLink = "https://example.com/updated-cv.pdf"
    )
    
    firebaseManager.applicants.updateApplicant("applicant_id", updatedApplicant)
        .onSuccess {
            println("Applicant updated successfully")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Update Specific Fields

```kotlin
viewModelScope.launch {
    val updates = mapOf(
        "Phone" to "+9876543210",
        "CV-Link" to "https://example.com/new-cv.pdf"
    )
    
    firebaseManager.applicants.updateApplicantFields("applicant_id", updates)
        .onSuccess {
            println("Fields updated successfully")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Delete Applicant

```kotlin
viewModelScope.launch {
    firebaseManager.applicants.deleteApplicant("applicant_id")
        .onSuccess {
            println("Applicant deleted successfully")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Search Applicant by Email

```kotlin
viewModelScope.launch {
    firebaseManager.applicants.getApplicantByEmail("john@example.com")
        .onSuccess { applicant ->
            if (applicant != null) {
                println("Found: ${applicant.email}")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

## Company Operations

### Create Company

```kotlin
viewModelScope.launch {
    val company = Company(
        email = "info@techcorp.com",
        industry = "Technology",
        linkedin = "https://linkedin.com/company/techcorp",
        name = "Tech Corp",
        password = "securepassword",
        phone = "+1234567890",
        website = "https://techcorp.com"
    )
    
    firebaseManager.companies.createCompany(company)
        .onSuccess { id ->
            println("Company created with ID: $id")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get Company by ID

```kotlin
viewModelScope.launch {
    firebaseManager.companies.getCompany("company_id")
        .onSuccess { company ->
            if (company != null) {
                println("Company: ${company.name}")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get Companies by Industry

```kotlin
viewModelScope.launch {
    firebaseManager.companies.getCompaniesByIndustry("Technology")
        .onSuccess { companies ->
            println("Found ${companies.size} tech companies")
            companies.forEach { company ->
                println("- ${company.name}")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Update Company

```kotlin
viewModelScope.launch {
    val updatedCompany = company.copy(
        website = "https://new-website.com",
        phone = "+9876543210"
    )
    
    firebaseManager.companies.updateCompany("company_id", updatedCompany)
        .onSuccess {
            println("Company updated")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

## Job Operations

### Create Job

```kotlin
viewModelScope.launch {
    val job = Job(
        companyId = "company_id",
        description = "We are looking for a skilled Android developer...",
        jobType = "Full-time",
        location = "Remote",
        requirements = "3+ years of Android development experience",
        responsibilities = "Develop and maintain Android applications",
        salary = "$80,000 - $120,000",
        title = "Senior Android Developer"
    )
    
    firebaseManager.jobs.createJob("company_id", job)
        .onSuccess { jobId ->
            println("Job created with ID: $jobId")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get All Jobs for a Company

```kotlin
viewModelScope.launch {
    firebaseManager.jobs.getAllJobsForCompany("company_id")
        .onSuccess { jobs ->
            println("Found ${jobs.size} jobs")
            jobs.forEach { job ->
                println("- ${job.title} (${job.location})")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get All Jobs (Across All Companies)

```kotlin
viewModelScope.launch {
    firebaseManager.jobs.getAllJobs()
        .onSuccess { jobs ->
            println("Total jobs available: ${jobs.size}")
            jobs.forEach { job ->
                println("- ${job.title} at Company ID: ${job.companyId}")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get Jobs by Type

```kotlin
viewModelScope.launch {
    firebaseManager.jobs.getJobsByType("company_id", "Full-time")
        .onSuccess { jobs ->
            println("Full-time jobs: ${jobs.size}")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get Jobs by Location

```kotlin
viewModelScope.launch {
    firebaseManager.jobs.getJobsByLocation("company_id", "Remote")
        .onSuccess { jobs ->
            println("Remote jobs: ${jobs.size}")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Search Jobs by Title

```kotlin
viewModelScope.launch {
    firebaseManager.jobs.searchJobsByTitle("company_id", "Android")
        .onSuccess { jobs ->
            println("Android jobs: ${jobs.size}")
            jobs.forEach { job ->
                println("- ${job.title}")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Update Job

```kotlin
viewModelScope.launch {
    val updatedJob = job.copy(
        salary = "$90,000 - $130,000",
        description = "Updated job description..."
    )
    
    firebaseManager.jobs.updateJob("company_id", "job_id", updatedJob)
        .onSuccess {
            println("Job updated")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Delete Job

```kotlin
viewModelScope.launch {
    firebaseManager.jobs.deleteJob("company_id", "job_id")
        .onSuccess {
            println("Job deleted")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

## Application Operations

### Submit Application

```kotlin
viewModelScope.launch {
    val application = Application(
        companyId = "company_id",
        jobId = "job_id",
        cvLink = "https://example.com/cv.pdf",
        email = "applicant@example.com",
        linkedIn = "https://linkedin.com/in/applicant",
        name = "John Doe",
        phone = "+1234567890",
        status = "Pending"
    )
    
    firebaseManager.applications.createApplication("company_id", "job_id", application)
        .onSuccess { appId ->
            println("Application submitted with ID: $appId")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get All Applications for a Job

```kotlin
viewModelScope.launch {
    firebaseManager.applications.getAllApplicationsForJob("company_id", "job_id")
        .onSuccess { applications ->
            println("Applications: ${applications.size}")
            applications.forEach { app ->
                println("- ${app.name} (${app.status})")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get All Applications for a Company

```kotlin
viewModelScope.launch {
    firebaseManager.applications.getAllApplicationsForCompany("company_id")
        .onSuccess { applications ->
            println("Total applications: ${applications.size}")
            applications.forEach { app ->
                println("- ${app.name} for Job ${app.jobId}")
            }
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Get Applications by Status

```kotlin
viewModelScope.launch {
    firebaseManager.applications.getApplicationsByStatus("company_id", "job_id", "Pending")
        .onSuccess { applications ->
            println("Pending applications: ${applications.size}")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

### Update Application Status

```kotlin
viewModelScope.launch {
    firebaseManager.applications.updateApplicationStatus(
        "company_id",
        "job_id",
        "application_id",
        "Accepted"
    ).onSuccess {
        println("Status updated to Accepted")
    }.onFailure { error ->
        println("Error: ${error.message}")
    }
}
```

### Update Application

```kotlin
viewModelScope.launch {
    val updatedApplication = application.copy(
        status = "Interview Scheduled",
        phone = "+9876543210"
    )
    
    firebaseManager.applications.updateApplication(
        "company_id",
        "job_id",
        "application_id",
        updatedApplication
    ).onSuccess {
        println("Application updated")
    }.onFailure { error ->
        println("Error: ${error.message}")
    }
}
```

### Delete Application

```kotlin
viewModelScope.launch {
    firebaseManager.applications.deleteApplication("company_id", "job_id", "application_id")
        .onSuccess {
            println("Application deleted")
        }
        .onFailure { error ->
            println("Error: ${error.message}")
        }
}
```

## Error Handling

### Best Practices

```kotlin
viewModelScope.launch {
    try {
        firebaseManager.applicants.getApplicant("applicant_id")
            .onSuccess { applicant ->
                // Handle success
                applicant?.let {
                    _applicantState.value = ApplicantState.Success(it)
                } ?: run {
                    _applicantState.value = ApplicantState.NotFound
                }
            }
            .onFailure { error ->
                // Handle specific errors
                when (error) {
                    is com.google.firebase.firestore.FirebaseFirestoreException -> {
                        when (error.code) {
                            com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                                _applicantState.value = ApplicantState.Error("Permission denied")
                            }
                            com.google.firebase.firestore.FirebaseFirestoreException.Code.UNAVAILABLE -> {
                                _applicantState.value = ApplicantState.Error("Service unavailable")
                            }
                            else -> {
                                _applicantState.value = ApplicantState.Error(error.message ?: "Unknown error")
                            }
                        }
                    }
                    else -> {
                        _applicantState.value = ApplicantState.Error(error.message ?: "Unknown error")
                    }
                }
            }
    } catch (e: Exception) {
        _applicantState.value = ApplicantState.Error(e.message ?: "Unexpected error")
    }
}
```

### Loading States

```kotlin
sealed class DataState<out T> {
    object Idle : DataState<Nothing>()
    object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
}

// Usage in ViewModel
private val _applicantsState = MutableStateFlow<DataState<List<Applicant>>>(DataState.Idle)
val applicantsState: StateFlow<DataState<List<Applicant>>> = _applicantsState

fun loadApplicants() {
    viewModelScope.launch {
        _applicantsState.value = DataState.Loading
        
        firebaseManager.applicants.getAllApplicants()
            .onSuccess { applicants ->
                _applicantsState.value = DataState.Success(applicants)
            }
            .onFailure { error ->
                _applicantsState.value = DataState.Error(error.message ?: "Failed to load applicants")
            }
    }
}
```

## Using in Compose UI

```kotlin
@Composable
fun ApplicantListScreen(viewModel: ApplicantViewModel = viewModel()) {
    val applicantsState by viewModel.applicantsState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadApplicants()
    }
    
    when (val state = applicantsState) {
        is DataState.Idle -> {
            // Show initial state
        }
        is DataState.Loading -> {
            CircularProgressIndicator()
        }
        is DataState.Success -> {
            LazyColumn {
                items(state.data) { applicant ->
                    ApplicantItem(applicant)
                }
            }
        }
        is DataState.Error -> {
            Text("Error: ${state.message}")
        }
    }
}
```

## Notes

- All operations are suspend functions and should be called from a coroutine
- Each operation returns a `Result<T>` that can be handled with `.onSuccess` and `.onFailure`
- Use `viewModelScope.launch` in ViewModels or `lifecycleScope.launch` in Activities/Fragments
- Remember to handle loading states and errors appropriately in your UI
- The Firebase services use Kotlin coroutines for asynchronous operations
