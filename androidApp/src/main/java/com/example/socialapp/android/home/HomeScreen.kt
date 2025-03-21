package com.example.socialapp.android.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.socialapp.android.common.components.PostListItem
import com.example.socialapp.android.common.components.PullToRefreshBox
import com.example.socialapp.android.common.fakedata.FollowsUser
import com.example.socialapp.android.common.fakedata.Post
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.android.common.fakedata.sampleUsers
import com.example.socialapp.android.common.theming.SocialAppTheme
import com.example.socialapp.android.home.onboarding.OnBoardingSection
import com.example.socialapp.android.home.onboarding.OnBoardingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onBoardingUiState: OnBoardingUiState,
    postUiState: PostUiState,
    onPostClick: (Post) -> Unit,
    onProfileClick: (Int) -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    isDetailScreen: Boolean = false,
    onFollowButtonClick: (Boolean, FollowsUser) -> Unit,
    onBoardingFinish: () -> Unit,
    fetchData: () -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier,
        state = pullRefreshState,
        isRefreshing = postUiState.isLoading && onBoardingUiState.isLoading,
        onRefresh = fetchData
    ) {
        LazyColumn (
            modifier = modifier.fillMaxSize()
        ) {
            if (onBoardingUiState.shouldShowOnBoarding) {
                item(key = "onBoardingSection") {
                    OnBoardingSection(
                        users = onBoardingUiState.users,
                        onUserClick = { onProfileClick(it.id) },
                        onFollowButtonClick = onFollowButtonClick
                    ) {
                        onBoardingFinish()
                    }
                }
            }

            items(items = postUiState.posts, key = {post -> post.id}) {
                PostListItem(
                    post = it,
                    onPostClick = onPostClick,
                    onProfileClick = onProfileClick,
                    onLikeClick = onLikeClick,
                    onCommentClick = onCommentClick
                )
            }
        }

    }
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreview() {
    SocialAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(
                onBoardingUiState = OnBoardingUiState(
                    users = sampleUsers,
                    shouldShowOnBoarding = true,
                ),
                postUiState = PostUiState(
                    posts = samplePosts,
                ),
                onPostClick = {},
                onProfileClick = {  },
                onLikeClick = {},
                onCommentClick = {},
                onFollowButtonClick = {_, _ ->},
                onBoardingFinish = {},
                fetchData = {}
            )
        }
    }
}