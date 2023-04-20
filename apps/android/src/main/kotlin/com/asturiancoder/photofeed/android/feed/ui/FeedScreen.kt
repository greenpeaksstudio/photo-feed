package com.asturiancoder.photofeed.android.feed.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asturiancoder.photofeed.android.R
import com.asturiancoder.photofeed.android.ui.theme.PhotoFeedTheme

data class FeedUiState(
    val feed: List<FeedPhotoUiState>,
)

data class FeedPhotoUiState(
    val authorName: String,
    val location: String?,
    val description: String?,
)

@Composable
fun FeedScreen(
    feedUiState: FeedUiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .fillMaxSize(),
    ) {
        items(feedUiState.feed, { it.hashCode() }) { item ->
            FeedPhotoItem(item)
        }
    }
}

@Composable
private fun FeedPhotoItem(item: FeedPhotoUiState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = item.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(48.dp)
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
            )
            Text(
                text = item.authorName,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = item.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .fillMaxSize()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.primaryContainer),
        )
        item.location?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                )
                Text(text = item.location)
            }
        }
        item.description?.let {
            Text(
                text = item.description,
                modifier = Modifier.padding(start = if (item.location != null) 24.dp else 0.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FeedScreenPreview() {
    PhotoFeedTheme {
        val feed = List(10) {
            FeedPhotoUiState(
                "Author $it",
                null,
                "A long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long description $it",
            )
        }
        val state = FeedUiState(feed = feed)
        FeedScreen(feedUiState = state)
    }
}
