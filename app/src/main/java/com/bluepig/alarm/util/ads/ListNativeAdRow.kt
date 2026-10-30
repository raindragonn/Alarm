package com.bluepig.alarm.util.ads

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
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
        Image(painterResource(R.drawable.ic_launcher), contentDescription = null, Modifier.size(48.dp))
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
private fun nativeAdView(context: Context, colors: AlarmColors): NativeAdView {
    fun dp(value: Int) = (value * context.resources.displayMetrics.density).toInt()
    fun font(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(context, id)
    fun text(sizeSp: Float, @FontRes fontId: Int, color: Int) = TextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
        typeface = font(fontId)
        setTextColor(color)
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }

    val icon = ImageView(context).apply { layoutParams = LinearLayout.LayoutParams(dp(48), dp(48)) }
    val headline = text(14f, DesignSystemR.font.designsystem_pretendard_semibold, colors.text.toArgb())
    val body = text(12f, DesignSystemR.font.designsystem_pretendard_regular, colors.sub.toArgb())
    val action = text(12f, DesignSystemR.font.designsystem_pretendard_semibold, colors.text.toArgb()).apply {
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
    return NativeAdView(context).apply {
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
