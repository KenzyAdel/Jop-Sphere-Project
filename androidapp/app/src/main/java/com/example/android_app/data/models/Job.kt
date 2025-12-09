package com.example.android_app.data.models

data class Job(
    val id: String = "",
    val companyId: String = "",
    val description: String = "",
    val jobType: String = "",
    val location: String = "",
    val requirements: String = "",
    val responsibilities: String = "",
    val salary: String = "",
    val title: String = ""
) {
    // Convert to HashMap for Firestore
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "Description" to description,
            "JobType" to jobType,
            "Location" to location,
            "Requirements" to requirements,
            "Responsibilities" to responsibilities,
            "Salary" to salary,
            "Title" to title
        )
    }

    companion object {
        // Create from Firestore document
        fun fromFirestore(id: String, companyId: String, data: Map<String, Any>): Job {
            return Job(
                id = id,
                companyId = companyId,
                description = data["Description"] as? String ?: "",
                jobType = data["JobType"] as? String ?: "",
                location = data["Location"] as? String ?: "",
                requirements = data["Requirements"] as? String ?: "",
                responsibilities = data["Responsibilities"] as? String ?: "",
                salary = data["Salary"] as? String ?: "",
                title = data["Title"] as? String ?: ""
            )
        }
    }
}
