package com.bluepig.alarm.util.logger.event

import androidx.core.os.bundleOf
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.google.firebase.analytics.FirebaseAnalytics

sealed interface Event {
    val name: String

    data object ScreenView : Event {
        override val name: String
            get() = FirebaseAnalytics.Event.SCREEN_VIEW

        fun getBundle(screenName: String) = bundleOf(
            FirebaseAnalytics.Param.SCREEN_NAME to screenName,
            FirebaseAnalytics.Param.SCREEN_CLASS to screenName
        )
    }

    data object AlarmCount : Event {
        override val name: String
            get() = "alarm_count"

        private const val TOTAL_COUNT = "total_count"
        private const val ACTIVE_COUNT = "active_count"

        fun getBundle(
            totalCount: Int,
            activeCount: Int
        ) = bundleOf(
            TOTAL_COUNT to totalCount,
            ACTIVE_COUNT to activeCount,
        )
    }

    data object AlarmSave : Event {
        override val name: String
            get() = "alarm_save"

        private const val IS_CREATED = "is_created"
        private const val TIME = "time"
        private const val WEEKS = "weeks"
        private const val MEDIA_TYPE = "media_type"
        private const val MEDIA_TITLE = "media_title"
        private const val VOLUME_INCREASE_ACTIVE = "volume_increase_active"
        private const val VIBRATION_ACTIVE = "vibration_active"
        private const val TTS_ACTIVE = "tts_active"

        fun getBundle(
            isCreated: Boolean,
            time: String,
            weeks: String,
            mediaType: String,
            mediaTitle: String,
            volumeIncreaseActive: Boolean,
            vibrationActive: Boolean,
            ttsActive: Boolean,
        ) = bundleOf(
            "${name}_$IS_CREATED" to isCreated,
            "${name}_$TIME" to time,
            "${name}_$WEEKS" to weeks,
            "${name}_$MEDIA_TYPE" to mediaType,
            "${name}_$MEDIA_TITLE" to mediaTitle,
            "${name}_$VOLUME_INCREASE_ACTIVE" to volumeIncreaseActive,
            "${name}_$VIBRATION_ACTIVE" to vibrationActive,
            "${name}_$TTS_ACTIVE" to ttsActive,
        )
    }

    data object AlarmDelete : Event {
        override val name: String
            get() = "alarm_delete"

        fun getBundle() = bundleOf()
    }

    data object MediaSearch : Event {
        override val name: String
            get() = "media_search"

        private const val TYPE = "type"
        private const val TEXT = "text"

        fun getBundle(
            type: String,
            searchText: String
        ) = bundleOf(
            "${name}_$TYPE" to type,
            "${name}_$TEXT" to searchText,
        )
    }

    data object MediaSelect : Event {
        override val name: String
            get() = "media_select"

        private const val TYPE = "type"
        private const val TITLE = "title"

        fun getBundle(
            alarmMedia: AlarmMedia
        ) = bundleOf(
            "${name}_$TYPE" to alarmMedia.javaClass.simpleName,
            "${name}_$TITLE" to alarmMedia.title,
        )
    }

    data object PreviewClick : Event {
        override val name: String
            get() = "preview_click"

        fun getBundle() = bundleOf()
    }

    data object AlarmFired : Event {
        override val name: String
            get() = "alarm_fired"

        private const val FIRED_TIME = "fired_time"
        private const val ALARM_TIME = "alarm_time"
        private const val MEDIA_TYPE = "media_type"
        private const val MEDIA_TITLE = "media_title"

        fun getBundle(
            firedTime: String,
            alarmTime: String,
            mediaType: String,
            mediaTitle: String,
        ) = bundleOf(
            "${name}_$FIRED_TIME" to firedTime,
            "${name}_$ALARM_TIME" to alarmTime,
            "${name}_$MEDIA_TYPE" to mediaType,
            "${name}_$MEDIA_TITLE" to mediaTitle,
        )
    }

    data object FiredAlarmClose : Event {
        override val name: String
            get() = "fired_alarm_close"

        private const val CLOSE_TIME = "close_time"
        private const val ALARM_TIME = "alarm_time"
        private const val MEDIA_TYPE = "media_type"
        private const val MEDIA_TITLE = "media_title"

        fun getBundle(
            closeTime: String,
            alarmTime: String,
            mediaType: String,
            mediaTitle: String,
        ) = bundleOf(
            "${name}_$CLOSE_TIME" to closeTime,
            "${name}_$ALARM_TIME" to alarmTime,
            "${name}_$MEDIA_TYPE" to mediaType,
            "${name}_$MEDIA_TITLE" to mediaTitle,
        )
    }
}
