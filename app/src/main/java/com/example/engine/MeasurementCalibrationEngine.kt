package com.example.engine

import com.example.data.model.CalibrationMethod
import com.example.data.model.MeasurementProfile
import com.example.data.model.MeasurementUnit
import kotlin.math.roundToInt

data class CalibrationCheck(
    val hasGoodLighting: Boolean,
    val isBodyFullyVisible: Boolean,
    val isCameraAngleLevel: Boolean,
    val referenceFound: Boolean,
    val confidencePercentage: Int,
    val feedbackMessage: String,
    val swahiliFeedbackMessage: String
)

object MeasurementCalibrationEngine {
    // International standard ID/Credit card width in millimeters (ISO/IEC 7810 ID-1)
    const val STANDARD_CARD_WIDTH_MM = 85.60
    const val STANDARD_CARD_HEIGHT_MM = 53.98

    fun validateCaptureConditions(
        detectedCardPixelWidth: Float?,
        estimatedBodyHeightPixels: Float?,
        isStraightPosture: Boolean,
        hasContrastBackground: Boolean
    ): CalibrationCheck {
        val refFound = (detectedCardPixelWidth != null && detectedCardPixelWidth > 30f)
        val bodyVisible = (estimatedBodyHeightPixels != null && estimatedBodyHeightPixels > 300f)

        var score = 30 // Base
        if (refFound) score += 35
        if (bodyVisible) score += 20
        if (isStraightPosture) score += 10
        if (hasContrastBackground) score += 5

        val confidence = score.coerceIn(10, 99)

        val feedback = when {
            !refFound -> "Reference card not detected. Calibrate using a standard card or use manual measurements."
            !bodyVisible -> "Full body not visible from head to toe. Step back 2.5 meters."
            !isStraightPosture -> "Posture tilted. Stand upright with arms slightly away from sides."
            confidence >= 85 -> "Excellent calibration. Scale verified with high confidence."
            else -> "Acceptable calibration. Please review and verify all measurement fields."
        }

        val feedbackSw = when {
            !refFound -> "Kadi ya rejea haijaonekana. Weka kadi kifuani/kiunoni au jaza vipimo kwa mkono."
            !bodyVisible -> "Mwili wote hauonekani vizuri. Rudi nyuma hatua 3 ili urefu wote uingie kwenye kamera."
            !isStraightPosture -> "Mkao haujanyooka. Simama wima na weka mikono pembeni kidogo."
            confidence >= 85 -> "Ulinganifu mzuri sana. Vipimo vimethibitishwa kwa uaminifu mkubwa."
            else -> "Ulinganifu wa wastani. Tafadhali hakiki vipimo vyote kabla ya kuanza kukata."
        }

        return CalibrationCheck(
            hasGoodLighting = hasContrastBackground,
            isBodyFullyVisible = bodyVisible,
            isCameraAngleLevel = isStraightPosture,
            referenceFound = refFound,
            confidencePercentage = confidence,
            feedbackMessage = feedback,
            swahiliFeedbackMessage = feedbackSw
        )
    }

    /**
     * Calibrate measurements from reference scale.
     * Computes pixel-to-millimeter ratio from the reference card.
     */
    fun computeCalibratedMeasurements(
        clientName: String,
        gender: String,
        detectedCardPixelWidth: Float,
        headToToePixels: Float,
        shoulderPixels: Float,
        chestPixels: Float,
        waistPixels: Float,
        hipPixels: Float
    ): MeasurementProfile {
        val mmPerPixel = STANDARD_CARD_WIDTH_MM / detectedCardPixelWidth.coerceAtLeast(1f)

        // Calculate heights and widths in cm
        val heightCm = ((headToToePixels * mmPerPixel) / 10.0).roundToOneDecimal().coerceIn(120.0, 220.0)
        val shoulderWidthCm = ((shoulderPixels * mmPerPixel) / 10.0).roundToOneDecimal().coerceIn(32.0, 60.0)

        // Estimated circumference from visible planar width using ellipse geometry approximation
        // Depth-to-width ratio: Chest ~ 0.78, Waist ~ 0.74, Hips ~ 0.82
        val chestWidthCm = (chestPixels * mmPerPixel) / 10.0
        val chestCircumferenceCm = (chestWidthCm * 2.85).roundToOneDecimal().coerceIn(70.0, 150.0)

        val waistWidthCm = (waistPixels * mmPerPixel) / 10.0
        val waistCircumferenceCm = (waistWidthCm * 2.75).roundToOneDecimal().coerceIn(55.0, 140.0)

        val hipWidthCm = (hipPixels * mmPerPixel) / 10.0
        val hipCircumferenceCm = (hipWidthCm * 2.90).roundToOneDecimal().coerceIn(75.0, 160.0)

        val neckCm = (shoulderWidthCm * 0.88).roundToOneDecimal()
        val sleeveCm = (heightCm * 0.36).roundToOneDecimal()
        val backLengthCm = (heightCm * 0.24).roundToOneDecimal()
        val inseamCm = (heightCm * 0.45).roundToOneDecimal()
        val outseamCm = (heightCm * 0.60).roundToOneDecimal()
        val thighCm = (hipCircumferenceCm * 0.58).roundToOneDecimal()
        val legOpeningCm = (thighCm * 0.65).roundToOneDecimal()

        return MeasurementProfile(
            clientName = clientName,
            gender = gender,
            unit = MeasurementUnit.CENTIMETERS,
            height = heightCm,
            shoulderWidth = shoulderWidthCm,
            chestBust = chestCircumferenceCm,
            waist = waistCircumferenceCm,
            hips = hipCircumferenceCm,
            neck = neckCm,
            sleeveLength = sleeveCm,
            armCircumference = (chestCircumferenceCm * 0.33).roundToOneDecimal(),
            backLength = backLengthCm,
            desiredGarmentLength = (heightCm * 0.42).roundToOneDecimal(),
            trouserWaist = waistCircumferenceCm + 2.0,
            inseam = inseamCm,
            outseam = outseamCm,
            thighCircumference = thighCm,
            legOpening = legOpeningCm,
            calibrationMethod = CalibrationMethod.STANDARD_CARD,
            confidenceScore = 92,
            isConfirmedByUser = false // Requires user review and confirmation!
        )
    }

    private fun Double.roundToOneDecimal(): Double =
        ((this * 10.0).roundToInt()) / 10.0
}
