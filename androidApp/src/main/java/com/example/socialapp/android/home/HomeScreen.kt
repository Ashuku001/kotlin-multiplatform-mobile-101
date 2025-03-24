package com.example.socialapp.android.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.socialapp.android.common.components.PostListItem
import com.example.socialapp.android.common.components.PullToRefreshBox
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.android.common.fakedata.sampleUsers
import com.example.socialapp.android.common.theming.LargeSpacing
import com.example.socialapp.android.common.theming.MediumSpacing
import com.example.socialapp.android.common.theming.SocialAppTheme
import com.example.socialapp.android.common.util.Constants
import com.example.socialapp.android.home.onboarding.OnBoardingSection
import com.example.socialapp.common.domain.model.Post
import kotlin.random.Random

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

    val listState = rememberLazyListState() // list column state

    // a derived state
    val shouldFetchMorePosts by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleInfo = layoutInfo.visibleItemsInfo

            if(layoutInfo.totalItemsCount == 0) { // no items in the colum
                false
            } else {
                val lastVisibleItem = visibleInfo.last()
                (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount) // check last visible item is last item
            }
        }
    }

    PullToRefreshBox(
        modifier = Modifier,
        state = pullRefreshState,
        isRefreshing = homeRefreshState.isRefreshing,
        onRefresh = { onUiAction(HomeUiAction.RefreshAction) }

    ) {
        LazyColumn (
            modifier = modifier.fillMaxSize(),
            state = listState
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

            // TODO check pagination it is duplicating
            println("POSTS ${postFeedUiState.posts.map{it.postId}}")

            items(items = postFeedUiState.posts, key = { post ->  "${post.postId}_${Random.nextInt()}"  }) { post ->
                PostListItem(
                    post = post,
                    onPostClick = { onPostDetailNavigation(it) },
                    onProfileClick = { onProfileNavigation(it)},
                    onLikeClick = { onUiAction(HomeUiAction.PostLikeAction(post)) },
                    onCommentClick = {onPostDetailNavigation(post)}
                )
            }

            if(postFeedUiState.isLoading && postFeedUiState.posts.isNotEmpty()) {
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

    LaunchedEffect(key1 = shouldFetchMorePosts, ) {
        if (shouldFetchMorePosts && !postFeedUiState.endReached) {
            onUiAction(HomeUiAction.LoadMorePostsAction)
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
                    posts = samplePosts.map { it.toDomainPost() },
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