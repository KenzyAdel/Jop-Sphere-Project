package com.example.android_app.data.repositories

import com.example.android_app.data.DAOs.SavedJobDao
import com.example.android_app.data.entities.SavedJobsEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class SavedJobRepository(
    private val dao: SavedJobDao,
    private val firebase: FirebaseFirestore
) {
    // Get all saved jobs from Room
    fun getSavedJobs(): Flow<List<SavedJobsEntity>> = dao.getAllSavedJobs()

    // Save job from Firebase (fetch job + company name)
    suspend fun saveJob(jobId: String) {
        try {
            // 1. Fetch job document
            println("DEBUG: Starting saveJob for ID: $jobId")
            
            // Query all jobs in the collection group and filter by document ID
            val jobQuery = firebase.collectionGroup("Jobs")
                .get()
                .await()
            
            println("DEBUG: Retrieved ${jobQuery.size()} total jobs from Firebase")
            
            // 2. Find the job with matching document ID
            val jobDocument = jobQuery.documents.find { it.id == jobId }
            
            if (jobDocument == null) {
                println("DEBUG: Job not found with ID: $jobId")
                return
            }
            println("DEBUG: Job found with ID: $jobId")

            val jobData = jobDocument.data
            if (jobData == null) {
                println("DEBUG: Job data is null")
                return
            }
            
            // DEBUG: Print all available fields in the job document
            println("DEBUG: Available fields in job document: ${jobData.keys}")
            jobData.forEach { (key, value) ->
                println("DEBUG:   $key = $value")
            }

            // 3. Get company ID from path (Parent of Jobs collection is Company/{id})
            val companyRef = jobDocument.reference.parent.parent
            if (companyRef == null) {
                println("DEBUG: Could not get company reference from job path")
                return
            }
            val companyId = companyRef.id
            println("DEBUG: Found Company ID: $companyId")

            // 4. Fetch company document
            val companySnapshot = firebase.collection("Company")
                .document(companyId)
                .get()
                .await()

            // DEBUG: Print all available fields in company document
            println("DEBUG: Company document exists: ${companySnapshot.exists()}")
            if (companySnapshot.exists()) {
                println("DEBUG: Available fields in company: ${companySnapshot.data?.keys}")
                companySnapshot.data?.forEach { (key, value) ->
                    println("DEBUG:   $key = $value")
                }
            }

            val companyName = companySnapshot.getString("name") 
                ?: companySnapshot.getString("companyName")
                ?: companySnapshot.getString("Name")
                ?: "Unknown Company"
            println("DEBUG: Fetched Company Name: $companyName")

            // 5. Create SavedJobEntity - try different field name variations
            val savedJob = SavedJobsEntity(
                id = jobDocument.id,
                title = jobData["title"] as? String 
                    ?: jobData["jobTitle"] as? String 
                    ?: jobData["Title"] as? String 
                    ?: "",
                company = companyName,
                location = jobData["location"] as? String 
                    ?: jobData["jobLocation"] as? String 
                    ?: jobData["Location"] as? String 
                    ?: "",
                salary = jobData["salary"] as? String 
                    ?: jobData["jobSalary"] as? String 
                    ?: jobData["Salary"] as? String 
                    ?: "",
                jobType = jobData["jobType"] as? String 
                    ?: jobData["type"] as? String 
                    ?: jobData["Type"] as? String 
                    ?: jobData["JobType"] as? String 
                    ?: ""
            )

            println("DEBUG: Created SavedJobEntity:")
            println("DEBUG:   id = ${savedJob.id}")
            println("DEBUG:   title = ${savedJob.title}")
            println("DEBUG:   company = ${savedJob.company}")
            println("DEBUG:   location = ${savedJob.location}")
            println("DEBUG:   salary = ${savedJob.salary}")
            println("DEBUG:   jobType = ${savedJob.jobType}")

            // 6. Insert into DAO
            dao.insertJob(savedJob)
            println("DEBUG: ✅ Successfully inserted job into DAO with ID: ${savedJob.id}")

        } catch (e: Exception) {
            e.printStackTrace()
            println("DEBUG: ❌ Error saving job in repository: ${e.message}")
        }
    }

    // Remove job from Room
    suspend fun removeJob(job: SavedJobsEntity) {
        dao.deleteJob(job)
    }

}
