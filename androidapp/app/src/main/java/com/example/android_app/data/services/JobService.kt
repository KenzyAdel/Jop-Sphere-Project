package com.example.android_app.data.services

import com.example.android_app.data.models.Job
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class JobService {
    private val db = FirebaseFirestore.getInstance()

    // Helper function to get Jobs collection reference for a company
    private fun getJobsCollection(companyId: String) =
        db.collection("Company").document(companyId).collection("Jobs")

    // CREATE - Add a new job
    suspend fun createJob(companyId: String, job: Job): Result<String> {
        return try {
            val docRef = getJobsCollection(companyId).add(job.toHashMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CREATE with custom ID
    suspend fun createJobWithId(companyId: String, jobId: String, job: Job): Result<Unit> {
        return try {
            getJobsCollection(companyId).document(jobId).set(job.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get job by ID
    suspend fun getJob(companyId: String, jobId: String): Result<Job?> {
        return try {
            val snapshot = getJobsCollection(companyId).document(jobId).get().await()
            if (snapshot.exists()) {
                val data = snapshot.data ?: return Result.success(null)
                Result.success(Job.fromFirestore(snapshot.id, companyId, data))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get all jobs for a company
    suspend fun getAllJobsForCompany(companyId: String): Result<List<Job>> {
        return try {
            val snapshot = getJobsCollection(companyId).get().await()
            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Job.fromFirestore(doc.id, companyId, it) }
            }
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get all jobs across all companies
    suspend fun getAllJobs(): Result<List<Job>> {
        return try {
            val allJobs = mutableListOf<Job>()
            val companiesSnapshot = db.collection("Company").get().await()
            
            for (companyDoc in companiesSnapshot.documents) {
                val jobsSnapshot = companyDoc.reference.collection("Jobs").get().await()
                val jobs = jobsSnapshot.documents.mapNotNull { jobDoc ->
                    jobDoc.data?.let { Job.fromFirestore(jobDoc.id, companyDoc.id, it) }
                }
                allJobs.addAll(jobs)
            }
            Result.success(allJobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get jobs by type
    suspend fun getJobsByType(companyId: String, jobType: String): Result<List<Job>> {
        return try {
            val snapshot = getJobsCollection(companyId)
                .whereEqualTo("JobType", jobType)
                .get()
                .await()
            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Job.fromFirestore(doc.id, companyId, it) }
            }
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get jobs by location
    suspend fun getJobsByLocation(companyId: String, location: String): Result<List<Job>> {
        return try {
            val snapshot = getJobsCollection(companyId)
                .whereEqualTo("Location", location)
                .get()
                .await()
            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Job.fromFirestore(doc.id, companyId, it) }
            }
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Search jobs by title
    suspend fun searchJobsByTitle(companyId: String, title: String): Result<List<Job>> {
        return try {
            val snapshot = getJobsCollection(companyId)
                .whereGreaterThanOrEqualTo("Title", title)
                .whereLessThanOrEqualTo("Title", title + '\uf8ff')
                .get()
                .await()
            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Job.fromFirestore(doc.id, companyId, it) }
            }
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE - Update an existing job
    suspend fun updateJob(companyId: String, jobId: String, job: Job): Result<Unit> {
        return try {
            getJobsCollection(companyId).document(jobId).update(job.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE - Update specific fields
    suspend fun updateJobFields(companyId: String, jobId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            getJobsCollection(companyId).document(jobId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE - Delete a job
    suspend fun deleteJob(companyId: String, jobId: String): Result<Unit> {
        return try {
            getJobsCollection(companyId).document(jobId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE - Delete all jobs for a company
    suspend fun deleteAllJobsForCompany(companyId: String): Result<Unit> {
        return try {
            val snapshot = getJobsCollection(companyId).get().await()
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
