package com.example.socialapp.android.account.follows

import androidx.compose.runtime.Composable
import com.example.socialapp.android.destinations.ProfileDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination
fun Following (
    navigator: DestinationsNavigator,
    userId: Int
) {
    val viewModel: FollowsViewModel = koinViewModel()

    FollowsScreen(
        uiState = viewModel.uiState,
        fetchFollows = {viewModel.fetchFollows(userId, 1)},
        onItemClick = {navigator.navigate(ProfileDestination(it))}
    )
}