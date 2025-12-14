package com.example.android_app.data.services

import com.example.android_app.data.models.Application
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicationService {
    private val db = FirebaseFirestore.getInstance()

    private fun getApplicationsCollection(companyId: String, jobId: String) =
        db.collection("Company")
            .document(companyId)
            .collection("Jobs")
            .document(jobId)
            .collection("application")


    suspend fun createApplicationWithId(
        companyId: String,
        jobId: String,
        applicationId: String,
        application: Application
    ): Result<Unit> {
        return try {
            getApplicationsCollection(companyId, jobId)
                .document(applicationId)
                .set(application.toHashMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun getAllApplicationsForJob(companyId: String, jobId: String): Result<List<Application>> {
        return try {
            val snapshot = getApplicationsCollection(companyId, jobId).get().await()
            val applications = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Application.fromFirestore(doc.id, companyId, jobId, it) }
            }
            Result.success(applications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateApplicationStatus(
        companyId: String,
        jobId: String,
        applicationId: String,
        status: String
    ): Result<Unit> {
        return try {
            val path = "Company/$companyId/Jobs/$jobId/application/$applicationId"
            println("DEBUG ApplicationService: Updating status at path: $path")
            println("DEBUG ApplicationService: New status: $status")
            
            getApplicationsCollection(companyId, jobId)
                .document(applicationId)
                .set(mapOf("Status" to status), com.google.firebase.firestore.SetOptions.merge())
                .await()
            
            println("DEBUG ApplicationService: Status update successful")
            Result.success(Unit)
        } catch (e: Exception) {
            println("DEBUG ApplicationService: Error updating status - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }



}