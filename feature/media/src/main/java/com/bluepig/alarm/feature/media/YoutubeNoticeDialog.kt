package com.bluepig.alarm.feature.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bluepig.alarm.designsystem.component.AlarmButton
import com.bluepig.alarm.designsystem.component.ButtonStyle
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Radius
import com.bluepig.alarm.designsystem.theme.Space
import com.bluepig.alarm.designsystem.R as DesignSystemR

/** 유튜브 연동 안내 다이얼로그 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun YoutubeNoticeDialog(onAgree: () -> Unit, onDismiss: () -> Unit) {
    val colors = AlarmTheme.colors
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .width(312.dp)
                .background(colors.surface, RoundedCornerShape(Radius.xl))
                .padding(start = Space.s24, end = Space.s24, top = 28.dp, bottom = Space.s16)
                .semantics { testTagsAsResourceId = true },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(painterResource(DesignSystemR.drawable.designsystem_ic_youtube), null, Modifier.size(44.dp, 31.dp))
            Spacer(Modifier.height(Space.s16))
            Text(
                stringResource(R.string.media_youtube_notice_title),
                style = AlarmTheme.typography.titleSmall,
                color = colors.text,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Space.s8))
            Text(
                stringResource(R.string.media_youtube_notice_body),
                style = AlarmTheme.typography.bodySmall,
                color = colors.sub,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Space.s24))
            AlarmButton(stringResource(R.string.media_youtube_notice_agree), onAgree, Modifier.fillMaxWidth())
            AlarmButton(
                stringResource(R.string.media_youtube_notice_disagree),
                onDismiss,
                Modifier.fillMaxWidth(),
                style = ButtonStyle.Text,
            )
        }
    }
}
