package com.example

import com.example.data.model.GarmentCategory
import com.example.data.model.GarmentDesign
import com.example.data.model.MeasurementProfile
import com.example.engine.FabricCostCalculator
import com.example.engine.MeasurementCalibrationEngine
import com.example.engine.PatternDraftingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPatternDraftingEngine_Shirt() {
        val profile = MeasurementProfile(
            clientName = "Test Client",
            chestBust = 100.0,
            backLength = 44.0,
            desiredGarmentLength = 75.0,
            shoulderWidth = 46.0,
            neck = 40.0,
            sleeveLength = 64.0
        )
        val design = GarmentDesign(
            title = "Tailored Oxford Shirt",
            category = GarmentCategory.SHIRT
        )

        val result = PatternDraftingEngine.draftPattern(design, profile, 44.0)

        assertNotNull(result)
        assertTrue("Drafted pieces must not be empty", result.pieces.isNotEmpty())
        assertEquals(44.0, result.fabricWidthInches, 0.01)
        assertTrue("Total length required must be at least 1.5 meters", result.totalLengthRequiredMeters >= 1.5)

        val frontPiece = result.pieces.find { it.name.contains("Front Shirt") }
        assertNotNull(frontPiece)
        assertEquals("Front width should be chest/4 + 3.5cm ease", 28.5, frontPiece!!.widthCm, 0.01)
    }

    @Test
    fun testFabricCostCalculator_TraditionalKitenge() {
        val cost = FabricCostCalculator.calculateCost(
            category = GarmentCategory.TRADITIONAL_AFRICAN,
            fabricWidthInches = 44.0,
            pricePerMeterTzs = 12000.0,
            hasEmbroidery = true,
            tailorLaborTzs = 40000.0,
            markupPercent = 30
        )

        assertTrue(cost.requiredMeters >= 3.5)
        assertEquals(48000.0, cost.fabricCostTzs, 1000.0)
        assertTrue(cost.embroideryCostTzs > 0)
        assertTrue("Suggested selling price must exceed production cost", cost.suggestedSellingPriceTzs > cost.totalProductionCostTzs)
    }

    @Test
    fun testMeasurementCalibrationEngine() {
        // Standard card is 85.6 mm. If detected card pixel width is 100px, 1px = 0.856mm
        val check = MeasurementCalibrationEngine.validateCaptureConditions(
            detectedCardPixelWidth = 100f,
            estimatedBodyHeightPixels = 500f,
            isStraightPosture = true,
            hasContrastBackground = true
        )

        assertTrue(check.referenceFound)
        assertTrue(check.isBodyFullyVisible)
        assertTrue(check.confidencePercentage >= 80)

        val profile = MeasurementCalibrationEngine.computeCalibratedMeasurements(
            clientName = "Calibrated User",
            gender = "Female",
            detectedCardPixelWidth = 100f,
            headToToePixels = 1962f, // ~168 cm
            shoulderPixels = 467f,  // ~40 cm
            chestPixels = 373f,
            waistPixels = 310f,
            hipPixels = 390f
        )

        assertNotNull(profile)
        assertEquals(168.0, profile.height, 1.0)
        assertEquals(40.0, profile.shoulderWidth, 1.0)
    }
}
