package com.bluepig.alarm.domain.entity

data class Alarm(
    val id: Long? = null,
    val timeInMillis: Long,
    val isActive: Boolean,
    val songName: String,
) : BaseEntity
