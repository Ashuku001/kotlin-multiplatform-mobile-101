package com.example.socialapp.android.post.create

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination("create_post")
@Composable
fun CreatePost (
    navigator: DestinationsNavigator
) {
    val viewModel: CreatePostViewModel = koinViewModel()

    CreatePostScreen(
        modifier = Modifier,
        createPostUiState = viewModel.createPostUiState,
        onPostCreated = {navigator.popBackStack()},
        onUiAction = viewModel::onUiAction
    )
}