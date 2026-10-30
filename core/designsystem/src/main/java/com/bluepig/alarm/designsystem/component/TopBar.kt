package com.bluepig.alarm.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.R
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Space

/** v2/TopBar: 왼쪽 닫기/뒤로(v2/IconButton, 터치 48·아이콘 24) + 가운데 제목 */
@Composable
fun TopBar(
    title: String,
    @DrawableRes navigationIcon: Int,
    navigationContentDescription: String,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = Space.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onNavigationClick, Modifier.size(48.dp)) {
            Icon(
                painterResource(navigationIcon),
                contentDescription = navigationContentDescription,
                tint = AlarmTheme.colors.text,
            )
        }
        Text(
            title,
            Modifier.weight(1f),
            style = AlarmTheme.typography.topbar,
            color = AlarmTheme.colors.text,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Spacer(Modifier.size(48.dp))
    }
}

@Preview
@Composable
private fun TopBarPreview() {
    AlarmTheme { TopBar("알람 수정", R.drawable.designsystem_ic_close, "닫기", {}) }
}
