package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class MeasurementUnit(val symbol: String) {
    CENTIMETERS("cm"),
    INCHES("in")
}

enum class CalibrationMethod {
    STANDARD_CARD,
    REFERENCE_RULER,
    MANUAL_ENTRY,
    SENSOR_DEPTH
}

@Entity(tableName = "measurement_profiles")
data class MeasurementProfile(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val clientName: String,
    val gender: String = "Unisex", // Male, Female, Unisex
    val unit: MeasurementUnit = MeasurementUnit.CENTIMETERS,
    
    // Core body dimensions (stored in cm as canonical format)
    val height: Double = 175.0,
    val shoulderWidth: Double = 44.0,
    val chestBust: Double = 98.0,
    val waist: Double = 82.0,
    val hips: Double = 96.0,
    val neck: Double = 39.0,
    val sleeveLength: Double = 62.0,
    val armCircumference: Double = 32.0,
    val backLength: Double = 43.0,
    val desiredGarmentLength: Double = 72.0,
    
    // Lower body / Trouser dimensions
    val trouserWaist: Double = 84.0,
    val inseam: Double = 78.0,
    val outseam: Double = 104.0,
    val thighCircumference: Double = 56.0,
    val legOpening: Double = 38.0,
    
    // Calibration & Validation metadata
    val calibrationMethod: CalibrationMethod = CalibrationMethod.MANUAL_ENTRY,
    val confidenceScore: Int = 95, // 0-100%
    val isConfirmedByUser: Boolean = true,
    val calibrationReferenceDimensionCm: Double = 8.56,
    val frontPhotoPath: String? = null,
    val sidePhotoPath: String? = null,
    val userNotes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

enum class GarmentCategory(val displayName: String, val swahiliName: String) {
    TRADITIONAL_AFRICAN("Traditional African", "Mavazi ya Kitamaduni"),
    SHIRT("Shirt / Top", "Shati"),
    DRESS("Dress / Gown", "Gauni"),
    TROUSERS("Trousers / Pants", "Suruali"),
    SUIT("Suit / Blazer", "Suti"),
    SKIRT("Skirt", "Sketi"),
    UNIFORM("Uniform / Workwear", "Sare")
}

@Entity(tableName = "garment_designs")
data class GarmentDesign(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: GarmentCategory = GarmentCategory.TRADITIONAL_AFRICAN,
    val silhouette: String = "Tailored Fit",
    val collarStyle: String = "Mandarin Collar",
    val sleeveStyle: String = "Long Sleeve with Cuff",
    val neckline: String = "Asymmetrical V-Neck",
    val pockets: String = "Welt Chest Pocket",
    val closures: String = "Concealed Front Buttons",
    val embellishments: String = "Gold Filigree Embroidery & Kitenge Border Trim",
    val primaryColorHex: String = "#1A1B35",
    val accentColorHex: String = "#D4AF37",
    val naturalLanguagePrompt: String = "Modern Tanzanian Kitenge gala jacket with embroidery",
    val aiGeneratedImageUrl: String? = null,
    val isApproved: Boolean = false,
    val approvedAt: Long? = null,
    val revisionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "fabric_items")
data class FabricItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val fabricType: String = "Wax Print (Kitenge)",
    val widthInches: Double = 44.0, // 44" or 60"
    val weightGsm: Double = 180.0,
    val stretchPercentage: Double = 0.0,
    val thickness: String = "Medium",
    val texture: String = "Crisp Cotton Weave",
    val primaryColorHex: String = "#1E3A8A",
    val quantityAvailableMeters: Double = 6.0,
    val pricePerMeterTzs: Double = 12000.0,
    val photoUri: String? = null,
    val geminiDescription: String = "Vibrant high-density Dutch/African wax cotton with geometric Tanzanian motif."
)

enum class ProjectStatus(val label: String, val swahiliLabel: String) {
    DRAFT("Draft", "Rasimu"),
    MEASURED("Measurements Verified", "Vipimo Vimethibitishwa"),
    DESIGN_APPROVED("Design Approved", "Muundo Umeidhinishwa"),
    PATTERN_READY("Pattern Drafted", "Mifumo Imekamilika"),
    IN_SEWING("In Sewing", "Inashonwa"),
    COMPLETED("Completed & Fitted", "Imekamilika")
}

@Entity(tableName = "sewing_projects")
data class SewingProject(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectTitle: String,
    val clientName: String,
    val profileId: String,
    val designId: String,
    val fabricId: String,
    val status: ProjectStatus = ProjectStatus.DRAFT,
    val fabricMetersRequired: Double = 2.5,
    val fabricCostTzs: Double = 30000.0,
    val notionsCostTzs: Double = 12000.0,
    val embroideryCostTzs: Double = 25000.0,
    val laborCostTzs: Double = 45000.0,
    val totalCostTzs: Double = 112000.0,
    val sellingPriceTzs: Double = 160000.0,
    val tailorNotes: String = "Customer requested reinforced collar and double-stitched armholes.",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class PatternPiece(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val swahiliName: String,
    val widthCm: Double,
    val heightCm: Double,
    val seamAllowanceCm: Double = 1.5,
    val cuttingQuantity: Int = 2,
    val cutOnFold: Boolean = false,
    val grainlineAngleDegrees: Float = 0f,
    val matchingNotches: List<String> = emptyList(),
    val description: String = ""
)

data class SewingStep(
    val stepNumber: Int,
    val title: String,
    val swahiliTitle: String,
    val instruction: String,
    val swahiliInstruction: String,
    val stitchType: String = "Straight Stitch (2.5mm)",
    val seamAllowance: String = "1.5 cm (5/8 in)",
    val checkpoint: String = "Check bust dart alignment before pressing.",
    val isComplete: Boolean = false
)
