package com.example.android_app.data.services

import com.example.android_app.data.models.Company
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CompanyService {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("Company")

    suspend fun createCompany(company: Company): Result<Unit> {
        return try {
            collection.document(company.id).set(company.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getAllCompanies(): Result<List<Company>> {
        return try {
            val snapshot = collection.get().await()
            val companies = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Company.fromFirestore(doc.id, it) }
            }
            Result.success(companies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCompany(companyId: String): Result<Company?> {
        return try {
            val snapshot = collection.document(companyId).get().await()
            if (snapshot.exists()) {
                val data = snapshot.data ?: return Result.success(null)
                Result.success(Company.fromFirestore(snapshot.id, data))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}