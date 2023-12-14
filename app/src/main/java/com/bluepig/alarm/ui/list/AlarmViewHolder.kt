package com.bluepig.alarm.ui.list

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.ItemAlarmBinding
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.util.ext.getGuideDrawableTintColor
import com.bluepig.alarm.util.ext.getGuideText
import java.text.SimpleDateFormat
import java.util.Locale

class AlarmViewHolder private constructor(
    private val _binding: ItemAlarmBinding,
) : RecyclerView.ViewHolder(_binding.root) {

    private val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    private val meridiemFormat = SimpleDateFormat("a", Locale.getDefault())

    @SuppressLint("UseCompatTextViewDrawableApis")
    fun bind(alarm: Alarm) {
        _binding.apply {
            tvRepeatGuide.text = alarm.repeatWeek.getGuideText(root.context)
            tvRepeatGuide.compoundDrawableTintList =
                ColorStateList.valueOf(
                    alarm.repeatWeek.getGuideDrawableTintColor(
                        root.context,
                        alarm.isActive
                    )
                )
            tvTime.text = timeFormat.format(alarm.timeInMillis)
            tvMeridiem.text = meridiemFormat.format(alarm.timeInMillis)

            tvMedia.text = alarm.media.title

            groupMemo.isVisible = alarm.memo.isNotBlank()
            tvMemo.text = alarm.memo

            val colorStateList =
                root.resources.getColorStateList(R.color.switch_track, null)
            switchOnOff.trackTintList = colorStateList
            switchOnOff.trackDecorationTintList = colorStateList

            switchOnOff.isChecked = alarm.isActive

            listOf(tvRepeatGuide, tvTime, tvMeridiem, tvMedia, tvMemo, tvMediaGuide, tvMemoGuide)
                .forEach {
                    it.isEnabled = alarm.isActive
                }
        }
    }

    companion object {
        fun create(
            binding: ItemAlarmBinding
        ): AlarmViewHolder {
            return AlarmViewHolder(binding)
        }
    }
}