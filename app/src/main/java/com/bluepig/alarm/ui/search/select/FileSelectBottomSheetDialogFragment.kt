package com.bluepig.alarm.ui.search.select

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentFileSelectBinding
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FileSelectBottomSheetDialogFragment :
    BottomSheetDialogFragment(R.layout.fragment_file_select) {

    private val _binding: FragmentFileSelectBinding by viewBinding(FragmentFileSelectBinding::bind)
    private val _navArgs: FileSelectBottomSheetDialogFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(_binding) {
        val file = _navArgs.file
        _binding.tvTitle.text = file.title
        _binding.ivThumbnail.setThumbnail(file.thumbnail)
    }
}