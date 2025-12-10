package com.example.android_app.examples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.models.Applicant
import com.example.android_app.data.models.Application
import com.example.android_app.data.models.Company
import com.example.android_app.data.models.Job
import com.example.android_app.data.services.FirebaseManager
import kotlinx.coroutines.launch

/**
 * Quick Start Examples for Firebase Services
 * 
 * This file contains practical examples of how to use the Firebase services
 * in your application. Copy and adapt these examples to your needs.
 */
class FirebaseQuickStartExamples : ViewModel() {
    
    private val firebase = FirebaseManager.getInstance()
    
    // ========== APPLICANT EXAMPLES ==========
    
    /**
     * Example 1: Create a new applicant
     */
    fun createNewApplicant() {
        viewModelScope.launch {
            val applicant = Applicant(
                cvLink = "https://example.com/john-cv.pdf",
                email = "john.doe@email.com",
                linkedin = "https://linkedin.com/in/johndoe",
                phone = "+1234567890"
            )
            
            firebase.applicants.createApplicant(applicant)
                .onSuccess { id ->
                    println("✅ Applicant created with ID: $id")
                }
                .onFailure { error ->
                    println("❌ Error: ${error.message}")
                }
        }
    }
    
    /**
     * Example 2: Login - Get applicant by email
     */
    fun loginApplicant(email: String, password: String) {
        viewModelScope.launch {
            firebase.applicants.getApplicantByEmail(email)
                .onSuccess { applicant ->
                    if (applicant != null ) {
                        println("✅ Login successful! Welcome ${applicant.email}")
                        // Navigate to applicant dashboard
                    } else {
                        println("❌ Invalid credentials")
                    }
                }
                .onFailure { error ->
                    println("❌ Login error: ${error.message}")
                }
        }
    }
    
    /**
     * Example 3: Update applicant profile
     */
    fun updateApplicantProfile(applicantId: String, newCvLink: String, newPhone: String) {
        viewModelScope.launch {
            // Option 1: Update specific fields
            val updates = mapOf(
                "CV-Link" to newCvLink,
                "Phone" to newPhone
            )
            
            firebase.applicants.updateApplicantFields(applicantId, updates)
                .onSuccess {
                    println("✅ Profile updated successfully")
                }
                .onFailure { error ->
                    println("❌ Update error: ${error.message}")
                }
        }
    }
    
    // ========== COMPANY EXAMPLES ==========
    
    /**
     * Example 4: Register a new company
     */
    fun registerCompany() {
        viewModelScope.launch {
            val company = Company(
                email = "hr@techcorp.com",
                industry = "Technology",
                linkedin = "https://linkedin.com/company/techcorp",
                name = "Tech Corp Inc.",
                phone = "+1-555-0100",
                website = "https://techcorp.com"
            )
            
            firebase.companies.createCompany(company)
                .onSuccess { id ->
                    println("✅ Company registered with ID: $id")
                }
                .onFailure { error ->
                    println("❌ Registration error: ${error.message}")
                }
        }
    }
    
    /**
     * Example 5: Browse companies by industry
     */
    fun browseCompaniesByIndustry(industry: String) {
        viewModelScope.launch {
            firebase.companies.getCompaniesByIndustry(industry)
                .onSuccess { companies ->
                    println("✅ Found ${companies.size} companies in $industry")
                    companies.forEach { company ->
                        println("   - ${company.name} (${company.email})")
                    }
                }
                .onFailure { error ->
                    println("❌ Error: ${error.message}")
                }
        }
    }
    
    // ========== JOB EXAMPLES ==========
    
    /**
     * Example 6: Post a new job
     */
    fun postNewJob(companyId: String) {
        viewModelScope.launch {
            val job = Job(
                companyId = companyId,
                description = "We are seeking a talented Android developer to join our team. You will be responsible for developing and maintaining our mobile applications using Kotlin and Jetpack Compose.",
                jobType = "Full-time",
                location = "Remote",
                requirements = "• 3+ years of Android development experience\n• Strong knowledge of Kotlin\n• Experience with Jetpack Compose\n• Understanding of MVVM architecture",
                responsibilities = "• Develop new features for Android app\n• Write clean, maintainable code\n• Collaborate with cross-functional teams\n• Participate in code reviews",
                salary = "$80,000 - $120,000 per year",
                title = "Senior Android Developer"
            )
            
            firebase.jobs.createJob(companyId, job)
                .onSuccess { jobId ->
                    println("✅ Job posted with ID: $jobId")
                }
                .onFailure { error ->
                    println("❌ Error posting job: ${error.message}")
                }
        }
    }
    
    /**
     * Example 7: Search for jobs
     */
    fun searchJobs() {
        viewModelScope.launch {
            // Get all jobs across all companies
            firebase.jobs.getAllJobs()
                .onSuccess { jobs ->
                    println("✅ Found ${jobs.size} total jobs")
                    
                    // Filter jobs by criteria
                    val remoteJobs = jobs.filter { it.location == "Remote" }
                    val fullTimeJobs = jobs.filter { it.jobType == "Full-time" }
                    val androidJobs = jobs.filter { 
                        it.title.contains("Android", ignoreCase = true) 
                    }
                    
                    println("   📍 Remote jobs: ${remoteJobs.size}")
                    println("   ⏰ Full-time jobs: ${fullTimeJobs.size}")
                    println("   📱 Android jobs: ${androidJobs.size}")
                }
                .onFailure { error ->
                    println("❌ Error: ${error.message}")
                }
        }
    }
    
