package com.example.socialapp.android.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.socialapp.android.common.theming.DarkGray
import com.example.socialapp.android.common.theming.LargeSpacing
import com.example.socialapp.android.common.theming.LightGray
import com.example.socialapp.android.common.theming.MediumSpacing
import com.example.socialapp.android.R
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.android.common.theming.Black54
import com.example.socialapp.android.common.theming.ExtraLargeSpacing
import com.example.socialapp.android.common.theming.SocialAppTheme
import com.example.socialapp.android.common.util.toCurrentUrl
import com.example.socialapp.common.domain.model.Post

@Composable
fun PostListItem (
    modifier: Modifier = Modifier,
    post: Post,
    onPostClick: (Post) -> Unit,
    onProfileClick: (userId: Long) -> Unit,
    onLikeClick: (post: Post) -> Unit,
    onCommentClick: (post: Post) -> Unit,
    isDetailScreen: Boolean = false
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface
            )
            .clickable { onPostClick(post) }
            .padding(bottom = ExtraLargeSpacing)
    ) {
        PostHeader(
            name = post.userName,
            profileUrl = post.imageUrl,
            date = post.createdAt,
            onProfileClick = { onProfileClick(post.userId) },
        )

        AsyncImage(
            model = post.imageUrl.toCurrentUrl(),
            contentDescription = null,
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1f) ,
            placeholder = if (!isSystemInDarkTheme()) {
                painterResource(id = R.drawable.light_image_place_holder)
            } else {
                painterResource(id = R.drawable.dark_image_place_holder)
            },

        )

        PostFooter(
            post = post,
            likesCount = post.likesCount,
            commentCount = post.commentsCount,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
        )

        Text(
            text = post.caption,
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier.padding(horizontal = LargeSpacing),
            maxLines = if (isDetailScreen) {
                20
            } else {
                2
            },
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PostHeader (
    modifier: Modifier = Modifier,
    name: String,
    profileUrl: String,
    date: String,
    onProfileClick: () -> Unit
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = LargeSpacing,
                vertical = MediumSpacing
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MediumSpacing)
    ) {
        CircleImage(
            imageUrl = profileUrl,
            modifier = modifier.size(30.dp),
            onClick = onProfileClick
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(
                    color = if (isSystemInDarkTheme()) {
                        DarkGray
                    } else {
                        LightGray
                    }
                )
        )
        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                color = if (isSystemInDarkTheme()) {
                    DarkGray
                } else {
                    LightGray
                }
            ),
            modifier = modifier.weight(1f)
        )

        Icon(
            painter =  painterResource(id = R.drawable.round_more_horizontal),
            contentDescription = null,
            tint = if (isSystemInDarkTheme()) {
                DarkGray
            } else {
                LightGray
            }
        )
    }
}


@Composable
fun PostFooter (
    modifier: Modifier = Modifier,
    post: Post,
    likesCount: Int,
    commentCount: Int,
    isPostLiked: Boolean = false,
    onLikeClick: (Post) -> Unit,
    onCommentClick: (Post) -> Unit,
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MediumSpacing,
                vertical = 0.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { onLikeClick(post) }
        ) {
            Icon(
                painter = if(post.isLiked ) painterResource(id = R.drawable.like_icon_filled) else painterResource(id = R.drawable.like_icon_outlined),
                contentDescription = null,
                tint = if (post.isLiked) {
                    Color.Red
                } else {
                    DarkGray
                }

            )
        }

        Text(
            text = "$likesCount", style = MaterialTheme.typography.titleSmall.copy(
            fontSize = 18.sp
        ))

        IconButton(
            onClick = { onCommentClick(post) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.chat_icon_outlined),
                contentDescription = null,
                tint = if (isSystemInDarkTheme()) {
                    DarkGray
                } else {
                    LightGray
                }

            )
        }

        Text(
            text = "$commentCount", style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 18.sp
            )
        )
    }
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PostListItemPreview() {
    SocialAppTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            PostListItem(
                post = samplePosts.first().toDomainPost(),
                onPostClick = {},
                onProfileClick = {},
                onCommentClick = {},
                onLikeClick = {}
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PostHeaderPreview() {
    SocialAppTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            PostHeader(
                name = "Mr Ezra",
                profileUrl = "",
                date = "20 min",
                onProfileClick = {}
            )
        }
    }
}



@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PostLikesRowPreview() {
    SocialAppTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            PostFooter (
                post = samplePosts.first().toDomainPost(),
                likesCount = 12,
                commentCount = 2,
                isPostLiked = true,
                onLikeClick = {},
                onCommentClick = {}
            )
        }
    }
}
