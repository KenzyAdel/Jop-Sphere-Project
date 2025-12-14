package com.example.android_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_jobs_table")
data class SavedJobsEntity(
    @PrimaryKey
    val id: String, // Matches the Firebase Document ID
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val jobType: String
)