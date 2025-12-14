package com.example.android_app.data.repositories

import com.example.android_app.data.DAOs.SavedJobDao
import com.example.android_app.data.entities.SavedJobsEntity
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SavedJobRepository @Inject constructor(
    private val dao: SavedJobDao,
    private val firebase: FirebaseFirestore
) {
    fun getSavedJobs(): Flow<List<SavedJobsEntity>> = dao.getAllSavedJobs()

    suspend fun saveJob(jobId: String) {
        try {
            val jobSnapshot = firebase.collectionGroup("Jobs")
                .whereEqualTo(FieldPath.documentId(), jobId)
                .limit(1)
                .get()
                .await()
                .documents
                .firstOrNull() ?: return

            val companyRef = jobSnapshot.reference.parent.parent
            val companyName = companyRef?.get()?.await()?.let {
                it.getString("name") ?: it.getString("companyName") ?: "Unknown Company"
            } ?: "Unknown Company"

            val savedJob = SavedJobsEntity(
                id = jobSnapshot.id,
                // Using keys from Job.kt
                title = jobSnapshot.getString("Title") ?: jobSnapshot.getString("title") ?: "",
                company = companyName,
                location = jobSnapshot.getString("Location") ?: jobSnapshot.getString("location") ?: "",
                salary = jobSnapshot.getString("Salary") ?: jobSnapshot.getString("salary") ?: "",
                jobType = jobSnapshot.getString("JobType") ?: jobSnapshot.getString("jobType") ?: ""
            )

            dao.insertJob(savedJob)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun removeJob(job: SavedJobsEntity) {
        dao.deleteJob(job)
    }
}