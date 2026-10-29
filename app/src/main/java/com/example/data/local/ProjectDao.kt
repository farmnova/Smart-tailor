package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SewingProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM sewing_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<SewingProject>>

    @Query("SELECT * FROM sewing_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): SewingProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: SewingProject)

    @Update
    suspend fun updateProject(project: SewingProject)

    @Delete
    suspend fun deleteProject(project: SewingProject)
}
