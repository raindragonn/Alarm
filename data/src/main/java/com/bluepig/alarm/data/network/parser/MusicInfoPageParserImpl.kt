package com.bluepig.alarm.data.network.parser

import com.bluepig.alarm.data.BuildConfig
import org.jsoup.Jsoup

class MusicInfoPageParserImpl(
    private val _cssQuery: String = BuildConfig.PARSING_DEFAULT_SELECT,
    private val _attributeKey: String = BuildConfig.PARSING_DEFAULT_ATTR,
) : MusicInfoPageParser {
    override suspend fun parse(url: String, userAgent: String): String {
        val doc = Jsoup
            .connect(url)
            .userAgent(userAgent)
            .get()
        val element = doc.select(_cssQuery)
        return element.attr(_attributeKey)
    }
}
