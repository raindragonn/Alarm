package com.bluepig.alarm.designsystem.component

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bluepig.alarm.designsystem.theme.AlarmTheme

/** 일회성 안내. 디자인에 없어 토큰으로 맞춤: 배경 text, 글자 background */
@Composable
fun AlarmSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState, modifier) {
        Snackbar(it, containerColor = AlarmTheme.colors.text, contentColor = AlarmTheme.colors.background)
    }
}