    /**
     * Example 8: Get jobs for a specific company
     */
    fun getCompanyJobs(companyId: String) {
        viewModelScope.launch {
            firebase.jobs.getAllJobsForCompany(companyId)
                .onSuccess { jobs ->
                    println("✅ Company has ${jobs.size} job openings:")
                    jobs.forEach { job ->
                        println("   - ${job.title} (${job.location}) - ${job.salary}")
                    }
                }
                .onFailure { error ->
                    println("❌ Error: ${error.message}")
                }
        }
    }
    
    // ========== APPLICATION EXAMPLES ==========
    
    /**
     * Example 9: Submit job application
     */
    fun submitJobApplication(companyId: String, jobId: String) {
        viewModelScope.launch {
            val application = Application(
                companyId = companyId,
                jobId = jobId,
                cvLink = "https://example.com/john-cv.pdf",
                email = "john.doe@email.com",
                linkedIn = "https://linkedin.com/in/johndoe",
                name = "John Doe",
                phone = "+1234567890",
                status = "Pending"
            )
            
            firebase.applications.createApplication(companyId, jobId, application)
                .onSuccess { applicationId ->
                    println("✅ Application submitted! ID: $applicationId")
                    println("   Status: Pending Review")
                }
                .onFailure { error ->
                    println("❌ Submission error: ${error.message}")
                }
        }
    }
    
    /**
     * Example 10: Company reviews applications
     */
    fun reviewApplications(companyId: String, jobId: String) {
        viewModelScope.launch {
            firebase.applications.getAllApplicationsForJob(companyId, jobId)
                .onSuccess { applications ->
                    println("✅ Reviewing ${applications.size} applications")
                    
                    // Group by status
                    val pending = applications.filter { it.status == "Pending" }
                    val accepted = applications.filter { it.status == "Accepted" }
                    val rejected = applications.filter { it.status == "Rejected" }
                    val interviewed = applications.filter { it.status == "Interview Scheduled" }
                    
                    println("   ⏳ Pending: ${pending.size}")
                    println("   ✅ Accepted: ${accepted.size}")
                    println("   ❌ Rejected: ${rejected.size}")
                    println("   📞 Interviews: ${interviewed.size}")
                    
                    // List pending applications
                    println("\n📋 Pending Applications:")
                    pending.forEach { app ->
                        println("   - ${app.name} (${app.email})")
                        println("     CV: ${app.cvLink}")
                    }
                }
                .onFailure { error ->
                    println("❌ Error: ${error.message}")
                }
        }
    }
    
    /**
     * Example 11: Accept an application
     */
    fun acceptApplication(companyId: String, jobId: String, applicationId: String) {
        viewModelScope.launch {
            firebase.applications.updateApplicationStatus(
                companyId,
                jobId,
                applicationId,
                "Accepted"
            ).onSuccess {
                println("✅ Application accepted! Candidate has been notified.")
            }.onFailure { error ->
                println("❌ Error: ${error.message}")
            }
        }
    }
    
    /**
     * Example 12: Schedule interview
     */
    fun scheduleInterview(companyId: String, jobId: String, applicationId: String) {
        viewModelScope.launch {
            firebase.applications.updateApplicationStatus(
                companyId,
                jobId,
                applicationId,
                "Interview Scheduled"
            ).onSuccess {
                println("✅ Interview scheduled! Follow up with candidate.")
            }.onFailure { error ->
                println("❌ Error: ${error.message}")
            }
        }
    }
    
    // ========== COMPLETE WORKFLOW EXAMPLE ==========
    
    /**
     * Example 13: Complete job application workflow
     */
    fun completeJobApplicationWorkflow() {
        viewModelScope.launch {
            // Step 1: Applicant searches for jobs
            firebase.jobs.getAllJobs()
                .onSuccess { jobs ->
                    println("Step 1: Found ${jobs.size} jobs")
                    
                    // Step 2: Applicant picks a job
                    val selectedJob = jobs.firstOrNull { it.title.contains("Android") }
                    
                    if (selectedJob != null) {
                        println("Step 2: Selected job: ${selectedJob.title}")
                        
                        // Step 3: Submit application
                        val application = Application(
                            companyId = selectedJob.companyId,
                            jobId = selectedJob.id,
                            cvLink = "https://example.com/cv.pdf",
                            email = "applicant@email.com",
                            linkedIn = "https://linkedin.com/in/applicant",
                            name = "Jane Smith",
                            phone = "+1234567890",
                            status = "Pending"
                        )
                        
                        firebase.applications.createApplication(
                            selectedJob.companyId,
                            selectedJob.id,
                            application
                        ).onSuccess { appId ->
                            println("Step 3: Application submitted with ID: $appId")
                            
                            // Step 4: Company reviews and accepts
                            firebase.applications.updateApplicationStatus(
                                selectedJob.companyId,
                                selectedJob.id,
                                appId,
                                "Interview Scheduled"
                            ).onSuccess {
                                println("Step 4: Interview scheduled!")
                                println("✅ Workflow complete!")
                            }
                        }
                    }
                }
        }
    }
}
