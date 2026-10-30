package com.bluepig.alarm.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.theme.AlarmTheme

/** v2/Toggle. 켜짐 = accent, 꺼짐 = fill + dim 테두리 */
@Composable
fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AlarmTheme.colors
    val track by animateColorAsState(if (checked) colors.accent else colors.fill)
    val border by animateColorAsState(if (checked) Color.Transparent else colors.dim)
    val thumbOffset by animateDpAsState(if (checked) 24.dp else 4.dp)
    Box(
        modifier.toggleable(checked, role = Role.Switch, onValueChange = onCheckedChange),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(48.dp, 28.dp)
                .background(track, CircleShape)
                .border(1.dp, border, CircleShape),
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(thumbOffset.roundToPx(), 0) }
                    .size(20.dp)
                    .background(if (checked) colors.onAccent else colors.thumbOff, CircleShape)
                    .border(1.dp, if (checked) Color.Transparent else colors.dim, CircleShape),
            )
        }
    }
}

@Preview
@Composable
private fun TogglePreview() {
    AlarmTheme {
        Box(Modifier.background(AlarmTheme.colors.background).padding(16.dp)) {
            Toggle(checked = true, onCheckedChange = {})
            Toggle(checked = false, onCheckedChange = {}, Modifier.padding(start = 64.dp))
        }
    }
}
