package com.bluepig.alarm.domain.entity.alarm.media

import kotlinx.serialization.Serializable

@Suppress("Unused")
@Serializable
sealed interface AlarmMedia : java.io.Serializable {
    val title: String

    val isMusic
        get() = this is MusicMedia

    val isRingtone
        get() = this is RingtoneMedia

    val isTube
        get() = this is TubeMedia

    fun onMusic(action: (MusicMedia) -> Unit): AlarmMedia {
        if (this is MusicMedia) {
            action.invoke(this)
        }
        return this
    }

    fun onRingtone(action: (RingtoneMedia) -> Unit): AlarmMedia {
        if (this is RingtoneMedia) action.invoke(this)
        return this
    }

    fun onTube(action: (TubeMedia) -> Unit): AlarmMedia {
        if (this is TubeMedia) {
            action.invoke(this)
        }
        return this
    }

}