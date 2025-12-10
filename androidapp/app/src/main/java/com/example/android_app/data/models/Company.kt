package com.example.android_app.data.models

data class Company(
    val id: String = "",
    val email: String = "",
    val industry: String = "",
    val linkedin: String = "",
    val name: String = "",
    val phone: String = "",
    val website: String = ""
) {
    // Convert to HashMap for Firestore
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "Email" to email,
            "Industry" to industry,
            "Linkedin" to linkedin,
            "Name" to name,
            "Phone" to phone,
            "Website" to website
        )
    }

    companion object {
        // Create from Firestore document
        fun fromFirestore(id: String, data: Map<String, Any>): Company {
            return Company(
                id = id,
                email = data["Email"] as? String ?: "",
                industry = data["Industry"] as? String ?: "",
                linkedin = data["Linkedin"] as? String ?: "",
                name = data["Name"] as? String ?: "",
                phone = data["Phone"] as? String ?: "",
                website = data["Website"] as? String ?: ""
            )
        }
    }
}
