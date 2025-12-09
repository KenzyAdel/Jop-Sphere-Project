package com.example.android_app.data.services

import com.example.android_app.data.models.Company
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CompanyService {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("Company")

    // CREATE - Add a new company
    suspend fun createCompany(company: Company): Result<String> {
        return try {
            val docRef = collection.add(company.toHashMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get company by ID
    suspend fun getCompany(id: String): Result<Company?> {
        return try {
            val snapshot = collection.document(id).get().await()
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

    // READ - Get all companies
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

    // READ - Get company by email
    suspend fun getCompanyByEmail(email: String): Result<Company?> {
        return try {
            val snapshot = collection.whereEqualTo("Email", email).get().await()
            if (snapshot.documents.isNotEmpty()) {
                val doc = snapshot.documents.first()
                val data = doc.data ?: return Result.success(null)
                Result.success(Company.fromFirestore(doc.id, data))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Get companies by industry
    suspend fun getCompaniesByIndustry(industry: String): Result<List<Company>> {
        return try {
            val snapshot = collection.whereEqualTo("Industry", industry).get().await()
            val companies = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Company.fromFirestore(doc.id, it) }
            }
            Result.success(companies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE - Update an existing company
    suspend fun updateCompany(id: String, company: Company): Result<Unit> {
        return try {
            collection.document(id).update(company.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE - Update specific fields
    suspend fun updateCompanyFields(id: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            collection.document(id).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE - Delete a company
    suspend fun deleteCompany(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // SEARCH - Search companies by name
    suspend fun searchCompaniesByName(name: String): Result<List<Company>> {
        return try {
            val snapshot = collection.whereGreaterThanOrEqualTo("Name", name)
                .whereLessThanOrEqualTo("Name", name + '\uf8ff')
                .get()
                .await()
            val companies = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Company.fromFirestore(doc.id, it) }
            }
            Result.success(companies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
