package com.bluepig.alarm.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Radius
import com.bluepig.alarm.designsystem.theme.Space

/** v2/NoticeCard. 화면 안에 두는 안내 (권한 안내, 제품 0017). [dismissLabel]이 있으면 닫기 버튼 */
@Composable
fun NoticeCard(
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String? = null,
    onDismiss: () -> Unit = {},
) {
    val colors = AlarmTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .background(colors.fill, RoundedCornerShape(Radius.md))
            .padding(start = Space.s16, end = Space.s4, top = Space.s16),
    ) {
        Text(title, Modifier.padding(end = Space.s12), style = AlarmTheme.typography.labelStrong, color = colors.text)
        Text(
            body,
            Modifier.padding(top = Space.s4, end = Space.s12),
            style = AlarmTheme.typography.bodySmall,
            color = colors.sub,
        )
        Row(Modifier.align(Alignment.End)) {
            if (dismissLabel != null) NoticeAction(dismissLabel, colors.sub, onDismiss)
            NoticeAction(actionLabel, colors.accent, onAction)
        }
    }
}

@Composable
private fun NoticeAction(text: String, color: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(Radius.sm), color = Color.Transparent, contentColor = color) {
        Box(Modifier.heightIn(min = 48.dp).padding(horizontal = Space.s12), contentAlignment = Alignment.Center) {
            Text(text, style = AlarmTheme.typography.labelStrong)
        }
    }
}

@Preview
@Composable
private fun NoticeCardPreview() {
    AlarmTheme {
        Column(Modifier.background(AlarmTheme.colors.background).padding(Space.s16)) {
            NoticeCard(
                title = "유튜브 알람이 영상으로 울리려면",
                body = "휴대폰을 쓰는 중에 울리면 영상 대신 기본 알람음이 나요. '다른 앱 위에 표시'를 허용해 주세요.",
                actionLabel = "허용하기",
                onAction = {},
                dismissLabel = "닫기",
            )
        }
    }
}
