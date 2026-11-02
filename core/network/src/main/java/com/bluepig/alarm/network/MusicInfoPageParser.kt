package com.bluepig.alarm.network

import org.jsoup.Jsoup

/** 4shared 다운로드 페이지에서 파일 주소를 꺼냄. 네트워크를 쓰는 블로킹 호출 */
internal class MusicInfoPageParser(
    private val _cssQuery: String = BuildConfig.PARSING_DEFAULT_SELECT,
    private val _attributeKey: String = BuildConfig.PARSING_DEFAULT_ATTR,
) {
    fun parse(url: String, userAgent: String): String {
        val doc = Jsoup
            .connect(url)
            .userAgent(userAgent)
            .get()
        val element = doc.select(_cssQuery)
        return element.attr(_attributeKey)
    }
}
