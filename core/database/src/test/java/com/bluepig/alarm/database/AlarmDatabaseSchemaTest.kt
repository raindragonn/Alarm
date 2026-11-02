package com.bluepig.alarm.database

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class AlarmDatabaseSchemaTest {
    // 빌드할 때마다 Room이 schemas/의 v1 스키마를 현재 엔티티로 다시 쓴다.
    @Test
    fun `v1 스키마는 배포본과 같다`() {
        val schema = File("schemas/${AlarmDatabase::class.qualifiedName}/1.json").readText()
        val identityHash = Json.parseToJsonElement(schema)
            .jsonObject["database"]!!.jsonObject["identityHash"]!!.jsonPrimitive.content

        assertEquals(
            "AlarmData를 바꿨다면 AlarmDatabase의 version을 올리고 마이그레이션을 추가",
            "5b57f062bcfb9c642937c8fa946a327d",
            identityHash,
        )
    }
}
