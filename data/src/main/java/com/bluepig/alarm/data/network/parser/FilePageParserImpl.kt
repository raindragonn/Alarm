package com.bluepig.alarm.data.network.parser

import com.bluepig.alarm.data.BuildConfig
import org.jsoup.Jsoup

class FilePageParserImpl : FilePageParser {
    override suspend fun parse(url: String, userAgent: String): String {
        val doc = Jsoup
            .connect(url)
            .userAgent(userAgent)
            .get()
        val element = doc.select(BuildConfig.PARSING_DEFAULT_SELECT)
        return element.attr(BuildConfig.PARSING_DEFAULT_ATTR)
    }
}