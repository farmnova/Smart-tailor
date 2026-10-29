package com.example.engine

import com.example.data.model.GarmentCategory
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

data class CostBreakdown(
    val requiredMeters: Double,
    val fabricCostTzs: Double,
    val notionsCostTzs: Double,
    val embroideryCostTzs: Double,
    val laborCostTzs: Double,
    val totalProductionCostTzs: Double,
    val suggestedSellingPriceTzs: Double,
    val marginPercentage: Int = 30
) {
    fun formattedTotal(): String = formatTzs(totalProductionCostTzs)
    fun formattedSellingPrice(): String = formatTzs(suggestedSellingPriceTzs)
    fun formattedFabric(): String = formatTzs(fabricCostTzs)
    fun formattedNotions(): String = formatTzs(notionsCostTzs)
    fun formattedEmbroidery(): String = formatTzs(embroideryCostTzs)
    fun formattedLabor(): String = formatTzs(laborCostTzs)

    companion object {
        fun formatTzs(amount: Double): String {
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            return "${formatter.format(amount.roundToInt())} TZS"
        }
    }
}

object FabricCostCalculator {
    fun calculateCost(
        category: GarmentCategory,
        fabricWidthInches: Double = 44.0,
        pricePerMeterTzs: Double = 15000.0,
        hasEmbroidery: Boolean = true,
        tailorLaborTzs: Double = 40000.0,
        markupPercent: Int = 35
    ): CostBreakdown {
        // Required fabric meterage varies with fabric width
        val baseMeters = when (category) {
            GarmentCategory.SHIRT -> if (fabricWidthInches >= 58) 1.6 else 2.2
            GarmentCategory.DRESS -> if (fabricWidthInches >= 58) 2.5 else 3.5
            GarmentCategory.TROUSERS -> if (fabricWidthInches >= 58) 1.4 else 1.8
            GarmentCategory.TRADITIONAL_AFRICAN -> if (fabricWidthInches >= 58) 2.8 else 4.0
            GarmentCategory.SUIT -> if (fabricWidthInches >= 58) 3.0 else 4.2
            GarmentCategory.SKIRT -> if (fabricWidthInches >= 58) 1.2 else 1.8
            GarmentCategory.UNIFORM -> if (fabricWidthInches >= 58) 2.0 else 2.6
        }

        val roundedMeters = ((baseMeters * 10).roundToInt()) / 10.0
        val fabricCost = roundedMeters * pricePerMeterTzs

        val notionsCost = when (category) {
            GarmentCategory.SUIT -> 22000.0 // Canvas, horn buttons, lining, shoulder pads
            GarmentCategory.DRESS -> 14000.0 // Concealed zipper, interfacing, bias tape
            GarmentCategory.TROUSERS -> 9000.0 // Pant zipper, waistband canvas, pocketing, hook
            GarmentCategory.SHIRT -> 7000.0 // Collar stay, placket interfacing, pearl buttons
            GarmentCategory.TRADITIONAL_AFRICAN -> 12000.0 // Neck placket interfacing, snap buttons
            else -> 8000.0
        }

        val embroideryCost = if (hasEmbroidery) {
            when (category) {
                GarmentCategory.TRADITIONAL_AFRICAN -> 30000.0
                GarmentCategory.DRESS -> 25000.0
                GarmentCategory.SUIT -> 20000.0
                GarmentCategory.SHIRT -> 15000.0
                else -> 10000.0
            }
        } else 0.0

        val totalCost = fabricCost + notionsCost + embroideryCost + tailorLaborTzs
        val multiplier = 1.0 + (markupPercent / 100.0)
        val sellingPrice = totalCost * multiplier

        return CostBreakdown(
            requiredMeters = roundedMeters,
            fabricCostTzs = fabricCost,
            notionsCostTzs = notionsCost,
            embroideryCostTzs = embroideryCost,
            laborCostTzs = tailorLaborTzs,
            totalProductionCostTzs = totalCost,
            suggestedSellingPriceTzs = sellingPrice,
            marginPercentage = markupPercent
        )
    }
}
