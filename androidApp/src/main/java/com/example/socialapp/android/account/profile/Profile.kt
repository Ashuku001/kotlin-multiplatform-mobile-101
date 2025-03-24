package com.example.socialapp.android.account.profile

import androidx.compose.runtime.Composable
import com.example.socialapp.android.destinations.EditProfileDestination
import com.example.socialapp.android.destinations.FollowersDestination
import com.example.socialapp.android.destinations.FollowingDestination
import com.example.socialapp.android.destinations.PostDetailDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination
fun Profile (
    navigator: DestinationsNavigator,
    userId: Long,
) {
    val viewModel: ProfileViewModel = koinViewModel()

    ProfileScreen(
        userInfoUiState = viewModel.userUiInfoUiState,
        profilePostUiState = viewModel.profilePostUiState,
        onButtonClick = {navigator.navigate(EditProfileDestination(userId))},
        onFollowersClick ={navigator.navigate(FollowersDestination(userId))},
        onFollowingClick= {navigator.navigate(FollowingDestination(userId))},
        onPostClick = {post -> navigator.navigate(PostDetailDestination(post.postId))},
        onProfileClick = {},
        onLikeClick = {},
        onCommentClick = {},
        fetchData = { viewModel.fetchProfile(userId)}
    )
}