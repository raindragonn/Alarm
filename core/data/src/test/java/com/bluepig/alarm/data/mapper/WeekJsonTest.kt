package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.Week.FRIDAY
import com.bluepig.alarm.domain.entity.alarm.Week.MONDAY
import com.bluepig.alarm.domain.entity.alarm.Week.WEDNESDAY
import org.junit.Assert.assertEquals
import org.junit.Test

// 배포본이 alarm.repeatWeek 컬럼에 저장한 JSON. 이 문자열을 바꾸면 기존 사용자 알람을 못 읽는다.
class WeekJsonTest {
    private fun assertGolden(json: String, weeks: Set<Week>) {
        assertEquals(weeks, weeksFromJson(json))
        assertEquals(json, weeks.toJson())
    }

    @Test
    fun `1회성`() = assertGolden("[]", emptySet())

    @Test
    fun `요일 하나`() = assertGolden("""["WEDNESDAY"]""", setOf(WEDNESDAY))

    @Test
    fun `요일 여러 개`() =
        assertGolden("""["MONDAY","WEDNESDAY","FRIDAY"]""", setOf(MONDAY, WEDNESDAY, FRIDAY))
}
