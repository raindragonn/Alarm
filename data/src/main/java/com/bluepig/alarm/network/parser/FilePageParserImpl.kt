package com.bluepig.alarm.network.parser

import com.bluepig.alarm.data.BuildConfig
import org.jsoup.Jsoup

class FilePageParserImpl : FilePageParser {
    override suspend fun parse(url: String): String {
        val doc = Jsoup.connect(url).get()
        val element = doc.select(BuildConfig.PARSING_DEFAULT_SELECT)
        return element.attr(BuildConfig.PARSING_DEFAULT_ATTR)
    }
}