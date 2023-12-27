package com.bluepig.alarm.util.ads

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.MainBottomNativeLayoutBinding
import com.bluepig.alarm.domain.preferences.AppPreferences
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.util.ext.inflater
import com.bluepig.alarm.util.logger.BpLogger
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AdsManager @Inject constructor(
    @ApplicationContext
    private val _context: Context,
    private val _appPref: AppPreferences,
) {
    private var _lifecycle: Lifecycle? = null
    private var _lifeCycleObserver: LifecycleObserver? = null

    private val _adRequest
        get() = AdRequest.Builder().build()

    private var _interstitialAd: InterstitialAd? = null

    private fun setLifeCycle(lifecycle: Lifecycle) {
        _lifeCycleObserver?.let { observer ->
            _lifecycle?.removeObserver(observer)
        }
        _lifeCycleObserver = null
        _lifecycle = lifecycle
    }

    private val _isBottomNativeEnabled
        get() = _appPref.adsSwitch && _appPref.adsMainBottomNativeSwitch

    private val _isListNativeEnabled
        get() = _appPref.adsSwitch && _appPref.adsAlarmListNativeSwitch

    private val _isBannerEnabled
        get() = _appPref.adsSwitch && _appPref.adsAlarmBannerSwitch

    private val _isInterstitialEnabled
        get() = _appPref.adsSwitch && _appPref.adsInterstitialMusicClickSwitch

    private fun checkInterstitialEnabled(): Boolean {
        val enabled = _isInterstitialEnabled
        val savedTime = _appPref.lastInterstitialShowTime
        val now = CalendarHelper.now.timeInMillis
        val isOverTime = now - savedTime > 60 * 60 * 1000

        return enabled && isOverTime
    }

    fun loadBottomNativeAd(
        lifecycle: Lifecycle,
        container: ViewGroup
    ) {
        if (_isBottomNativeEnabled.not()) return

        kotlin.runCatching {
            setLifeCycle(lifecycle)
            val adLoader =
                AdLoader.Builder(
                    _context,
                    _context.getString(R.string.ads_main_bottom_native)
                ).forNativeAd { ad: NativeAd ->
                    _lifecycle?.apply {
                        val lifecycleObserver = DestroyListener(ad)
                        addObserver(lifecycleObserver)
                        _lifeCycleObserver = lifecycleObserver
                    }
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
            adLoader.loadAd(_adRequest)
        }.onFailure {
            BpLogger.logException(it)
        }
    }

    fun loadAlarmListNativeAd(
        lifecycle: Lifecycle,
        loadedListener: (NativeAd) -> Unit
    ) {
        if (_isListNativeEnabled.not()) return

        kotlin.runCatching {
            setLifeCycle(lifecycle)
            val adLoader =
                AdLoader.Builder(
                    _context,
                    _context.getString(R.string.ads_alarm_list_native)
                ).forNativeAd { ad: NativeAd ->
                    _lifecycle?.apply {
                        val lifecycleObserver = DestroyListener(ad)
                        addObserver(lifecycleObserver)
                        _lifeCycleObserver = lifecycleObserver
                    }
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
            adLoader.loadAd(_adRequest)
        }.onFailure {
            BpLogger.logException(it)
        }
    }

    fun loadBanner(container: ViewGroup) {
        if (_isBannerEnabled.not()) return

        kotlin.runCatching {
            val adView = AdView(_context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = context.getString(R.string.ads_alarm_banner)
            }
            adView.loadAd(_adRequest)
            container.removeAllViews()
            container.addView(adView)
        }.onFailure {
            BpLogger.logException(it)
        }
    }

    fun loadInterstitial(lifecycle: Lifecycle) {
        if (!checkInterstitialEnabled()) return

        kotlin.runCatching {
            InterstitialAd.load(
                _context,
                _context.getString(R.string.ads_media_select_interstitial),
                _adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        if (lifecycle.currentState == Lifecycle.State.CREATED) {
                            loadInterstitial(lifecycle)
                        }
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        super.onAdLoaded(ad)
                        _interstitialAd = ad
                    }
                }
            )
        }.onFailure {
            BpLogger.logException(it)
        }
    }

    fun showInterstitial(
        activity: Activity,
        onShowed: () -> Unit,
        onClose: () -> Unit,
        onLoadFail: () -> Unit
    ) {
        if (!checkInterstitialEnabled()) {
            onLoadFail.invoke()
            return
        }

        _interstitialAd?.apply {
            fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    _interstitialAd = null
                    onLoadFail.invoke()
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    _interstitialAd = null
                    _appPref.lastInterstitialShowTime = CalendarHelper.now.timeInMillis
                    onClose.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    onShowed.invoke()
                }
            }
            show(activity)
        } ?: onLoadFail.invoke()
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