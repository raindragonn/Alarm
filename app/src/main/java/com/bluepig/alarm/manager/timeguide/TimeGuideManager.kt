package com.bluepig.alarm.manager.timeguide

import android.content.Context
import com.bluepig.alarm.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TimeGuideManager @Inject constructor(
    @ApplicationContext
    private val _context: Context
) {
    // TODO: 실제 동작 확인 후 초단위 사용
    fun getRemainingTimeGuide(remainingTime: Long): String {
        val seconds = remainingTime / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

//        val secondsGuide = seconds % 60
        val minutesGuide = minutes % 60
        val hoursGuide = hours % 24

        return if (days > 0) {
            _context.getString(R.string.day_remaining_guide, days)
        } else {
            if (hoursGuide > 0) {
                _context.getString(R.string.hour_remaining_guide, hoursGuide, minutesGuide)
            } else {
                if (minutesGuide > 0) {
                    if (minutesGuide == 1L) {
                        _context.getString(R.string.minute_remaining_guide, minutesGuide + 1)
                    } else {
                        _context.getString(R.string.minute_remaining_guide, minutesGuide)
                    }
                } else {
                    _context.getString(R.string.soon_remaining_guide)
                }
            }
        }
    }
}