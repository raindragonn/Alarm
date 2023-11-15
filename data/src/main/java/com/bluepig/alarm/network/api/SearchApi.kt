package com.bluepig.alarm.network.api

import com.bluepig.alarm.data.BuildConfig
import com.bluepig.alarm.network.response.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET(BuildConfig.SEARCH_API_ENDPOINT)
    suspend fun getSongList(
        @Query("query") query: String,
        @Query("offset") offset: Int = 0,
        @Query(BuildConfig.SEARCH_API_OPTION1) option1: Int = BuildConfig.SEARCH_API_OPTION1_VALUE.toInt(),
        @Query(BuildConfig.SEARCH_API_OPTION2) option2: String = BuildConfig.SEARCH_API_OPTION2_VALUE,
        @Query(BuildConfig.SEARCH_API_OPTION3) option3: String = BuildConfig.SEARCH_API_OPTION3_VALUE,
        @Query(BuildConfig.SEARCH_API_OPTION4) option4: Int = BuildConfig.SEARCH_API_OPTION4_VALUE.toInt()
    ): SearchResponse
}