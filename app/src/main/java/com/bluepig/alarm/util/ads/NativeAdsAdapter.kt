package com.bluepig.alarm.util.ads

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bluepig.alarm.databinding.ItemAlarmNativeLayoutBinding
import com.bluepig.alarm.util.ext.inflater
import com.google.android.gms.ads.nativead.NativeAd


class NativeAdsAdapter : ListAdapter<NativeAd, NativeAdsViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NativeAdsViewHolder {
        val binding = ItemAlarmNativeLayoutBinding.inflate(parent.inflater, parent, false)
        return NativeAdsViewHolder.create(binding)
    }

    override fun onBindViewHolder(holder: NativeAdsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<NativeAd>() {
            override fun areItemsTheSame(oldItem: NativeAd, newItem: NativeAd): Boolean {
                return oldItem.headline == newItem.headline
            }

            override fun areContentsTheSame(oldItem: NativeAd, newItem: NativeAd): Boolean {
                return oldItem.body == newItem.body
            }
        }
    }
}