package com.example.socialapp.android.post

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.socialapp.android.R
import com.example.socialapp.android.common.components.CommentListItem
import com.example.socialapp.android.common.components.PostListItem
import com.example.socialapp.android.common.fakedata.Comment
import com.example.socialapp.android.common.fakedata.sampleComments
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.android.common.theming.LargeSpacing
import com.example.socialapp.android.common.theming.SocialAppTheme

@Composable
fun PostDetailScreen (
    modifier: Modifier = Modifier,
    postUiState: PostUiState,
    commentsUiState: CommentsUiState,
    onCommentMoreIconClick: (Comment) -> Unit,
    onProfileClick: (Int) -> Unit,
    onAddCommentClick: () -> Unit,
    fetchData: () -> Unit
) {
    if (postUiState.isLoading && commentsUiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (postUiState.post != null) {
        LazyColumn (
            modifier = modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            item(key = "post_item") {
                PostListItem(
                    post = postUiState.post,
                    onPostClick = {},
                    onProfileClick =  onProfileClick,
                    onLikeClick = { TODO() },
                    onCommentClick = { TODO() },
                    isDetailScreen = true
                )
            }

            item(key = "comments_header_section") {
                CommentSectionHeader {onAddCommentClick() }
            }

            items(
                items = sampleComments,
                key = {comment -> comment.id}
            ) {
                Divider()
                CommentListItem(
                    comment = it,
                    onProfileClick = onProfileClick
                ) {
                    onCommentMoreIconClick(it)
                }
            }
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(LargeSpacing)
            ) {
                Text(
                    text = stringResource(id = R.string.loading_post_error),
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton (
                    onClick = fetchData
                ) {
                    Text(
                        text = stringResource(id = R.string.retry_button_text)
                    )
                }
            }
        }
    }

    // effect to fetch data
    LaunchedEffect(
        key1 = Unit,
        block = {
            fetchData()
        }
    )

}



@Composable
fun CommentSectionHeader(
    modifier: Modifier = Modifier,
    onAddComment: () -> Unit
) {
    Row  (
        modifier = modifier
            .fillMaxWidth()
            .padding(LargeSpacing),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(id = R.string.comments_label),
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedButton(onClick = onAddComment) {
            Text(text = stringResource(id = R.string.new_comment_button_label))
        }
    }
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PostDetailPreview() {
    SocialAppTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            PostDetailScreen(
                postUiState = PostUiState(
                    isLoading = false,
                    post = samplePosts.first()
                ),
                commentsUiState = CommentsUiState(
                    isLoading = false,
                    comments = sampleComments
                ),
                onProfileClick = { },
                onAddCommentClick = {},
                fetchData = {},
                onCommentMoreIconClick = {}
            )
        }
    }
}