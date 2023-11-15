package com.bluepig.alarm.mapper

import com.bluepig.alarm.database.data.BaseData
import com.bluepig.alarm.domain.entity.base.BaseEntity

interface BaseMapper<Data : BaseData, Entity : BaseEntity> {
    fun mapToEntity(data: Data): Entity
    fun mapToData(entity: Entity): Data
}