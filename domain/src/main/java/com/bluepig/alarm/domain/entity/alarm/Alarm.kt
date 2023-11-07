package com.bluepig.alarm.domain.entity.alarm

import com.bluepig.alarm.domain.entity.base.BaseEntity

data class Alarm(
    val id: Long? = null,
    val timeInMillis: Long,
    val isActive: Boolean,
    val songName: String,
) : BaseEntity
