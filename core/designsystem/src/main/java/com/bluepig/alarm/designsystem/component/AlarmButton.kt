package com.bluepig.alarm.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.R
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Radius
import com.bluepig.alarm.designsystem.theme.Space

enum class ButtonStyle { Primary, Secondary, Text }

/** v2/Button. Primary = 저장·선택·알람 추가, Secondary = 미리보기·멈춤, Text = 보조 동작 */
@Composable
fun AlarmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    @DrawableRes icon: Int? = null,
) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    val (container, content, textStyle) = when (style) {
        ButtonStyle.Primary -> Triple(colors.accent, colors.onAccent, typography.button)
        ButtonStyle.Secondary -> Triple(colors.surface, colors.text, typography.bodyStrong)
        ButtonStyle.Text -> Triple(Color.Transparent, colors.sub, typography.labelMedium)
    }
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = if (style == ButtonStyle.Text) 48.dp else 56.dp),
        shape = RoundedCornerShape(if (style == ButtonStyle.Text) Radius.sm else Radius.xl),
        color = container,
        contentColor = content,
        border = if (style == ButtonStyle.Secondary) BorderStroke(1.dp, colors.dim) else null,
    ) {
        Row(
            Modifier.padding(PaddingValues(horizontal = Space.s24)),
            horizontalArrangement = Arrangement.spacedBy(Space.s8, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) Icon(painterResource(icon), contentDescription = null, Modifier.size(20.dp))
            Text(text, style = textStyle)
        }
    }
}

@Preview
@Composable
private fun AlarmButtonPreview() {
    AlarmTheme {
        Column(
            Modifier.padding(Space.s16),
            verticalArrangement = Arrangement.spacedBy(Space.s8),
        ) {
            AlarmButton("알람 추가", {}, icon = R.drawable.designsystem_ic_add)
            AlarmButton("미리보기", {}, style = ButtonStyle.Secondary, icon = R.drawable.designsystem_ic_play)
            AlarmButton("연동하지 않을게요", {}, style = ButtonStyle.Text)
        }
    }
}
