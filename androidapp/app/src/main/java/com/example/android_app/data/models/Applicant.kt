package com.example.android_app.data.models

data class Applicant(
    val id: String = "",
    val cvLink: String = "",
    val email: String = "",
    val linkedin: String = "",
    val password: String = "",
    val phone: String = ""
) {
    // Convert to HashMap for Firestore
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "CV-Link" to cvLink,
            "Email" to email,
            "Linkedin" to linkedin,
            "Password" to password,
            "Phone" to phone
        )
    }

    companion object {
        // Create from Firestore document
        fun fromFirestore(id: String, data: Map<String, Any>): Applicant {
            return Applicant(
                id = id,
                cvLink = data["CV-Link"] as? String ?: "",
                email = data["Email"] as? String ?: "",
                linkedin = data["Linkedin"] as? String ?: "",
                password = data["Password"] as? String ?: "",
                phone = data["Phone"] as? String ?: ""
            )
        }
    }
}
