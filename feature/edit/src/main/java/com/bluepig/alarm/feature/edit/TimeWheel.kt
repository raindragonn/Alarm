package com.bluepig.alarm.feature.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.text.DateFormatSymbols
import kotlin.math.abs
import android.text.format.DateFormat as AndroidDateFormat

private val ItemHeight = 62.dp
private val WheelHeight = 172.dp
private const val LOOP_TIMES = 1000

/**
 * v2/TimeWheel. 가운데 띠(band)가 선택 값. 한국어처럼 시각 패턴이 오전/오후로 시작하면 앞에, 아니면 뒤에.
 * 24시간제 기기는 0~23시, 오전/오후 없음
 */
@Composable
internal fun TimeWheel(
    hourOfDay: Int,
    minute: Int,
    is24Hour: Boolean,
    onTimeChange: (hourOfDay: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    val locale = LocalConfiguration.current.locales[0]
    val meridiemFirst = remember(locale) { AndroidDateFormat.getBestDateTimePattern(locale, "hma").startsWith("a") }
    val amPm = remember(locale) { DateFormatSymbols.getInstance(locale).amPmStrings.toList() }
    val time by rememberUpdatedState(hourOfDay to minute)

    Box(modifier.fillMaxWidth().height(WheelHeight).testTag("timepicker"), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(colors.band, RoundedCornerShape(18.dp))
                .border(1.dp, colors.line, RoundedCornerShape(18.dp)),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val meridiem = @Composable {
                if (!is24Hour) {
                    WheelColumn(
                        labels = amPm,
                        initialIndex = if (hourOfDay < 12) 0 else 1,
                        loop = false,
                        width = 64.dp,
                        style = typography.wheelLabel,
                        otherScale = 18f / 22f,
                        onSelect = { index -> onTimeChange(time.first % 12 + index * 12, time.second) },
                    )
                }
            }
            if (meridiemFirst) meridiem()
            WheelColumn(
                labels = if (is24Hour) (0..23).map(::twoDigits) else (listOf(12) + (1..11)).map(::twoDigits),
                initialIndex = if (is24Hour) hourOfDay else hourOfDay % 12,
                loop = true,
                width = 84.dp,
                style = typography.numWheel,
                otherScale = 36f / 56f,
                onSelect = { index ->
                    val hour = if (is24Hour) index else index + if (time.first >= 12) 12 else 0
                    onTimeChange(hour, time.second)
                },
            )
            Text(":", Modifier.width(16.dp), style = typography.numWheel, color = colors.text)
            WheelColumn(
                labels = (0..59).map(::twoDigits),
                initialIndex = minute,
                loop = true,
                width = 84.dp,
                style = typography.numWheel,
                otherScale = 36f / 56f,
                onSelect = { onTimeChange(time.first, it) },
            )
            if (!meridiemFirst) meridiem()
        }
    }
}

private fun twoDigits(value: Int) = "%02d".format(value)

@Composable
private fun WheelColumn(
    labels: List<String>,
    initialIndex: Int,
    loop: Boolean,
    width: Dp,
    style: TextStyle,
    otherScale: Float,
    onSelect: (Int) -> Unit,
) {
    val count = labels.size
    val state = rememberLazyListState(if (loop) count * (LOOP_TIMES / 2) + initialIndex else initialIndex)
    val itemPx = with(LocalDensity.current) { ItemHeight.toPx() }
    val centered by remember { derivedStateOf { state.centeredIndex(itemPx) } }
    val currentOnSelect by rememberUpdatedState(onSelect)
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress to centered }
            .filter { (scrolling, _) -> !scrolling }
            .map { (_, index) -> index % count }
            .distinctUntilChanged()
            .drop(1) // 처음 멈춰 있는 값은 이미 선택된 값
            .collect { currentOnSelect(it) }
    }
    LazyColumn(
        Modifier
            .width(width)
            .height(WheelHeight)
            .semantics { contentDescription = labels[centered % count] },
        state = state,
        contentPadding = PaddingValues(vertical = (WheelHeight - ItemHeight) / 2),
        flingBehavior = rememberSnapFlingBehavior(state, SnapPosition.Center),
    ) {
        items(if (loop) count * LOOP_TIMES else count) { index ->
            val distance by remember {
                derivedStateOf {
                    val center = state.firstVisibleItemIndex + state.firstVisibleItemScrollOffset / itemPx
                    abs(index - center).coerceAtMost(1f)
                }
            }
            Box(Modifier.fillMaxWidth().height(ItemHeight), contentAlignment = Alignment.Center) {
                Text(
                    labels[index % count],
                    Modifier.graphicsLayer {
                        val scale = 1f - (1f - otherScale) * distance
                        scaleX = scale
                        scaleY = scale
                    },
                    style = style,
                    color = lerp(AlarmTheme.colors.text, AlarmTheme.colors.dim, distance),
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

private fun LazyListState.centeredIndex(itemPx: Float) =
    firstVisibleItemIndex + if (firstVisibleItemScrollOffset > itemPx / 2) 1 else 0
