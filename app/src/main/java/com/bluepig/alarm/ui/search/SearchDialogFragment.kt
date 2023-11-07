package com.bluepig.alarm.ui.search

import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.DialogFragmentSearchBinding
import com.bluepig.alarm.ui.base.BaseFullScreenDialogFragment
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchDialogFragment : BaseFullScreenDialogFragment(R.layout.dialog_fragment_search) {
    private val _binding: DialogFragmentSearchBinding by viewBinding(DialogFragmentSearchBinding::bind)

    override fun initViews() {

    }
}