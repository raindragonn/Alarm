package com.bluepig.alarm.data.network.parser

interface FilePageParser {
    suspend fun parse(url: String, userAgent: String): String
}