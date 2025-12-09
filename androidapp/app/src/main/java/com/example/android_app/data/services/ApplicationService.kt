package com.example.android_app.data.services

import com.example.android_app.data.models.Application
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicationService {
    private val db = FirebaseFirestore.getInstance()

    // Helper function to get Application collection reference
    private fun getApplicationsCollection(companyId: String, jobId: String) =
        db.collection("Company")
            .document(companyId)
            .collection("Jobs")
            .document(jobId)
            .collection("application")

    // CREATE - Add a new application
    suspend fun createApplication(companyId: String, jobId: String, application: Application): Result<String> {
        return try {
            val docRef = getApplicationsCollection(companyId, jobId).add(application.toHashMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CREATE with custom ID
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

    // READ - Get application by ID
    suspend fun getApplication(companyId: String, jobId: String, applicationId: String): Result<Application?> {
        return try {
            val snapshot = getApplicationsCollection(companyId, jobId)
                .document(applicationId)
                .get()
                .await()
            if (snapshot.exists()) {
                val data = snapshot.data ?: return Result.success(null)
                Result.success(Application.fromFirestore(snapshot.id, companyId, jobId, data))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get all applications for a job
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

    // READ - Get all applications for a company
    suspend fun getAllApplicationsForCompany(companyId: String): Result<List<Application>> {
        return try {
            val allApplications = mutableListOf<Application>()
            val jobsSnapshot = db.collection("Company")
                .document(companyId)
                .collection("Jobs")
                .get()
                .await()

            for (jobDoc in jobsSnapshot.documents) {
                val applicationsSnapshot = jobDoc.reference.collection("application").get().await()
                val applications = applicationsSnapshot.documents.mapNotNull { appDoc ->
                    appDoc.data?.let { Application.fromFirestore(appDoc.id, companyId, jobDoc.id, it) }
                }
                allApplications.addAll(applications)
            }
            Result.success(allApplications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get applications by status
    suspend fun getApplicationsByStatus(
        companyId: String,
        jobId: String,
        status: String
    ): Result<List<Application>> {
        return try {
            val snapshot = getApplicationsCollection(companyId, jobId)
                .whereEqualTo("Status", status)
                .get()
                .await()
            val applications = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Application.fromFirestore(doc.id, companyId, jobId, it) }
            }
            Result.success(applications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get applications by email
    suspend fun getApplicationsByEmail(
        companyId: String,
        jobId: String,
        email: String
    ): Result<List<Application>> {
        return try {
            val snapshot = getApplicationsCollection(companyId, jobId)
                .whereEqualTo("Email", email)
                .get()
                .await()
            val applications = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Application.fromFirestore(doc.id, companyId, jobId, it) }
            }
            Result.success(applications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE - Update an existing application
    suspend fun updateApplication(
        companyId: String,
        jobId: String,
        applicationId: String,
        application: Application
    ): Result<Unit> {
        return try {
            getApplicationsCollection(companyId, jobId)
                .document(applicationId)
                .update(application.toHashMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE - Update application status
    suspend fun updateApplicationStatus(
        companyId: String,
        jobId: String,
        applicationId: String,
        status: String
    ): Result<Unit> {
        return try {
            getApplicationsCollection(companyId, jobId)
                .document(applicationId)
                .update("Status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE - Update specific fields
    suspend fun updateApplicationFields(
        companyId: String,
        jobId: String,
        applicationId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        return try {
            getApplicationsCollection(companyId, jobId)
                .document(applicationId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE - Delete an application
    suspend fun deleteApplication(companyId: String, jobId: String, applicationId: String): Result<Unit> {
        return try {
            getApplicationsCollection(companyId, jobId)
                .document(applicationId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE - Delete all applications for a job
    suspend fun deleteAllApplicationsForJob(companyId: String, jobId: String): Result<Unit> {
        return try {
            val snapshot = getApplicationsCollection(companyId, jobId).get().await()
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
