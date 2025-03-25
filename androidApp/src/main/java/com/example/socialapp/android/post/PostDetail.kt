package com.example.socialapp.android.post

import androidx.compose.runtime.Composable
import com.example.socialapp.android.destinations.ProfileDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination
fun PostDetail(
    navigator: DestinationsNavigator,
    postId: Long,
) {
    val viewModel: PostDetailScreenViewModel = koinViewModel()

    PostDetailScreen(
        postUiState = viewModel.postUiState,
        commentsUiState = viewModel.commentsUiState,
        onProfileNavigation = { userId -> navigator.navigate(ProfileDestination(userId))},
        postId = postId,
        onUiAction = viewModel::onUiAction
    )
}