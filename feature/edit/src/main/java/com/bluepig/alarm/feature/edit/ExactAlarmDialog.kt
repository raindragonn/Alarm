package com.bluepig.alarm.feature.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.window.Dialog
import com.bluepig.alarm.designsystem.component.AlarmButton
import com.bluepig.alarm.designsystem.component.ButtonStyle
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Radius
import com.bluepig.alarm.designsystem.theme.Space

/** API 31·32에서 정확한 알람 권한이 꺼져 있을 때 (C6). 메모 다이얼로그와 같은 틀 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ExactAlarmDialog(onOpenSetting: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(AlarmTheme.colors.surface, RoundedCornerShape(Radius.xl))
                .padding(Space.s24)
                .semantics { testTagsAsResourceId = true },
            verticalArrangement = Arrangement.spacedBy(Space.s16),
        ) {
            Text(stringResource(R.string.edit_exact_alarm_title), style = AlarmTheme.typography.titleSmall, color = AlarmTheme.colors.text)
            Text(stringResource(R.string.edit_exact_alarm_body), style = AlarmTheme.typography.bodySmall, color = AlarmTheme.colors.sub)
            Column {
                AlarmButton(stringResource(R.string.edit_exact_alarm_open), onOpenSetting, Modifier.fillMaxWidth())
                AlarmButton(stringResource(R.string.edit_cancel), onDismiss, Modifier.fillMaxWidth(), style = ButtonStyle.Text)
            }
        }
    }
}
