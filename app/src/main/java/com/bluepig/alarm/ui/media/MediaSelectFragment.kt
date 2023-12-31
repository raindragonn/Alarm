package com.bluepig.alarm.ui.media

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentMediaSelectBinding
import com.bluepig.alarm.domain.preferences.AppPreferences
import com.bluepig.alarm.util.logger.BpLogger
import com.bluepig.alarm.util.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaSelectFragment : Fragment(R.layout.fragment_media_select) {
    private val _binding: FragmentMediaSelectBinding by viewBinding(FragmentMediaSelectBinding::bind)
    private val _adapter: MediaSelectFragmentAdapter by lazy {
        MediaSelectFragmentAdapter(this, appPreferences.showYoutubeSearch)
    }

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(MediaSelectFragment::class.java.simpleName)
    }

    private fun initViews() = with(_binding) {
        viewPager.adapter = _adapter
        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = _adapter.getTabTitle(position)
        }.attach()

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}