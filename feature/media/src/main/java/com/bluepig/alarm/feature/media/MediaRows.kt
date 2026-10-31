package com.bluepig.alarm.feature.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.bluepig.alarm.designsystem.component.MediaTile
import com.bluepig.alarm.designsystem.component.MediaTileType
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Radius
import com.bluepig.alarm.designsystem.theme.Space

/** v2/MediaRow. 노래 = 타일 48 + 한 줄, 벨소리 = 타일 40 + 한 줄, 유튜브 = 16:9 썸네일 + 두 줄 */
@Composable
internal fun SongRow(title: String, thumbnail: String, onClick: () -> Unit) = MediaRowFrame(onClick, Space.s12) {
    MediaTile(MediaTileType.Song, thumbnail = thumbnail)
    RowTitle(title, AlarmTheme.typography.bodyStrong, maxLines = 1)
}

@Composable
internal fun RingtoneRow(title: String, onClick: () -> Unit) = MediaRowFrame(onClick, Space.s8) {
    MediaTile(MediaTileType.Ringtone, size = 40.dp)
    RowTitle(title, AlarmTheme.typography.labelMedium, maxLines = 1)
}

@Composable
internal fun YoutubeRow(title: String, thumbnail: String, onClick: () -> Unit) = MediaRowFrame(onClick, Space.s12) {
    YoutubeThumbnail(thumbnail, Modifier.size(107.dp, 60.dp))
    RowTitle(title, AlarmTheme.typography.labelMedium, maxLines = 2)
}

@Composable
internal fun YoutubeThumbnail(thumbnail: String, modifier: Modifier = Modifier) {
    val fallback = @Composable { MediaTile(MediaTileType.YouTube, size = 107.dp, cornerRadius = 0.dp) }
    Box(modifier.clip(RoundedCornerShape(Radius.sm))) {
        SubcomposeAsyncImage(
            model = thumbnail,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = { fallback() },
            error = { fallback() },
        )
    }
}

@Composable
private fun MediaRowFrame(
    onClick: () -> Unit,
    vertical: Dp,
    content: @Composable RowScope.() -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = Space.s24, vertical = vertical),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
        HorizontalDivider(color = AlarmTheme.colors.line)
    }
}

@Composable
private fun RowScope.RowTitle(
    title: String,
    style: TextStyle,
    maxLines: Int,
) {
    Text(
        title,
        Modifier.weight(1f),
        style = style,
        color = AlarmTheme.colors.text,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

/** 결과가 없거나 검색 전·실패일 때 가운데 안내 (디자인: 검색 결과 없음) */
@Composable
internal fun CenterMessage(title: String, body: String?, modifier: Modifier = Modifier, action: (@Composable () -> Unit)? = null) {
    Column(
        modifier.padding(horizontal = Space.s32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Space.s4),
    ) {
        Text(
            title,
            style = AlarmTheme.typography.titleXsmall,
            color = AlarmTheme.colors.text,
            textAlign = TextAlign.Center,
        )
        body?.let {
            Text(
                it,
                style = AlarmTheme.typography.captionRegular,
                color = AlarmTheme.colors.sub,
                textAlign = TextAlign.Center,
            )
        }
        action?.invoke()
    }
}
