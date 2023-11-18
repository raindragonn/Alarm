package com.bluepig.alarm.network.parser

interface FilePageParser {
    suspend fun parse(url: String, userAgent: String): String
}