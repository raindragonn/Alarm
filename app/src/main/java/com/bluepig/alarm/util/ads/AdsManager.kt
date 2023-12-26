package com.bluepig.alarm.util.ads

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.MainBottomNativeLayoutBinding
import com.bluepig.alarm.util.ext.inflater
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class AdsManager {
    private var _activity: AppCompatActivity? = null
    private var _fragment: Fragment? = null

    constructor(activity: AppCompatActivity) {
        _activity = activity
    }

    constructor(fragment: Fragment) {
        _fragment = fragment
    }

    private val _context
        get() = _activity ?: _fragment?.context

    private val _lifecycle
        get() = _activity?.lifecycle
            ?: _fragment?.lifecycle

    private val _lifeCycleObserverListener = mutableListOf<LifecycleObserver>()

    fun loadBottomNativeAd(
        container: ViewGroup
    ) {
        kotlin.runCatching {
            val context = _context ?: return@runCatching

            val adLoader =
                AdLoader.Builder(
                    context,
                    context.getString(R.string.ads_main_bottom_native)
                ).forNativeAd { ad: NativeAd ->
                    val lifecycleObserver = DestroyListener(ad)
                    _lifecycle?.addObserver(lifecycleObserver)
                    _lifeCycleObserverListener.add(lifecycleObserver)
                    applyBottomNativeAd(container, ad)
                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                }).withNativeAdOptions(
                    NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build()
                ).build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    fun loadAlarmListNativeAd(
        loadedListener: (NativeAd) -> Unit
    ) {
        kotlin.runCatching {
            val context = _context ?: return@runCatching

            val adLoader =
                AdLoader.Builder(
                    context,
                    context.getString(R.string.ads_alarm_list_native)
                ).forNativeAd { ad: NativeAd ->
                    val lifecycleObserver = DestroyListener(ad)
                    _lifecycle?.addObserver(lifecycleObserver)
                    _lifeCycleObserverListener.add(lifecycleObserver)
                    loadedListener.invoke(ad)
                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                }).withNativeAdOptions(
                    NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build()
                ).build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    fun release() {
        _lifeCycleObserverListener.forEach {
            _lifecycle?.removeObserver(it)
        }
    }

    private fun applyBottomNativeAd(container: ViewGroup, ad: NativeAd) {
        val adview = MainBottomNativeLayoutBinding.inflate(container.inflater).apply {
            adHeadline.text = ad.headline
        }

        adview.nativeAdView.callToActionView = adview.nativeAdView
        adview.nativeAdView.headlineView = adview.adHeadline
        adview.nativeAdView.setNativeAd(ad)
        container.removeAllViews()
        container.addView(adview.root)
    }
}

private class DestroyListener(private val nativeAd: NativeAd) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            nativeAd.destroy()
        }
    }
}