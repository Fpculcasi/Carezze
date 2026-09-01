package com.fpculcasi.carezze.domain.model

import java.time.Instant

sealed class ActivityLog {
    abstract val id: String
    abstract val personId: String
    abstract val timestamp: Instant
    abstract val loggedBy: String

    data class Meal(
        override val id: String,
        override val personId: String,
        override val timestamp: Instant,
        override val loggedBy: String,
        val amount: Double?,
        val amountUnit: MealUnit?,
        val mealType: MealType?,
        val notes: String?,
    ) : ActivityLog()

    data class Diaper(
        override val id: String,
        override val personId: String,
        override val timestamp: Instant,
        override val loggedBy: String,
        val diaperType: DiaperType,
        val notes: String?,
    ) : ActivityLog()

    data class SleepStart(
        override val id: String,
        override val personId: String,
        override val timestamp: Instant,
        override val loggedBy: String,
    ) : ActivityLog()

    data class SleepEnd(
        override val id: String,
        override val personId: String,
        override val timestamp: Instant,
        override val loggedBy: String,
    ) : ActivityLog()

    data class Temperature(
        override val id: String,
        override val personId: String,
        override val timestamp: Instant,
        override val loggedBy: String,
        val temperature: Double,
        val unit: TemperatureUnit,
        val method: MeasurementMethod?,
        val notes: String?,
    ) : ActivityLog()

    data class Weight(
        override val id: String,
        override val personId: String,
        override val timestamp: Instant,
        override val loggedBy: String,
        val weight: Double,
        val weightUnit: WeightUnit,
        val height: Double?,
        val heightUnit: HeightUnit?,
        val notes: String?,
    ) : ActivityLog()

    data class Hygiene(
        override val id: String,
        override val personId: String,
        override val timestamp: Instant,
        override val loggedBy: String,
        val notes: String?,
    ) : ActivityLog()
}

enum class MealUnit { ML, MIN, G }
enum class MealType { BREAST, FORMULA, SOLID }
enum class DiaperType { DRY, WET, DIRTY, BOTH }
enum class MeasurementMethod { AXILLARY, RECTAL, EAR, FOREHEAD }
enum class WeightUnit { KG, LB }
enum class HeightUnit { CM, IN }
