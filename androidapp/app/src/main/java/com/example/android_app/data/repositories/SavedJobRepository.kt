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
            val jobSnapshot = firebase.collection("jobs")
                .document(jobId)
                .get()
                .await()

            val jobData = jobSnapshot.data ?: return

            // 2. Get company ID
            val companyId = jobData["companyId"] as? String ?: return

            // 3. Fetch company document
            val companySnapshot = firebase.collection("companies")
                .document(companyId)
                .get()
                .await()

            val companyName = companySnapshot.getString("name") ?: "Unknown"

            // 4. Create SavedJobEntity
            val savedJob = SavedJobsEntity(
                id = jobSnapshot.id,
                title = jobData["title"] as? String ?: "",
                company = companyName,
                location = jobData["location"] as? String ?: "",
                salary = jobData["salary"] as? String ?: "",
                jobType = jobData["jobType"] as? String ?: ""
            )

            // 5. Insert into Room
            dao.insertJob(savedJob)

        } catch (e: Exception) {
            e.printStackTrace() // You can log or handle errors properly here
        }
    }

    // Remove job from Room
    suspend fun removeJob(job: SavedJobsEntity) {
        dao.deleteJob(job)
    }

}
