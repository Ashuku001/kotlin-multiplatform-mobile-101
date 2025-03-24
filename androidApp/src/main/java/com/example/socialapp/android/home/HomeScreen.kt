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
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.android.common.fakedata.sampleUsers
import com.example.socialapp.android.common.theming.SocialAppTheme
import com.example.socialapp.android.home.onboarding.OnBoardingSection
import com.example.socialapp.common.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onBoardingUiState: OnBoardingUiState,
    homeRefreshState: HomeRefreshState,
    postFeedUiState: PostFeedUiState,
    onUiAction: (HomeUiAction) -> Unit,
    onPostDetailNavigation: (Post) -> Unit,
    onProfileNavigation: (userId: Long) -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier,
        state = pullRefreshState,
        isRefreshing = postFeedUiState.isLoading || homeRefreshState.isRefreshing,
        onRefresh = { onUiAction(HomeUiAction.RefreshAction) }
    ) {
        LazyColumn (
            modifier = modifier.fillMaxSize()
        ) {
            if (onBoardingUiState.shouldShowOnBoarding) {
                item(key = "onBoardingSection") {
                    OnBoardingSection(
                        users = onBoardingUiState.followableUsers,
                        onUserClick = {onProfileNavigation(it.id)},
                        onFollowButtonClick = {
                            _, user ->
                            onUiAction(
                                HomeUiAction.FollowUserAction(
                                    user
                                )
                            )
                        }
                    ) {
                        onUiAction(HomeUiAction.RemoveOnBoardingAction)
                    }
                }
            }

            items(items = postFeedUiState.posts, key = { post -> post.postId}) { post ->
                PostListItem(
                    post = post,
                    onPostClick = { onPostDetailNavigation(it) },
                    onProfileClick = { onProfileNavigation(it.toLong())},
                    onLikeClick = { onUiAction(HomeUiAction.PostLikeAction(post.postId)) },
                    onCommentClick = {onPostDetailNavigation(post)}
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
                    followableUsers = sampleUsers.map { it.toFollowsUser() },
                    shouldShowOnBoarding = true,
                ),
                postFeedUiState = PostFeedUiState(
                    posts = samplePosts.map { it.toPost() },
                ),
                homeRefreshState = HomeRefreshState(
                    isRefreshing = false
                ),
                onProfileNavigation = {},
                onPostDetailNavigation = {},
                onUiAction = {}
            )
        }
    }
}