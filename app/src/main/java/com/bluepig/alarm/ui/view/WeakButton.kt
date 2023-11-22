package com.bluepig.alarm.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.bluepig.alarm.R
import com.bluepig.alarm.domain.entity.alarm.Weak


class WeakButton : AppCompatTextView {
    private lateinit var weak: Weak

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs,
        0
    ) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WeakButton,
            0,
            0
        ).apply {
            try {
                weak = Weak.fromCode(getInteger(R.styleable.WeakButton_weak, 1))
                text = when (weak) {
                    Weak.SUNDAY -> resources.getString(R.string.weak_sun)
                    Weak.MONDAY -> resources.getString(R.string.weak_mon)
                    Weak.TUESDAY -> resources.getString(R.string.weak_tues)
                    Weak.WEDNESDAY -> resources.getString(R.string.weak_wed)
                    Weak.THURSDAY -> resources.getString(R.string.weak_thu)
                    Weak.FRIDAY -> resources.getString(R.string.weak_fri)
                    Weak.SATURDAY -> resources.getString(R.string.weak_sat)
                }
            } finally {
                recycle()
            }
        }
    }

    fun getWeak(): Weak = weak

    fun setSelected() {
        val selectedForeground =
            ResourcesCompat.getDrawable(resources, R.drawable.shape_weak_selected_background, null)
        val selectedTextColor = ResourcesCompat.getColor(resources, R.color.primary_600, null)
        foreground = selectedForeground
        setTextColor(selectedTextColor)
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

    fun setOnclickWeak(action: (Weak) -> Unit) {
        setOnClickListener {
            action.invoke(getWeak())
        }
    }
}