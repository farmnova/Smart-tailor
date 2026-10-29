package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FabricItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FabricDao {
    @Query("SELECT * FROM fabric_items")
    fun getAllFabrics(): Flow<List<FabricItem>>

    @Query("SELECT * FROM fabric_items WHERE id = :id LIMIT 1")
    suspend fun getFabricById(id: String): FabricItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFabric(fabric: FabricItem)

    @Update
    suspend fun updateFabric(fabric: FabricItem)

    @Delete
    suspend fun deleteFabric(fabric: FabricItem)
}
