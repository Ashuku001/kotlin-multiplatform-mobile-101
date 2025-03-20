package com.example.socialapp.android.home.onboarding

import com.example.socialapp.android.common.fakedata.FollowsUser

data class OnBoardingUiState(
    val isLoading: Boolean = false,
    val users: List<FollowsUser> = listOf(),
    val errorMessage: String? = null,
    val shouldShowOnBoarding: Boolean = false,
)