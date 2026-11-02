package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia

interface TubeRepository {
    /** @return 검색 결과와 다음 페이지 토큰 (마지막 페이지면 null) */
    suspend fun getTubeList(
        query: String,
        nextPageToken: String? = null
    ): Pair<List<TubeMedia>, String?>

    suspend fun checkTubeMedia(query: String): TubeMedia?
}
