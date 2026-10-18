package com.fpculcasi.carezze.domain.model

data class Person(
    val id: String,
    val name: String,
    val nickname: String?,
    val createdBy: String,
    val members: Map<String, MemberRole>,
)

enum class MemberRole { OWNER, EDITOR }
