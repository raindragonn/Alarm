package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import kotlinx.serialization.json.Json

// alarm 테이블 alarmMedia·repeatWeek 컬럼의 JSON. 바꾸면 기존 사용자 알람을 못 읽음 (골든 테스트)
internal fun alarmMediaFromJson(json: String): AlarmMedia = Json.decodeFromString(json)

internal fun AlarmMedia.toJson(): String = Json.encodeToString(this)

internal fun weeksFromJson(json: String): Set<Week> = Json.decodeFromString(json)

internal fun Set<Week>.toJson(): String = Json.encodeToString(this)
