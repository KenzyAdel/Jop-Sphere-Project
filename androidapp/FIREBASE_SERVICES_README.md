# Firebase Services for Job Sphere Android App

A comprehensive Firebase Firestore integration for the Job Sphere Android application, providing complete CRUD operations for all entities.

## 📁 Project Structure

```
app/src/main/java/com/example/android_app/
├── data/
│   ├── models/
│   │   ├── Applicant.kt
│   │   ├── Company.kt
│   │   ├── Job.kt
│   │   └── Application.kt
│   └── services/
│       ├── ApplicantService.kt
│       ├── CompanyService.kt
│       ├── JobService.kt
│       ├── ApplicationService.kt
│       └── FirebaseManager.kt
└── presentation/
    └── company/
        └── applicationManagement/
            └── ApplicationManagementViewModel.kt
```

## 🗄️ Firestore Database Structure

```
Firestore Database
├── Applicant (Collection)
│   └── {applicantId} (Document)
│       ├── CV-Link: String
│       ├── Email: String
│       ├── Linkedin: String
│       ├── Password: String
│       └── Phone: String
│
├── Company (Collection)
    └── {companyId} (Document)
        ├── Email: String
        ├── Industry: String
        ├── Linkedin: String
        ├── Name: String
        ├── Password: String
        ├── Phone: String
        ├── Website: String
        └── Jobs (Subcollection)
            └── {jobId} (Document)
                ├── Description: String
                ├── JobType: String
                ├── Location: String
                ├── Requirements: String
                ├── Responsibilities: String
                ├── Salary: String
                ├── Title: String
                └── application (Subcollection)
                    └── {applicationId} (Document)
                        ├── CV-Link: String
                        ├── Email: String
                        ├── LinkedId: String
                        ├── Name: String
                        ├── Phone: String
                        └── Status: String
```

## 🚀 Features

### Data Models
- **Applicant**: User profiles for job seekers
- **Company**: Company profiles with job postings
- **Job**: Job listings (subcollection under Company)
- **Application**: Job applications (subcollection under Jobs)

### Services
Each service provides comprehensive CRUD operations:

#### ApplicantService
- ✅ Create applicant (with auto/custom ID)
- ✅ Read applicant by ID
- ✅ Read all applicants
- ✅ Search by email
- ✅ Search by phone
- ✅ Update applicant (full/partial)
- ✅ Delete applicant

#### CompanyService
- ✅ Create company (with auto/custom ID)
- ✅ Read company by ID
- ✅ Read all companies
- ✅ Search by email
- ✅ Filter by industry
- ✅ Search by name
- ✅ Update company (full/partial)
- ✅ Delete company

#### JobService
- ✅ Create job (with auto/custom ID)
- ✅ Read job by ID
- ✅ Read all jobs for a company
- ✅ Read all jobs across all companies
- ✅ Filter by job type
- ✅ Filter by location
- ✅ Search by title
- ✅ Update job (full/partial)
- ✅ Delete job
- ✅ Delete all jobs for a company

#### ApplicationService
- ✅ Create application (with auto/custom ID)
- ✅ Read application by ID
- ✅ Read all applications for a job
- ✅ Read all applications for a company
- ✅ Filter by status
- ✅ Filter by email
- ✅ Update application (full/partial)
- ✅ Update application status
- ✅ Delete application
- ✅ Delete all applications for a job

### FirebaseManager
A singleton class providing unified access to all services:
```kotlin
val firebaseManager = FirebaseManager.getInstance()
firebaseManager.applicants.createApplicant(...)
firebaseManager.companies.getAllCompanies()
firebaseManager.jobs.searchJobsByTitle(...)
firebaseManager.applications.updateApplicationStatus(...)
```

## 📖 Usage

### Quick Start

```kotlin
// In your ViewModel
class MyViewModel : ViewModel() {
    private val firebase = FirebaseManager.getInstance()
    
    fun loadData() {
        viewModelScope.launch {
            // Get all applicants
            firebase.applicants.getAllApplicants()
                .onSuccess { applicants ->
                    // Handle success
                }
                .onFailure { error ->
                    // Handle error
                }
        }
    }
}
```

### Example: Create and Submit Application

