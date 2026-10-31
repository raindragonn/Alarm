package com.bluepig.alarm.feature.alarm

import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Space
import kotlin.math.roundToInt
import com.bluepig.alarm.designsystem.R as DesignSystemR

/**
 * v2/SlideToStop: 손잡이를 끝까지 밀면 [onSlide] (제품 0004).
 * 주머니에서 잘못 눌려 꺼지지 않게 탭으로는 안 꺼지고, 접근성 서비스는 동작(onClick)으로 끔
 */
@Composable
internal fun SlideToStop(label: String, onSlide: () -> Unit, modifier: Modifier = Modifier) {
    val colors = AlarmTheme.colors
    // 끌기 중엔 바로 바꿈. Animatable.snapTo를 launch하면 늦게 온 snapTo가 onDragStopped를 취소해 안 꺼졌음 (API 28)
    var offset by remember { mutableFloatStateOf(0f) }
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(colors.overlay, CircleShape)
            .padding(Space.s8)
            .testTag("btn_alarm_end")
            .semantics {
                contentDescription = label
                onClick { onSlide(); true }
            },
    ) {
        val maxOffset = with(LocalDensity.current) { (maxWidth - 64.dp).toPx() }
        val progress = if (maxOffset > 0) offset / maxOffset else 0f
        Text(
            label,
            Modifier.align(Alignment.Center).padding(horizontal = 72.dp).graphicsLayer { alpha = 1f - progress },
            style = AlarmTheme.typography.bodyLarge,
            color = colors.sub,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Text(
            "›››",
            Modifier.align(Alignment.CenterEnd).padding(end = Space.s12).graphicsLayer { alpha = 1f - progress },
            style = AlarmTheme.typography.unit,
            color = colors.dim,
        )
        Box(
            Modifier
                .offset { IntOffset(offset.roundToInt(), 0) }
                .size(64.dp)
                .background(colors.accent, CircleShape)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta -> offset = (offset + delta).coerceIn(0f, maxOffset) },
                    onDragStopped = {
                        if (offset >= maxOffset * SLIDE_THRESHOLD) {
                            onSlide()
                        } else {
                            animate(offset, 0f) { value, _ -> offset = value }
                        }
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(DesignSystemR.drawable.designsystem_ic_arrow),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = colors.onAccent,
            )
        }
    }
}

private const val SLIDE_THRESHOLD = 0.85f
