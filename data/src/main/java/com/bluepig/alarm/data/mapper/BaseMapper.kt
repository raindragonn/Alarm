package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.data.database.data.BaseData
import com.bluepig.alarm.domain.entity.base.BaseEntity

interface BaseMapper<Data : BaseData, Entity : BaseEntity> {
    fun mapToEntity(data: Data): Entity
    fun mapToData(entity: Entity): Data
}