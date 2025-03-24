package com.example.socialapp.android.home

import androidx.compose.runtime.Composable
import com.example.socialapp.android.common.fakedata.Profile
import com.example.socialapp.android.destinations.PostDetailDestination
import com.example.socialapp.android.destinations.ProfileDestination
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
        postFeedUiState = viewModel.postFeedUiState,
        homeRefreshState = viewModel.homeRefreshState,
        onUiAction = {viewModel.onUiAction(it)}, // the it is automatically generated
        onProfileNavigation = {navigator.navigate(ProfileDestination.route)},
        onPostDetailNavigation = {navigator.navigate(PostDetailDestination.route)},
    )

}