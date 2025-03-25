package com.example.socialapp.android.account.profile

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialapp.android.R
import com.example.socialapp.android.common.components.CircleImage
import com.example.socialapp.android.common.components.FollowsButton
import com.example.socialapp.android.common.components.PostListItem
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.android.common.fakedata.sampleProfiles
import com.example.socialapp.android.common.theming.LargeSpacing
import com.example.socialapp.android.common.theming.MediumSpacing
import com.example.socialapp.android.common.theming.SmallSpacing
import com.example.socialapp.android.common.theming.SocialAppTheme
import com.example.socialapp.android.common.util.Constants
import com.example.socialapp.android.common.util.toCurrentUrl
import com.example.socialapp.common.domain.model.Post

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userInfoUiState: UserInfoUiState,
    profilePostUiState: ProfilePostUiState,
    profileId: Long,
    onUiAction: (ProfileUiAction) -> Unit,
    onFollowButtonClick: () -> Unit,
    onFollowersScreenNavigation: () -> Unit,
    onFollowingScreenNavigation: () -> Unit,
    onPostDetailNavigation: (post: Post) -> Unit
) {

    val listState = rememberLazyListState()

    val shouldFetchMorePosts by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.last()

                (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount)
            }
        }
    }

    if ( userInfoUiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn (
            modifier = modifier.fillMaxSize(),
            state = listState
        ) {
            item(key = "header_section") {
                ProfileHeaderSection(
                    imageUrl = userInfoUiState.profile?.imageUrl,
                    name = userInfoUiState.profile?.name ?: "",
                    bio = userInfoUiState.profile?.bio ?: "",
                    isFollowing = userInfoUiState.profile?.isFollowing ?: false,
                    isCurrentUser = userInfoUiState.profile?.isOwnProfile ?: false,
                    followersCount = userInfoUiState.profile?.followersCount ?: 0,
                    followingCount = userInfoUiState.profile?.followingCount ?: 0,
                    onButtonClick = onFollowButtonClick,
                    onFollowersClick = onFollowersScreenNavigation,
                    onFollowingClick = onFollowingScreenNavigation,
                )
            }

            items (
                items = profilePostUiState.posts,
                key = {post -> post.postId}
            ) {
                PostListItem(
                    post = it,
                    onPostClick = onPostDetailNavigation,
                    onProfileClick = {  },
                    onLikeClick = {post -> onUiAction(ProfileUiAction.PostLikeAction(post))},
                    onCommentClick = {  }
                )
            }

            println("is loading ${profilePostUiState.isLoading}")

            if(profilePostUiState.isLoading) {
                item(key = Constants.LOADING_MORE_ITEM_KEY) {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(vertical = MediumSpacing, horizontal = LargeSpacing),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

        }
    }

    LaunchedEffect(
        key1 = Unit,
    ) {
        onUiAction(ProfileUiAction.FetchProfileAction(profileId = profileId))
    }

    LaunchedEffect(
        key1 = shouldFetchMorePosts,
    ) {
        if(shouldFetchMorePosts && !profilePostUiState.endReached){
            onUiAction(ProfileUiAction.LoadMorePostsAction)
        }
    }


}


@Composable
fun ProfileHeaderSection(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    name: String,
    bio: String,
    followersCount: Int,
    followingCount: Int,
    isCurrentUser: Boolean = false,
    isFollowing: Boolean = false,
    onButtonClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit,
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(all = LargeSpacing)
            .padding(bottom = MediumSpacing)
    ) {
        CircleImage (
            modifier = modifier.size(90.dp),
            imageUrl = imageUrl?.toCurrentUrl(),
            onClick = {}
        )

        Spacer(modifier = modifier.height(SmallSpacing))

        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text (
            text = bio,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = modifier.height(MediumSpacing))

        Row (
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (
                modifier = modifier.weight(1f),

            ) {
                FollowsText(
                    count = followersCount,
                    text = R.string.followers_text,
                    onClick = onFollowersClick
                )

                Spacer(
                    modifier = modifier.width(MediumSpacing)
                )

                FollowsText(
                    count = followingCount,
                    text = R.string.following_text,
                    onClick = onFollowingClick
                )
            }
            FollowsButton(
                text = when {
                    isCurrentUser -> R.string.edit_profile_label
                    isFollowing -> R.string.unfollow_text_label
                    else -> R.string.follow_text_label
                },
                onClick = onButtonClick,
                modifier =  modifier
                    .heightIn(30.dp)
                    .widthIn(100.dp),
                isOutlined = isCurrentUser || isFollowing

            )
        }

    }
}

@Composable
fun FollowsText(
    modifier: Modifier = Modifier,
    count: Int,
    @StringRes text: Int,
    onClick: () -> Unit
){
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            ){
                append(text = "$count ")
            }
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.54f),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            ){
                append(text = stringResource(id = text))
            }
        },
        modifier = modifier.clickable { onClick() }
    )
}

@Preview
@Composable
fun ProfileHeaderPreview() {
    SocialAppTheme {
        Surface (color = MaterialTheme.colorScheme.surface) {
            ProfileHeaderSection (
                imageUrl = "",
                name = "Mr Ezra",
                bio = "Hey There welcome to My Ezra Application",
                followersCount = 9,
                followingCount = 19,
                onButtonClick = { TODO() },
                onFollowersClick = { TODO() },
                onFollowingClick = { TODO() }
            )
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    SocialAppTheme {
        Surface (color = MaterialTheme.colorScheme.surface) {
            ProfileScreen(
                userInfoUiState = UserInfoUiState(
                    isLoading = false,
                    profile = sampleProfiles.first().toDomainProfile()
                ),
                profilePostUiState = ProfilePostUiState(
                    isLoading = false,
                    posts = samplePosts.map { it.toDomainPost() }
                ),
                profileId = 12,
                onUiAction = {},
                onFollowButtonClick = {},
                onFollowersScreenNavigation = { },
                onFollowingScreenNavigation = {},
                onPostDetailNavigation = {}
            )
        }
    }
}