package com.bluepig.alarm.data.network.parser

interface MusicInfoPageParser {
    suspend fun parse(url: String, userAgent: String): String
}