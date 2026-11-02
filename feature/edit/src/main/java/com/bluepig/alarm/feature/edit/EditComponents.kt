package com.bluepig.alarm.feature.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.component.Toggle
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Space
import com.bluepig.alarm.designsystem.R as DesignSystemR

/** 설정 묶음(surface). 묶음 사이는 화면 배경 8dp 띠 */
@Composable
internal fun SettingsGroup(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .background(AlarmTheme.colors.surface)
            .padding(vertical = 6.dp),
        content = content,
    )
}

/** v2/SettingsRow Type=Toggle. 라벨까지 행 전체가 스위치 */
@Composable
internal fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier
            .fillMaxWidth()
            .toggleable(checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
            .alpha(if (enabled) 1f else 0.4f)
            .padding(horizontal = Space.s24, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(Space.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, Modifier.weight(1f), style = AlarmTheme.typography.bodyMedium, color = AlarmTheme.colors.text)
        Toggle(checked, onCheckedChange = null)
    }
}

/** v2/SettingsRow Type=Value. 값이 비었으면 [placeholder]를 dim으로 */
@Composable
internal fun ValueRow(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AlarmTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Space.s24, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(Space.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = AlarmTheme.typography.bodyMedium, color = colors.text)
        Row(
            Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Space.s4, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                value.ifBlank { placeholder },
                Modifier.weight(1f, fill = false),
                style = AlarmTheme.typography.bodyRegular,
                color = if (value.isBlank()) colors.dim else colors.sub,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                painterResource(DesignSystemR.drawable.designsystem_ic_chevron),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colors.sub,
            )
        }
    }
}

/** v2/SettingsRow Type=Destructive */
@Composable
internal fun DestructiveRow(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        label,
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Space.s24, vertical = 14.dp),
        style = AlarmTheme.typography.bodyMedium,
        color = AlarmTheme.colors.danger,
    )
}

/** v2/DayButton. 켜짐 = accent 채움 */
@Composable
internal fun DayButton(label: String, checked: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = AlarmTheme.colors
    Box(
        modifier
            .size(38.dp)
            .then(
                if (checked) Modifier.background(colors.accent, CircleShape) else Modifier.border(1.dp, colors.dim, CircleShape),
            )
            .toggleable(checked, role = Role.Checkbox, onValueChange = { onClick() }),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = AlarmTheme.typography.labelStrong,
            color = if (checked) colors.onAccent else colors.sub,
            maxLines = 1,
        )
    }
}

/** v2/VolumeSlider. 진행 = accent, 손잡이 = knob + accent 테두리 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VolumeSlider(value: Int, max: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = AlarmTheme.colors
    Slider(
        value = value.toFloat(),
        onValueChange = { onValueChange(it.toInt()) },
        modifier = modifier.fillMaxWidth(),
        valueRange = 0f..max.toFloat(),
        steps = (max - 1).coerceAtLeast(0),
        thumb = {
            Box(
                Modifier
                    .size(20.dp)
                    .background(colors.knob, CircleShape)
                    .border(2.dp, colors.accent, CircleShape),
            )
        },
        track = { state ->
            SliderDefaults.Track(
                sliderState = state,
                modifier = Modifier.height(4.dp),
                colors = SliderDefaults.colors(
                    activeTrackColor = colors.accent,
                    inactiveTrackColor = colors.dim,
                    activeTickColor = colors.accent.copy(alpha = 0f),
                    inactiveTickColor = colors.dim.copy(alpha = 0f),
                ),
                thumbTrackGapSize = 0.dp,
                drawStopIndicator = null,
            )
        },
    )
}
