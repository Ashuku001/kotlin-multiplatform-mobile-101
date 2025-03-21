package com.example.socialapp.android.home

import androidx.compose.runtime.Composable
import com.example.socialapp.android.common.fakedata.Post
import com.example.socialapp.android.destinations.PostDetailDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination(start = true)
@Composable
fun Home (
    navigator: DestinationsNavigator
) {
    val viewModel: HomeScreenViewModel = koinViewModel()

    HomeScreen(
        onBoardingUiState = viewModel.onBoardingUiState,
        postUiState = viewModel.postUiState,
        onPostClick = {
            navigator.navigate(PostDetailDestination(it.id))
        },
        onProfileClick = {},
        onLikeClick = { },
        onCommentClick = { },
        isDetailScreen = false,
        onFollowButtonClick = {_, _ ->},
        onBoardingFinish = {},
        fetchData = {viewModel.fetchData()}
    )

}