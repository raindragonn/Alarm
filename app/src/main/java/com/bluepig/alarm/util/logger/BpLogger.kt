package com.bluepig.alarm.util.logger

import android.content.Context
import android.os.Bundle
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.util.ext.getGuideText
import com.bluepig.alarm.util.logger.event.Event
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

object BpLogger {
    private val _crashlytics
        get() = Firebase.crashlytics

    private val _analytics
        get() = Firebase.analytics

    private val _dateFormat
        get() = SimpleDateFormat("yyyy-MM-dd,hh:mm", Locale.getDefault())

    private val _timeFormat
        get() = SimpleDateFormat("hh:mm:ss", Locale.getDefault())

    private fun logEvent(event: Event, bundle: Bundle) {
        _analytics.logEvent(event.name, bundle)
        Timber.d("[Event] ${event.name}: $bundle")
    }

    private fun logCustomKey(customKey: CustomKey, value: String) {
        _crashlytics.setCustomKey(customKey.lowerCaseName, value)
        Timber.d("[CustomKey] ${customKey.lowerCaseName}: $value")
    }

    fun logException(t: Throwable) {
        Timber.w(t)
        _crashlytics.recordException(t)
    }

    fun logScreenView(screenName: String) {
        logEvent(Event.ScreenView, Event.ScreenView.getBundle(screenName))
        logCustomKey(CustomKey.LAST_VISIT_SCREEN, screenName)
    }

    fun logAppCreatedTime() {
        val now = _dateFormat.format(CalendarHelper.now.timeInMillis)
        logCustomKey(CustomKey.APP_CREATED_TIME, now)
    }

    fun logAlarmTotalCount(list: List<Alarm>) {
        val count = list.size
        val activeCount = list.filter { it.isActive }.size
        val recentAlarmTime = list.firstOrNull()?.timeInMillis?.let { _dateFormat.format(it) }
        val bundle = Event.AlarmCount.getBundle(count, activeCount)

        if (recentAlarmTime != null) {
            logCustomKey(CustomKey.ALARM_ACTIVE_RECENT_TIME, recentAlarmTime)
        }
        logCustomKey(CustomKey.ALARM_TOTAL_COUNT, count.toString())
        logCustomKey(CustomKey.ALARM_ACTIVE_COUNT, activeCount.toString())
        logEvent(Event.AlarmCount, bundle)
    }

    fun logAlarmSave(alarm: Alarm, isCreated: Boolean, context: Context) {
        val bundle = Event.AlarmSave.getBundle(
            isCreated,
            _timeFormat.format(alarm.timeInMillis),
            alarm.repeatWeek.getGuideText(context),
            alarm.media.javaClass.simpleName,
            alarm.media.title,
            alarm.isVolumeAutoIncrease,
            alarm.hasVibration,
            alarm.memoTtsEnabled
        )
        logEvent(Event.AlarmSave, bundle)
    }

    fun logPreviewClick() {
        logEvent(Event.PreviewClick, Event.PreviewClick.getBundle())
    }

    fun logAlarmDelete() {
        logEvent(Event.AlarmDelete, Event.AlarmDelete.getBundle())
    }

    fun logMediaSearch(mediaType: String, searchText: String) {
        logEvent(Event.MediaSearch, Event.MediaSearch.getBundle(mediaType, searchText))
    }

    fun logMediaSelect(alarmMedia: AlarmMedia) {
        logEvent(Event.MediaSelect, Event.MediaSelect.getBundle(alarmMedia))
    }

    fun logAlarmFired(alarm: Alarm) {
        val firedTime = _dateFormat.format(CalendarHelper.now.timeInMillis)
        val alarmTime = _dateFormat.format(alarm.timeInMillis)

        logEvent(
            Event.AlarmFired, Event.AlarmFired.getBundle(
                firedTime, alarmTime, alarm.media.javaClass.simpleName, alarm.media.title
            )
        )
    }

    fun logFiredAlarmClose(alarm: Alarm) {
        val closeTime = _dateFormat.format(CalendarHelper.now.timeInMillis)
        val alarmTime = _dateFormat.format(alarm.timeInMillis)

        logEvent(
            Event.FiredAlarmClose, Event.FiredAlarmClose.getBundle(
                closeTime, alarmTime, alarm.media.javaClass.simpleName, alarm.media.title
            )
        )
    }
}