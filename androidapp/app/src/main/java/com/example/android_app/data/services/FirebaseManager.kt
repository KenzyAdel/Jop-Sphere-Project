package com.example.android_app.data.services

/**
 * FirebaseManager - A unified manager for all Firebase services
 * 
 * This class provides a single point of access to all Firebase services,
 * making it easier to manage Firebase operations across the app.
 * 
 * Usage Example:
 * ```
 * // In your ViewModel or Repository
 * val firebaseManager = FirebaseManager()
 * 
 * // Use any service
 * viewModelScope.launch {
 *     firebaseManager.applicants.createApplicant(applicant).onSuccess { id ->
 *         // Handle success
 *     }.onFailure { error ->
 *         // Handle error
 *     }
 * }
 * ```
 */
class FirebaseManager {
    val applicants = ApplicantService()
    val companies = CompanyService()
    val jobs = JobService()
    val applications = ApplicationService()
    
    companion object {
        @Volatile
        private var instance: FirebaseManager? = null
        
        /**
         * Get singleton instance of FirebaseManager
         */
        fun getInstance(): FirebaseManager {
            return instance ?: synchronized(this) {
                instance ?: FirebaseManager().also { instance = it }
            }
        }
    }
}
