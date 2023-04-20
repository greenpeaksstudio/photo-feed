package com.asturiancoder.photofeed.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.asturiancoder.photofeed.android.feed.ui.FeedPhotoUiState
import com.asturiancoder.photofeed.android.feed.ui.FeedScreen
import com.asturiancoder.photofeed.android.feed.ui.FeedUiState
import com.asturiancoder.photofeed.android.ui.theme.PhotoFeedTheme

class PhotoFeedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PhotoFeedTheme {
                val feed = getFeed()
                val state = FeedUiState(feed = feed)
                FeedScreen(feedUiState = state)
            }
        }
    }

    private fun getFeed() = List(10) {
        val location = if (it % 3 == 0) "A location $it" else null
        val description = if (it % 2 == 0) "A description $it" else null

        FeedPhotoUiState(authorName = "Author $it", location = location, description = description)
    }
}
