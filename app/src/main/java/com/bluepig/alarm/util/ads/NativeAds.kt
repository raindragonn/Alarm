package com.bluepig.alarm.util.ads

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.load
import com.bluepig.alarm.R
import com.bluepig.alarm.designsystem.theme.AlarmColors
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Space
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.bluepig.alarm.designsystem.R as DesignSystemR

/** 피그마 v2/NativeAdRow. 목록 맨 위 네이티브 광고, 광고가 없으면 리뷰 요청 */
@Composable
fun ListNativeAdRow(adsManager: AdsManager, onReviewClick: () -> Unit, modifier: Modifier = Modifier) {
    var ad by remember { mutableStateOf<NativeAd?>(null) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) { adsManager.loadAlarmListNativeAd(lifecycle) { ad = it } }
    val colors = AlarmTheme.colors
    Column(modifier) {
        val current = ad
        if (current == null) {
            ReviewRow(onReviewClick)
        } else {
            AndroidView(
                factory = { context -> nativeAdView(context, colors) },
                update = { it.bind(current) },
            )
        }
        HorizontalDivider(color = colors.line)
    }
}

/** 피그마 v2/BannerNav의 BottomNativeAd. 모든 화면 아래 가운데 한 줄 광고 (제품 0010), 광고가 없으면 영역 없음 */
@Composable
fun BottomNativeAdBar(adsManager: AdsManager) {
    var ad by remember { mutableStateOf<NativeAd?>(null) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) { adsManager.loadBottomNativeAd(lifecycle) { ad = it } }
    val current = ad ?: return
    val colors = AlarmTheme.colors
    Column(Modifier.background(colors.bar)) {
        HorizontalDivider(color = colors.line)
        AndroidView(
            factory = { context -> bottomNativeAdView(context, colors) },
            modifier = Modifier.fillMaxWidth(),
            update = { it.bindBottom(current) },
        )
    }
}

@Composable
private fun ReviewRow(onClick: () -> Unit) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    Row(
        Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = Space.s24, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 적응형 아이콘 108dp 중 보이는 72dp만 48dp에 담음
        Image(
            painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .paint(painterResource(R.drawable.ic_launcher_background), contentScale = ContentScale.FillBounds),
            contentScale = FixedScale(48f / 72f),
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                stringResource(R.string.ad_headline, stringResource(R.string.review_request_title)),
                style = typography.labelStrong,
                color = colors.text,
            )
            Text(
                stringResource(R.string.review_request_body),
                style = typography.smallRegular,
                color = colors.sub,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            stringResource(R.string.store),
            Modifier
                .border(1.dp, colors.dim, CircleShape)
                .padding(horizontal = Space.s12, vertical = 7.dp),
            style = typography.smallStrong,
            color = colors.text,
        )
    }
}

// AdMob은 광고 요소를 NativeAdView 안의 View로 등록해야 해서 ReviewRow와 같은 모양을 View로 만듦
private fun nativeAdView(context: Context, colors: AlarmColors): NativeAdView = with(context) {
    val icon = ImageView(context).apply { layoutParams = LinearLayout.LayoutParams(dp(48), dp(48)) }
    val headline = adText(14f, SEMIBOLD, colors.text)
    val body = adText(12f, REGULAR, colors.sub)
    val action = adText(12f, SEMIBOLD, colors.text).apply {
        setPadding(dp(12), dp(7), dp(12), dp(7))
        background = GradientDrawable().apply {
            cornerRadius = dp(999).toFloat()
            setStroke(dp(1), colors.dim.toArgb())
        }
    }
    val texts = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(14)
            marginEnd = dp(14)
        }
        addView(headline)
        addView(body)
    }
    val row = LinearLayout(context).apply {
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(24), dp(14), dp(24), dp(14))
        addView(icon)
        addView(texts)
        addView(action)
    }
    NativeAdView(context).apply {
        addView(row)
        iconView = icon
        headlineView = headline
        bodyView = body
        callToActionView = action
    }
}

private fun NativeAdView.bind(ad: NativeAd) {
    (headlineView as TextView).text = context.getString(R.string.ad_headline, ad.headline)
    (bodyView as TextView).text = ad.body
    (callToActionView as TextView).apply {
        text = ad.callToAction
        visibility = if (ad.callToAction == null) View.GONE else View.VISIBLE
    }
    (iconView as ImageView).apply {
        val uri = ad.icon?.uri
        visibility = if (uri == null) View.GONE else View.VISIBLE
        uri?.let { load(it) }
    }
    setNativeAd(ad)
}

// 가운데 한 줄. 배지·구분선·빈 곳은 클릭되지 않게 제목·CTA만 광고 요소로 등록 (AdMob 배경 클릭 금지)
private fun bottomNativeAdView(context: Context, colors: AlarmColors): NativeAdView = with(context) {
    val badge = adText(12f, SEMIBOLD, colors.sub).apply {
        setText(R.string.ad_badge)
        setPadding(dp(8), dp(2), dp(8), dp(2))
        background = GradientDrawable().apply {
            cornerRadius = dp(999).toFloat()
            setColor(colors.fill.toArgb())
        }
    }
    // 제목이 길면 남은 폭만큼 줄어 말줄임
    val headline = adText(13f, REGULAR, colors.text).apply {
        layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f).apply { marginStart = dp(8) }
    }
    val divider = View(context).apply {
        layoutParams = LinearLayout.LayoutParams(dp(1), dp(12)).apply {
            marginStart = dp(8)
            marginEnd = dp(8)
        }
        setBackgroundColor(colors.dim.toArgb())
    }
    val action = adText(13f, REGULAR, colors.text).apply {
        val chevron = ContextCompat.getDrawable(context, DesignSystemR.drawable.designsystem_ic_chevron)?.mutate()
        chevron?.setBounds(0, 0, dp(14), dp(14))
        chevron?.setTint(colors.text.toArgb())
        setCompoundDrawables(null, null, chevron, null)
        compoundDrawablePadding = dp(2)
    }
    val actionGroup = LinearLayout(context).apply {
        gravity = Gravity.CENTER_VERTICAL
        addView(divider)
        addView(action)
    }
    val row = LinearLayout(context).apply {
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER)
        addView(badge)
        addView(headline)
        addView(actionGroup)
    }
    val bar = FrameLayout(context).apply {
        minimumHeight = dp(40) // AdMob: 32dp 미만 광고 영역은 수익 제외
        setPadding(dp(24), dp(8), dp(24), dp(8))
        addView(row)
    }
    NativeAdView(context).apply {
        addView(bar)
        headlineView = headline
        callToActionView = action
    }
}

private fun NativeAdView.bindBottom(ad: NativeAd) {
    (headlineView as TextView).text = ad.headline
    (callToActionView as TextView).text = ad.callToAction
    (callToActionView?.parent as View).isVisible = ad.callToAction != null
    setNativeAd(ad)
}

private val SEMIBOLD = DesignSystemR.font.designsystem_pretendard_semibold
private val REGULAR = DesignSystemR.font.designsystem_pretendard_regular

private fun Context.dp(value: Int) = (value * resources.displayMetrics.density).toInt()

private fun Context.adText(sizeSp: Float, @FontRes fontId: Int, color: Color) = TextView(this).apply {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
    typeface = ResourcesCompat.getFont(context, fontId)
    setTextColor(color.toArgb())
    maxLines = 1
    ellipsize = TextUtils.TruncateAt.END
}
