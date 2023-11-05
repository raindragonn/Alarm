package com.bluepig.mapper

interface BaseMapper<Data, Entity> {
    fun mapToEntity(data: Data): Entity
    fun mapToData(entity: Entity): Data
}