package com.fpculcasi.carezze.domain.model

import java.time.Instant

data class Invitation(
    val id: String,
    val type: InvitationType,
    val targetId: String,
    val personId: String?,
    val targetName: String,
    val createdBy: String,
    val createdByName: String,
    val code: String,
    val expiresAt: Instant,
    val used: Boolean,
    val usedBy: String?,
    val usedAt: Instant?,
)

enum class InvitationType { PERSON, THERAPY }
