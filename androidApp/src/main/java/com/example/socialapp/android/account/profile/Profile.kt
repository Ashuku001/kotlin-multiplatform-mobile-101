package com.example.socialapp.android.account.profile

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination
fun Profile (
    navigator: DestinationsNavigator,
    userId: Int,
) {
    val viewModel: ProfileViewModel = koinViewModel()

    ProfileScreen(
        userInfoUiState = viewModel.userUiInfoUiState,
        profilePostUiState = viewModel.profilePostUiState,
        onButtonClick = {},
        onFollowersClick ={},
        onFollowingClick= {},
        onPostClick = {},
        onProfileClick = {},
        onLikeClick = {},
        onCommentClick = {},
        fetchData = { viewModel.fetchProfile(userId)}
    )
}