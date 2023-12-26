package com.bluepig.alarm.util.ads

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bluepig.alarm.databinding.ItemAlarmNativeLayoutBinding
import com.google.android.gms.ads.nativead.NativeAd

class NativeAdsViewHolder(
    private val _binding: ItemAlarmNativeLayoutBinding
) : RecyclerView.ViewHolder(_binding.root) {

    fun bindEmpty(onclickButton: () -> Unit) {
        _binding.apply {
            adBadge.isVisible = false
            adIcon.isVisible = false
            adHeadline.text = "리뷰 쓰러 가기"
            adBody.text = "앱이 마음에 드신다면 칭찬을, 불편한 점이나 건의사항이 있다면 남겨주세요"
            adStoreButton.isVisible = true
            adStoreButton.text = "스토어"
            adStoreButton.setOnClickListener { onclickButton.invoke() }
        }
    }

    fun bind(nativeAd: NativeAd) {
        _binding.apply {
            val adview = nativeAdView
            adBadge.isVisible = true

            adHeadline.text = nativeAd.headline
            adview.headlineView = adHeadline

            adBody.text = nativeAd.body
            adview.bodyView = adBody

            nativeAd.icon?.uri?.let {
                adIcon.load(it)
                adview.iconView = adIcon
                adIcon.isVisible = true
            } ?: kotlin.run {
                adIcon.isVisible = false
            }
            nativeAd.callToAction?.let {
                adStoreButton.text = it
                adview.callToActionView = adStoreButton
                adStoreButton.isVisible = true
            } ?: kotlin.run {
                adStoreButton.isVisible = false
            }

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