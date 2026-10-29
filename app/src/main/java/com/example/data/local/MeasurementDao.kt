package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MeasurementProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measurement_profiles ORDER BY updatedAt DESC")
    fun getAllProfiles(): Flow<List<MeasurementProfile>>

    @Query("SELECT * FROM measurement_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): MeasurementProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: MeasurementProfile)

    @Update
    suspend fun updateProfile(profile: MeasurementProfile)

    @Delete
    suspend fun deleteProfile(profile: MeasurementProfile)

    @Query("DELETE FROM measurement_profiles WHERE id = :id")
    suspend fun deleteById(id: String)
}
