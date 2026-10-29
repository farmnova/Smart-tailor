package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CalibrationMethod
import com.example.data.model.GarmentCategory
import com.example.data.model.MeasurementUnit
import com.example.data.model.ProjectStatus

class Converters {
    @TypeConverter
    fun fromMeasurementUnit(value: MeasurementUnit): String = value.name

    @TypeConverter
    fun toMeasurementUnit(value: String): MeasurementUnit = try {
        MeasurementUnit.valueOf(value)
    } catch (_: Exception) {
        MeasurementUnit.CENTIMETERS
    }

    @TypeConverter
    fun fromCalibrationMethod(value: CalibrationMethod): String = value.name

    @TypeConverter
    fun toCalibrationMethod(value: String): CalibrationMethod = try {
        CalibrationMethod.valueOf(value)
    } catch (_: Exception) {
        CalibrationMethod.MANUAL_ENTRY
    }

    @TypeConverter
    fun fromGarmentCategory(value: GarmentCategory): String = value.name

    @TypeConverter
    fun toGarmentCategory(value: String): GarmentCategory = try {
        GarmentCategory.valueOf(value)
    } catch (_: Exception) {
        GarmentCategory.TRADITIONAL_AFRICAN
    }

    @TypeConverter
    fun fromProjectStatus(value: ProjectStatus): String = value.name

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus = try {
        ProjectStatus.valueOf(value)
    } catch (_: Exception) {
        ProjectStatus.DRAFT
    }
}
