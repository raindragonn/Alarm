package com.bluepig.alarm.domain.model

data class Alarm(
    val id: Int? = null,
    val timeInMillis: Long,
    val isActive: Boolean,
    val songName: String,
)
