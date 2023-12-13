package com.bluepig.alarm.ui.media

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bluepig.alarm.R
import com.bluepig.alarm.ui.search.SearchFragment

class MediaSelectAdapter(
    parent: Fragment,
) : FragmentStateAdapter(parent) {

    private val _fragments = mapOf<String, Fragment>(
        parent.getString(R.string.tab_title_search) to SearchFragment(),
    )

    override fun getItemCount(): Int =
        _fragments.size

    override fun createFragment(position: Int): Fragment {
        return _fragments.values.elementAt(position)
    }

    fun getTabTitle(position: Int): String {
        return _fragments.keys.elementAt(position)
    }

    fun getFragment(position: Int): Fragment {
        return _fragments.values.elementAt(position)
    }
}