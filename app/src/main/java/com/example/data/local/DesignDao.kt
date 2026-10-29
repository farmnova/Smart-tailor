package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GarmentDesign
import kotlinx.coroutines.flow.Flow

@Dao
interface DesignDao {
    @Query("SELECT * FROM garment_designs ORDER BY createdAt DESC")
    fun getAllDesigns(): Flow<List<GarmentDesign>>

    @Query("SELECT * FROM garment_designs WHERE id = :id LIMIT 1")
    suspend fun getDesignById(id: String): GarmentDesign?

    @Query("SELECT * FROM garment_designs WHERE isApproved = 1 ORDER BY approvedAt DESC")
    fun getApprovedDesigns(): Flow<List<GarmentDesign>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesign(design: GarmentDesign)

    @Update
    suspend fun updateDesign(design: GarmentDesign)

    @Delete
    suspend fun deleteDesign(design: GarmentDesign)
}
