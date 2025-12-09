package com.example.android_app.data.services

import com.example.android_app.data.models.Applicant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicantService {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("Applicant")

    // CREATE - Add a new applicant
    suspend fun createApplicant(applicant: Applicant): Result<String> {
        return try {
            val docRef = collection.add(applicant.toHashMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CREATE with custom ID
    suspend fun createApplicantWithId(id: String, applicant: Applicant): Result<Unit> {
        return try {
            collection.document(id).set(applicant.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get applicant by ID
    suspend fun getApplicant(id: String): Result<Applicant?> {
        return try {
            val snapshot = collection.document(id).get().await()
            if (snapshot.exists()) {
                val data = snapshot.data ?: return Result.success(null)
                Result.success(Applicant.fromFirestore(snapshot.id, data))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get all applicants
    suspend fun getAllApplicants(): Result<List<Applicant>> {
        return try {
            val snapshot = collection.get().await()
            val applicants = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Applicant.fromFirestore(doc.id, it) }
            }
            Result.success(applicants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get applicant by email
    suspend fun getApplicantByEmail(email: String): Result<Applicant?> {
        return try {
            val snapshot = collection.whereEqualTo("Email", email).get().await()
            if (snapshot.documents.isNotEmpty()) {
                val doc = snapshot.documents.first()
                val data = doc.data ?: return Result.success(null)
                Result.success(Applicant.fromFirestore(doc.id, data))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE - Update an existing applicant
    suspend fun updateApplicant(id: String, applicant: Applicant): Result<Unit> {
        return try {
            collection.document(id).update(applicant.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE - Update specific fields
    suspend fun updateApplicantFields(id: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            collection.document(id).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE - Delete an applicant
    suspend fun deleteApplicant(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // SEARCH - Search applicants by phone
    suspend fun searchApplicantsByPhone(phone: String): Result<List<Applicant>> {
        return try {
            val snapshot = collection.whereEqualTo("Phone", phone).get().await()
            val applicants = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Applicant.fromFirestore(doc.id, it) }
            }
            Result.success(applicants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
