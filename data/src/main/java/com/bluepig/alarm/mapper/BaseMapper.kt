package com.bluepig.alarm.mapper

interface BaseMapper<Data, Entity> {
    fun mapToEntity(data: Data): Entity
    fun mapToData(entity: Entity): Data
}