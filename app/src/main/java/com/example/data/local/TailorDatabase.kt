package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.FabricItem
import com.example.data.model.GarmentCategory
import com.example.data.model.GarmentDesign
import com.example.data.model.MeasurementProfile
import com.example.data.model.ProjectStatus
import com.example.data.model.SewingProject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MeasurementProfile::class,
        GarmentDesign::class,
        FabricItem::class,
        SewingProject::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TailorDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao
    abstract fun designDao(): DesignDao
    abstract fun fabricDao(): FabricDao
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: TailorDatabase? = null

        fun getDatabase(context: Context): TailorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TailorDatabase::class.java,
                    "smart_tailor_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getDatabase(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(db: TailorDatabase) {
            val sampleProfile = MeasurementProfile(
                id = "prof_default_01",
                clientName = "Neema Mwangi",
                gender = "Female",
                height = 168.0,
                shoulderWidth = 40.0,
                chestBust = 92.0,
                waist = 74.0,
                hips = 98.0,
                neck = 36.0,
                sleeveLength = 58.0,
                armCircumference = 30.0,
                backLength = 41.0,
                desiredGarmentLength = 110.0,
                trouserWaist = 76.0,
                inseam = 74.0,
                outseam = 100.0,
                thighCircumference = 54.0,
                legOpening = 36.0,
                confidenceScore = 98,
                isConfirmedByUser = true
            )
            db.measurementDao().insertProfile(sampleProfile)

            val sampleDesign = GarmentDesign(
                id = "des_default_01",
                title = "Kitenge Royal Gala Dress",
                category = GarmentCategory.DRESS,
                silhouette = "Fit and Flare / Peplum",
                collarStyle = "Queen Anne / Sweetheart",
                sleeveStyle = "Three-Quarter Bishop Sleeve",
                neckline = "V-Neck with Gold Piping",
                pockets = "In-seam Side Pockets",
                closures = "Concealed Center-Back YKK Zipper",
                embellishments = "Handmade Gold Zari Embroidery on Bodice & Hem",
                primaryColorHex = "#1D2A44",
                accentColorHex = "#E0A926",
                naturalLanguagePrompt = "Modern Tanzanian Kitenge gala evening gown with sweetheart neckline and gold embroidery",
                isApproved = true,
                approvedAt = System.currentTimeMillis()
            )
            db.designDao().insertDesign(sampleDesign)

            val sampleFabrics = listOf(
                FabricItem(
                    id = "fab_01",
                    name = "Tanzanian Java Wax Kitenge",
                    fabricType = "African Wax Print (100% Cotton)",
                    widthInches = 44.0,
                    weightGsm = 185.0,
                    stretchPercentage = 0.0,
                    thickness = "Medium",
                    texture = "Crisp, Smooth, Vibrant Luster",
                    primaryColorHex = "#0E7490",
                    quantityAvailableMeters = 6.0,
                    pricePerMeterTzs = 12000.0,
                    geminiDescription = "Premium high-grade 100% African cotton wax print with colorfast reactive dyes and traditional diamond patterns."
                ),
                FabricItem(
                    id = "fab_02",
                    name = "Royal Indigo Silk Mikado",
                    fabricType = "Heavyweight Silk Mikado",
                    widthInches = 60.0,
                    weightGsm = 240.0,
                    stretchPercentage = 2.0,
                    thickness = "Heavy structured",
                    texture = "Subtle twill rib with luminous sheen",
                    primaryColorHex = "#1E1B4B",
                    quantityAvailableMeters = 4.5,
                    pricePerMeterTzs = 35000.0,
                    geminiDescription = "Couture structural silk blend perfect for architectural silhouettes and gala wear."
                ),
                FabricItem(
                    id = "fab_03",
                    name = "Kilimanjaro Pure Organic Linen",
                    fabricType = "100% Slub Linen",
                    widthInches = 54.0,
                    weightGsm = 190.0,
                    stretchPercentage = 0.0,
                    thickness = "Medium breathable",
                    texture = "Natural slubbed textured weave",
                    primaryColorHex = "#E2D7C3",
                    quantityAvailableMeters = 8.0,
                    pricePerMeterTzs = 18000.0,
                    geminiDescription = "Natural earth-tone breathable linen suitable for warm climate tailoring, suits, and tunics."
                )
            )
            sampleFabrics.forEach { db.fabricDao().insertFabric(it) }

            val sampleProject = SewingProject(
                id = "proj_01",
                projectTitle = "Neema's Wedding Reception Dress",
                clientName = "Neema Mwangi",
                profileId = sampleProfile.id,
                designId = sampleDesign.id,
                fabricId = "fab_01",
                status = ProjectStatus.PATTERN_READY,
                fabricMetersRequired = 3.2,
                fabricCostTzs = 38400.0,
                notionsCostTzs = 15000.0,
                embroideryCostTzs = 30000.0,
                laborCostTzs = 50000.0,
                totalCostTzs = 133400.0,
                sellingPriceTzs = 190000.0,
                tailorNotes = "High-priority order. Fitting session scheduled next Tuesday."
            )
            db.projectDao().insertProject(sampleProject)
        }
    }
}
