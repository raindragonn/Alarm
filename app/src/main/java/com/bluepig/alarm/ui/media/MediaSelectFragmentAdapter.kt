package com.bluepig.alarm.ui.media

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bluepig.alarm.R
import com.bluepig.alarm.ui.media.music.MusicSearchFragment
import com.bluepig.alarm.ui.media.ringtone.RingtoneFragment
import com.bluepig.alarm.ui.media.tube.TubeSearchFragment

class MediaSelectFragmentAdapter(
    parent: Fragment,
    useYoutube: Boolean,
) : FragmentStateAdapter(parent) {

    private val _fragments = if (useYoutube) {
        mapOf(
            parent.getString(R.string.youtube) to TubeSearchFragment(),
            parent.getString(R.string.song) to MusicSearchFragment(),
            parent.getString(R.string.ringtone) to RingtoneFragment(),
        )
    } else {
        mapOf(
            parent.getString(R.string.song) to MusicSearchFragment(),
            parent.getString(R.string.ringtone) to RingtoneFragment(),
        )
    }

    override fun getItemCount(): Int =
        _fragments.size

    override fun createFragment(position: Int): Fragment {
        return _fragments.values.elementAt(position)
    }

    fun getTabTitle(position: Int): String {
        return _fragments.keys.elementAt(position)
    }
}