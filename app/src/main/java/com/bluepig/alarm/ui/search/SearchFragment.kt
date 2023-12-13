package com.bluepig.alarm.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentSearchBinding
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.result.onFailureWitLoading
import com.bluepig.alarm.ui.search.select.MediaSelectBottomSheetDialogFragment
import com.bluepig.alarm.util.ext.setOnEnterListener
import com.bluepig.alarm.util.ext.setOnLoadMore
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private val _binding: FragmentSearchBinding by viewBinding(FragmentSearchBinding::bind)
    private val _vm: SearchViewModel by viewModels()

    private val _adapter: SearchAdapter by lazy { SearchAdapter(::itemClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(_binding) {
        rvSearch.adapter = _adapter
        rvSearch.setOnLoadMore {
            _vm.search()
        }
        etSearch.setOnEnterListener(_vm::search)

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            _vm.fileList
                .stateIn(this)
                .collect(::searchListHandle)
        }
    }

    private fun searchListHandle(result: Result<List<MusicInfo>>) {
        result.onSuccess { list ->
            changeLoadingState(false)
            _adapter.submitList(list)
        }.onFailureWitLoading(::onLoading) {
            showErrorToast(it)
            changeLoadingState(false)
        }
    }

    private fun onLoading() {
        changeLoadingState(true)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun itemClick(file: MusicInfo) {
        findNavController().navigate(
            R.id.MediaSelectBottomSheetDialogFragment,
            bundleOf(
                MediaSelectBottomSheetDialogFragment.KEY_ARGS_MUSIC_INFO to file
            )
        )
    }

    private fun changeLoadingState(isVisible: Boolean) {
        _binding.pbLoading.isVisible = isVisible
    }
}