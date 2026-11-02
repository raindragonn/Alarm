package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.fake.FakeAppConfigRepository
import com.bluepig.alarm.domain.fake.FakeTubeRepository
import com.bluepig.alarm.domain.fake.allOnConfig
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTubeMediaListTest {
    private val linked = TubeMedia("abcdefghijk", "", "링크 영상")
    private val searched = TubeMedia("bcdefghijkl", "", "검색 영상")

    @Test
    fun `링크 검색만 켜져 있으면 검색하지 않는다`() = runTest {
        val repository = FakeTubeRepository(searchResult = listOf(searched))
        val config = FakeAppConfigRepository(allOnConfig.copy(showOnlyYoutubeLink = true))

        val result = GetTubeMediaList(repository, config)("wake up").getOrThrow()

        assertEquals(emptyList<TubeMedia>(), result)
        assertEquals(0, repository.searchCount)
    }

    @Test
    fun `링크면 그 영상, 아니면 검색 결과`() = runTest {
        val config = FakeAppConfigRepository(allOnConfig.copy(showOnlyYoutubeLink = false))

        val byLink = GetTubeMediaList(FakeTubeRepository(linked, listOf(searched)), config)("link")
        val bySearch = GetTubeMediaList(FakeTubeRepository(null, listOf(searched)), config)("wake up")

        assertEquals(listOf(linked), byLink.getOrThrow())
        assertEquals(listOf(searched), bySearch.getOrThrow())
    }
}
