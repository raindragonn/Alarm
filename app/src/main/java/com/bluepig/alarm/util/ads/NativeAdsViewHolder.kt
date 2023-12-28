package com.bluepig.alarm.util.ads

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.ItemAlarmNativeLayoutBinding
import com.google.android.gms.ads.nativead.NativeAd

class NativeAdsViewHolder(
    private val _binding: ItemAlarmNativeLayoutBinding
) : RecyclerView.ViewHolder(_binding.root) {

    fun bindEmpty(onclickButton: () -> Unit) {
        _binding.apply {
            nativeAdView.destroy()
            nativeAdView.isVisible = false

            val context = _binding.root.context
            clReview.isVisible = true
            tvHeadline.text = context.getString(R.string.review_request_title)
            tvBody.text = context.getString(R.string.review_request_body)
            btnStore.text = context.getString(R.string.store)
            btnStore.setOnClickListener { onclickButton.invoke() }
        }
    }

    fun bind(nativeAd: NativeAd) {
        _binding.apply {
            clReview.isVisible = false
            nativeAdView.isVisible = true

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