```kotlin
viewModelScope.launch {
    // 1. Create an application
    val application = Application(
        companyId = "company123",
        jobId = "job456",
        cvLink = "https://example.com/cv.pdf",
        email = "john@example.com",
        linkedIn = "https://linkedin.com/in/johndoe",
        name = "John Doe",
        phone = "+1234567890",
        status = "Pending"
    )
    
    // 2. Submit to Firebase
    firebaseManager.applications.createApplication(
        companyId = "company123",
        jobId = "job456",
        application = application
    ).onSuccess { applicationId ->
        println("Application submitted: $applicationId")
    }.onFailure { error ->
        println("Error: ${error.message}")
    }
}
```

### Example: Update Application Status

```kotlin
viewModelScope.launch {
    firebaseManager.applications.updateApplicationStatus(
        companyId = "company123",
        jobId = "job456",
        applicationId = "app789",
        status = "Accepted"
    ).onSuccess {
        println("Status updated to Accepted")
    }.onFailure { error ->
        println("Error: ${error.message}")
    }
}
```

## 📚 Documentation

For comprehensive examples and usage patterns, see:
- **[FIREBASE_USAGE_GUIDE.md](./FIREBASE_USAGE_GUIDE.md)** - Complete usage guide with examples

## 🎯 Example ViewModel

An example `ApplicationManagementViewModel.kt` is included that demonstrates:
- ✅ State management with StateFlow
- ✅ Loading states (Idle, Loading, Success, Error)
- ✅ CRUD operations
- ✅ Status updates
- ✅ Application statistics
- ✅ Error handling

## 🔧 Dependencies

Already configured in `app/build.gradle.kts`:
```kotlin
// Firebase BoM
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

// Firebase services
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")
implementation("com.google.firebase:firebase-storage")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
```

## 🎨 UI Integration (Jetpack Compose)

```kotlin
@Composable
fun ApplicationListScreen(viewModel: ApplicationManagementViewModel) {
    val state by viewModel.applicationsState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadApplicationsForCompany("company_id")
    }
    
    when (val currentState = state) {
        is ApplicationsState.Loading -> {
            CircularProgressIndicator()
        }
        is ApplicationsState.Success -> {
            LazyColumn {
                items(currentState.applications) { app ->
                    ApplicationItem(
                        application = app,
                        onAccept = { 
                            viewModel.acceptApplication(
                                app.companyId, 
                                app.jobId, 
                                app.id
                            ) 
                        },
                        onReject = { 
                            viewModel.rejectApplication(
                                app.companyId, 
                                app.jobId, 
                                app.id
                            ) 
                        }
                    )
                }
            }
        }
        is ApplicationsState.Error -> {
            Text("Error: ${currentState.message}")
        }
        ApplicationsState.Idle -> {
            // Initial state
        }
    }
}
```

## ⚠️ Important Notes

1. **Coroutines**: All operations are suspend functions. Use `viewModelScope.launch` or `lifecycleScope.launch`
2. **Result Type**: All operations return `Result<T>` - use `.onSuccess` and `.onFailure` for handling
3. **Error Handling**: Always handle both success and failure cases
4. **Subcollections**: Jobs are under Company, Applications are under Jobs
5. **Field Names**: Firestore field names match the database (e.g., "CV-Link", "Email", "LinkedId")

## 🔐 Security

Remember to configure Firebase Security Rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Add your security rules here
    match /Applicant/{applicantId} {
      allow read, write: if request.auth != null;
    }
    
    match /Company/{companyId} {
      allow read, write: if request.auth != null;
      
      match /Jobs/{jobId} {
        allow read: if true;
        allow write: if request.auth != null;
        
        match /application/{applicationId} {
          allow read, write: if request.auth != null;
        }
      }
    }
  }
}
```

## 🎓 Best Practices

1. **Use ViewModel**: Always use ViewModels for business logic
2. **State Management**: Use StateFlow for reactive UI updates
3. **Error Handling**: Implement proper error handling with sealed classes
4. **Loading States**: Show loading indicators during operations
5. **Validation**: Validate data before sending to Firebase
6. **Offline Support**: Firebase Firestore has built-in offline support

## 📝 License

Part of the Job Sphere Android Application

---

**Created**: December 2025  
**Author**: Job Sphere Development Team  
**Technology**: Kotlin, Firebase Firestore, Jetpack Compose, Coroutines
