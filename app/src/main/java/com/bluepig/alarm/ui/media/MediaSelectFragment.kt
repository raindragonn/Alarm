package com.bluepig.alarm.ui.media

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentMediaSelectBinding
import com.bluepig.alarm.domain.usecase.GetAppConfig
import com.bluepig.alarm.util.logger.BpLogger
import com.bluepig.alarm.util.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaSelectFragment : Fragment(R.layout.fragment_media_select) {
    private val _binding: FragmentMediaSelectBinding by viewBinding(FragmentMediaSelectBinding::bind)

    @Inject
    lateinit var getAppConfig: GetAppConfig

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val useYoutube = getAppConfig().showYoutubeSearch
            initViews(MediaSelectFragmentAdapter(this@MediaSelectFragment, useYoutube))
        }
    }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(MediaSelectFragment::class.java.simpleName)
    }

    private fun initViews(adapter: MediaSelectFragmentAdapter) = with(_binding) {
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
