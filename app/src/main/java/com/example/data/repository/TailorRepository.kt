package com.example.data.repository

import com.example.data.local.TailorDatabase
import com.example.data.model.FabricItem
import com.example.data.model.GarmentDesign
import com.example.data.model.MeasurementProfile
import com.example.data.model.ProjectStatus
import com.example.data.model.SewingProject
import kotlinx.coroutines.flow.Flow

class TailorRepository(private val database: TailorDatabase) {

    // Profiles
    val allProfiles: Flow<List<MeasurementProfile>> =
        database.measurementDao().getAllProfiles()

    suspend fun getProfile(id: String): MeasurementProfile? =
        database.measurementDao().getProfileById(id)

    suspend fun saveProfile(profile: MeasurementProfile) =
        database.measurementDao().insertProfile(profile)

    suspend fun deleteProfile(id: String) =
        database.measurementDao().deleteById(id)

    // Designs
    val allDesigns: Flow<List<GarmentDesign>> =
        database.designDao().getAllDesigns()

    val approvedDesigns: Flow<List<GarmentDesign>> =
        database.designDao().getApprovedDesigns()

    suspend fun getDesign(id: String): GarmentDesign? =
        database.designDao().getDesignById(id)

    suspend fun saveDesign(design: GarmentDesign) =
        database.designDao().insertDesign(design)

    suspend fun setDesignApproval(designId: String, isApproved: Boolean, notes: String = "") {
        val existing = database.designDao().getDesignById(designId) ?: return
        val updated = existing.copy(
            isApproved = isApproved,
            approvedAt = if (isApproved) System.currentTimeMillis() else null,
            revisionNotes = notes
        )
        database.designDao().updateDesign(updated)
    }

    // Fabrics
    val allFabrics: Flow<List<FabricItem>> =
        database.fabricDao().getAllFabrics()

    suspend fun getFabric(id: String): FabricItem? =
        database.fabricDao().getFabricById(id)

    suspend fun saveFabric(fabric: FabricItem) =
        database.fabricDao().insertFabric(fabric)

    // Projects
    val allProjects: Flow<List<SewingProject>> =
        database.projectDao().getAllProjects()

    suspend fun getProject(id: String): SewingProject? =
        database.projectDao().getProjectById(id)

    suspend fun saveProject(project: SewingProject) =
        database.projectDao().insertProject(project)

    suspend fun updateProjectStatus(projectId: String, status: ProjectStatus) {
        val proj = database.projectDao().getProjectById(projectId) ?: return
        database.projectDao().updateProject(proj.copy(status = status, updatedAt = System.currentTimeMillis()))
    }
}
