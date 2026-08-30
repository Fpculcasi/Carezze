package com.fpculcasi.carezze.domain.model

data class User(
    val id: String,
    val email: String?,
    val displayName: String,
    val language: Language,
    val temperatureUnit: TemperatureUnit,
    val quietHoursStart: String,
    val quietHoursEnd: String,
    val personAccess: List<String>,
    val therapyAccess: List<String>,
    val isAnonymous: Boolean,
)

enum class Language { IT, EN }

enum class TemperatureUnit { C, F }
