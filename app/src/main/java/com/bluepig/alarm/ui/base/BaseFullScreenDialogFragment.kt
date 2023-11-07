package com.bluepig.alarm.ui.base

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.bluepig.alarm.R

/**
 * Base full screen dialog fragment
 */
abstract class BaseFullScreenDialogFragment(
    @LayoutRes layoutId: Int,
) : DialogFragment(layoutId) {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.Base_Theme_Alarm)
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                requestFeature(Window.FEATURE_NO_TITLE)
                setBackgroundDrawable(ColorDrawable(requireContext().getColor(R.color.transparent)))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    abstract fun initViews()
}