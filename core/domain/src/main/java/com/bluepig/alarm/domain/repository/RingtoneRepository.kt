package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia

interface RingtoneRepository {
    suspend fun getRingtoneList(): List<RingtoneMedia>
}