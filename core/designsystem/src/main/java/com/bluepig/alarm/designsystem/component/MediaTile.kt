package com.bluepig.alarm.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.bluepig.alarm.designsystem.R
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Space

enum class MediaTileType { Song, Ringtone, YouTube }

/**
 * v2/MediaTile: 알람음 종류 타일 (제품 0002). 썸네일은 있을 때만 그리고, 못 받으면 종류 아이콘 (U6).
 * 크기를 바꾸면 아이콘이 비율대로 커짐, 모서리는 48dp일 때 12dp
 */
@Composable
fun MediaTile(
    type: MediaTileType,
    modifier: Modifier = Modifier,
    thumbnail: String? = null,
    size: Dp = 48.dp,
) {
    val shape = RoundedCornerShape(size / 4)
    Box(modifier.size(size).clip(shape)) {
        val fallback = @Composable { TypeIcon(type, size) }
        if (thumbnail.isNullOrBlank() || type == MediaTileType.Ringtone) {
            fallback()
        } else {
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
}

@Composable
private fun TypeIcon(type: MediaTileType, size: Dp) {
    val colors = AlarmTheme.colors
    when (type) {
        MediaTileType.YouTube -> Box(
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF6B6F7A), Color(0xFF2A2C33)))),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painterResource(R.drawable.designsystem_ic_youtube),
                contentDescription = null,
                modifier = Modifier.size(size * 20 / 48, size * 14 / 48),
            )
        }

        else -> Box(Modifier.fillMaxSize().background(colors.fill), contentAlignment = Alignment.Center) {
            Icon(
                painterResource(type.icon),
                contentDescription = null,
                modifier = Modifier.size(size * 22 / 48),
                tint = colors.sub,
            )
        }
    }
}

@get:DrawableRes
private val MediaTileType.icon
    get() = if (this == MediaTileType.Ringtone) R.drawable.designsystem_ic_ringtone else R.drawable.designsystem_ic_song

@Preview
@Composable
private fun MediaTilePreview() {
    AlarmTheme {
        Row(
            Modifier.background(AlarmTheme.colors.background).padding(Space.s16),
            horizontalArrangement = Arrangement.spacedBy(Space.s16),
        ) {
            MediaTileType.entries.forEach { MediaTile(it) }
            MediaTile(MediaTileType.Song, size = 104.dp)
        }
    }
}
