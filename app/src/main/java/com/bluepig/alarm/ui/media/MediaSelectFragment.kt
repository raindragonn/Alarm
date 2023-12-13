package com.bluepig.alarm.ui.media

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentMediaSelectBinding
import com.bluepig.alarm.util.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaSelectFragment : Fragment(R.layout.fragment_media_select) {
    private val _binding: FragmentMediaSelectBinding by viewBinding(FragmentMediaSelectBinding::bind)
    private val _adapter: MediaSelectAdapter by lazy {
        MediaSelectAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(_binding) {
        viewPager.adapter = _adapter
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = _adapter.getTabTitle(position)
        }.attach()
    }
}