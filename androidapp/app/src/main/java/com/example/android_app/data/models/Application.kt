package com.example.android_app.data.models

data class Application(
    val id: String = "",
    val jobId: String = "",
    val companyId: String = "",
    val cvLink: String = "",
    val email: String = "",
    val linkedIn: String = "",
    val name: String = "",
    val phone: String = "",
    val status: String = ""
) {
    // Convert to HashMap for Firestore
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "CV-Link" to cvLink,
            "Email" to email,
            "LinkedId" to linkedIn,
            "Name" to name,
            "Phone" to phone,
            "Status" to status
        )
    }

    companion object {
        // Create from Firestore document
        fun fromFirestore(
            id: String,
            companyId: String,
            jobId: String,
            data: Map<String, Any>
        ): Application {
            return Application(
                id = id,
                jobId = jobId,
                companyId = companyId,
                cvLink = data["CV-Link"] as? String ?: "",
                email = data["Email"] as? String ?: "",
                linkedIn = data["LinkedId"] as? String ?: "",
                name = data["Name"] as? String ?: "",
                phone = data["Phone"] as? String ?: "",
                status = data["Status"] as? String ?: ""
            )
        }
    }
}