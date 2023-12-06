package com.bluepig.alarm.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.bluepig.alarm.R
import com.bluepig.alarm.domain.entity.alarm.Week


class WeekButton : AppCompatTextView {
    private lateinit var week: Week

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs,
        0
    ) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WeekButton,
            0,
            0
        ).apply {
            try {
                week = Week.fromCode(getInteger(R.styleable.WeekButton_week, 1))
                text = when (week) {
                    Week.SUNDAY -> resources.getString(R.string.week_sun)
                    Week.MONDAY -> resources.getString(R.string.week_mon)
                    Week.TUESDAY -> resources.getString(R.string.week_tues)
                    Week.WEDNESDAY -> resources.getString(R.string.week_wed)
                    Week.THURSDAY -> resources.getString(R.string.week_thu)
                    Week.FRIDAY -> resources.getString(R.string.week_fri)
                    Week.SATURDAY -> resources.getString(R.string.week_sat)
                }
            } finally {
                recycle()
            }
        }
    }

    fun getWeek(): Week = week

    fun setSelected() {
        val selectedForeground =
            ResourcesCompat.getDrawable(resources, R.drawable.shape_week_selected_background, null)
        var selectedTextColor = ResourcesCompat.getColor(resources, R.color.primary_600, null)
        if (week.isWeekend) {
            foregroundTintList = ColorStateList.valueOf(context.getColor(R.color.misc_050))
            background =
                ResourcesCompat.getDrawable(resources, R.drawable.ripple_weekend_selected, null)
            selectedTextColor = ResourcesCompat.getColor(resources, R.color.misc_050, null)
        }
        setTextColor(selectedTextColor)
        foreground = selectedForeground
        invalidate()
        requestLayout()
    }

    fun unSelected() {
        val unSelectedTextColor = ResourcesCompat.getColor(resources, R.color.button_text, null)
        setTextColor(unSelectedTextColor)
        foreground = null
        invalidate()
        requestLayout()
    }

    fun setOnclickWeek(action: (Week) -> Unit) {
        setOnClickListener {
            action.invoke(getWeek())
        }
    }
}