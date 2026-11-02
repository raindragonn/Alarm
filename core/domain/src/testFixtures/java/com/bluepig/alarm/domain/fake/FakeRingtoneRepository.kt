package com.bluepig.alarm.domain.fake

import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.repository.RingtoneRepository

class FakeRingtoneRepository(private val ringtones: List<RingtoneMedia> = emptyList()) : RingtoneRepository {
    override suspend fun getRingtoneList() = ringtones
}
