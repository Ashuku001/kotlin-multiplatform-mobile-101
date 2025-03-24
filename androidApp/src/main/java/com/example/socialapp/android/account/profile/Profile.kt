package com.example.socialapp.android.account.profile

import androidx.compose.runtime.Composable
import com.example.socialapp.android.destinations.EditProfileDestination
import com.example.socialapp.android.destinations.FollowersDestination
import com.example.socialapp.android.destinations.FollowingDestination
import com.example.socialapp.android.destinations.PostDetailDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel


@Destination
@Composable
fun Profile (
    navigator: DestinationsNavigator,
    userId: Long,
) {
    val viewModel: ProfileViewModel = koinViewModel()

    ProfileScreen(
        userInfoUiState = viewModel.userUiInfoUiState,
        profilePostUiState = viewModel.profilePostUiState,
        onFollowButtonClick = {
            viewModel.userUiInfoUiState.profile?.let { profile ->
                if(profile.isOwnProfile) {
                    navigator.navigate(EditProfileDestination(userId))
                } else {
                    viewModel.onUiAction(ProfileUiAction.FollowUserAction(profile))
                }
            }
        },
        profileId = userId,
        onUiAction = { viewModel.onUiAction(it) },
        onFollowersScreenNavigation = {navigator.navigate(FollowersDestination(userId))},
        onFollowingScreenNavigation = {navigator.navigate(FollowingDestination(userId))},
        onPostDetailNavigation = {post -> navigator.navigate(PostDetailDestination(post.postId))}
    )
}