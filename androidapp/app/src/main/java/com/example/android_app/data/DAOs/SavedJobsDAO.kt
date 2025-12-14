package com.example.android_app.data.DAOs

import androidx.room.*
import com.example.android_app.data.entities.SavedJobsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedJobDao {
    @Query("SELECT * FROM saved_jobs_table")
    fun getAllSavedJobs(): Flow<List<SavedJobsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: SavedJobsEntity)

    @Delete
    suspend fun deleteJob(job: SavedJobsEntity)

    @Query("DELETE FROM saved_jobs_table WHERE id = :jobId")
    suspend fun deleteJobById(jobId: String)
}