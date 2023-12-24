package com.bluepig.alarm.util.ads

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bluepig.alarm.databinding.ItemAlarmNativeLayoutBinding
import com.google.android.gms.ads.nativead.NativeAd

class NativeAdsViewHolder(
    private val _binding: ItemAlarmNativeLayoutBinding
) : RecyclerView.ViewHolder(_binding.root) {

    fun bind(nativeAd: NativeAd) {
        _binding.apply {
            val adview = nativeAdView
            adHeadline.text = nativeAd.headline
            adBody.text = nativeAd.body

            nativeAd.icon?.uri?.let {
                adIcon.load(it)
                adview.iconView = adIcon
                adIcon.isVisible = true
            } ?: kotlin.run {
                adIcon.isVisible = false
            }
            nativeAd.store?.let {
                adStoreButton.text = it
                adview.storeView = adStoreButton
                adStoreButton.isVisible = true
            } ?: kotlin.run {
                adStoreButton.isVisible = false
            }

            adview.callToActionView = adview
            adview.headlineView = adHeadline

            adview.setNativeAd(nativeAd)
        }
    }

    companion object {
        fun create(
            binding: ItemAlarmNativeLayoutBinding
        ): NativeAdsViewHolder {
            return NativeAdsViewHolder(binding)
        }
    }
}