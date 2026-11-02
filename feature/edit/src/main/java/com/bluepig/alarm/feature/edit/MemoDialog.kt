package com.bluepig.alarm.feature.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bluepig.alarm.designsystem.component.AlarmButton
import com.bluepig.alarm.designsystem.component.ButtonStyle
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Radius
import com.bluepig.alarm.designsystem.theme.Space

/** 메모 입력. 디자인에 없어 유튜브 연동 다이얼로그와 같은 틀(제목 + 내용 + Primary·Text 버튼)로 맞춤 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun MemoDialog(initial: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    var memo by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initial, TextRange(initial.length)))
    }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(colors.surface, RoundedCornerShape(Radius.xl))
                .padding(Space.s24)
                .semantics { testTagsAsResourceId = true },
            verticalArrangement = Arrangement.spacedBy(Space.s16),
        ) {
            Text(stringResource(R.string.edit_memo), style = typography.titleSmall, color = colors.text)
            BasicTextField(
                value = memo,
                onValueChange = { memo = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .testTag("et_memo"),
                textStyle = typography.bodyRegular.copy(color = colors.text),
                cursorBrush = SolidColor(colors.accent),
                maxLines = 3,
                decorationBox = { field ->
                    Box(
                        Modifier
                            .background(colors.fill, RoundedCornerShape(Radius.md))
                            .padding(horizontal = Space.s16, vertical = 14.dp),
                    ) {
                        if (memo.text.isEmpty()) {
                            Text(stringResource(R.string.edit_memo_hint), style = typography.bodyRegular, color = colors.dim)
                        }
                        field()
                    }
                },
            )
            Column {
                AlarmButton(
                    stringResource(R.string.edit_confirm),
                    onClick = { onConfirm(memo.text.trim()) },
                    modifier = Modifier.fillMaxWidth(),
                )
                AlarmButton(
                    stringResource(R.string.edit_cancel),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    style = ButtonStyle.Text,
                )
            }
        }
    }
}
