package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.alarm.AlarmVolume
import javax.inject.Inject

class GetMaxVolume @Inject constructor(
    private val _alarmVolume: AlarmVolume,
) {
    operator fun invoke(): Int = _alarmVolume.maxVolume()
}
