package com.example.socialapp.android.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.android.common.fakedata.samplePosts
import kotlinx.coroutines.launch
import com.example.socialapp.common.domain.model.FollowsUser
import com.example.socialapp.follows.domain.usecase.GetFollowableUsersUseCase
import com.example.socialapp.common.util.Result
import com.example.socialapp.common.domain.model.Post
import com.example.socialapp.follows.domain.usecase.FollowOrUnfollowUseCase


class HomeScreenViewModel (
    private val getFollowableUsersUseCase: GetFollowableUsersUseCase,
    private val followOrUnfollowUseCase: FollowOrUnfollowUseCase
): ViewModel() {
    private val _postFeedUiState: MutableState<PostFeedUiState> = mutableStateOf(PostFeedUiState())
    val postFeedUiState: PostFeedUiState get() = _postFeedUiState.value

    private val _homeRefreshState: MutableState<HomeRefreshState> = mutableStateOf(HomeRefreshState())
    val homeRefreshState: HomeRefreshState get() = _homeRefreshState.value

    private val _onBoardingUiState: MutableState<OnBoardingUiState> = mutableStateOf(OnBoardingUiState())
    val onBoardingUiState: OnBoardingUiState get() = _onBoardingUiState.value

    init {
        fetchData()
    }

    fun fetchData(){
        _homeRefreshState.value = _homeRefreshState.value.copy(isRefreshing = true)

        viewModelScope.launch {

            val users = getFollowableUsersUseCase()
            handleOnBoardingResult(users)
            _postFeedUiState.value = _postFeedUiState.value.copy(
                    isLoading = false,
                    posts = samplePosts.map { it.toPost() },
                )
//            _homeRefreshState.value = _homeRefreshState.value.copy(isRefreshing = false)

        }
    }

    private fun handleOnBoardingResult(result: Result<List<FollowsUser>>) {
        when (result) {
            is Result.Error -> Unit
            is Result.Success -> {
                result.data?.let { followsUsers ->
                    _onBoardingUiState.value = _onBoardingUiState.value.copy(
                        shouldShowOnBoarding = followsUsers.isNotEmpty(),
                        followableUsers = followsUsers
                    )
                }
            }
        }
    }

    private fun followUser(followsUser: FollowsUser) {
        viewModelScope.launch {
            val result = followOrUnfollowUseCase(
                followedUserId = followsUser.id,
                shouldFollow = !followsUser.isFollowing
            )

            // toggle the button
            _onBoardingUiState.value = _onBoardingUiState.value.copy(
                followableUsers =  _onBoardingUiState.value.followableUsers.map {
                    if(it.id == followsUser.id) {
                        it.copy(isFollowing = !followsUser.isFollowing)
                    } else {
                        it
                    }
                }
            )

            when (result) {
                is Result.Error ->  _onBoardingUiState.value = _onBoardingUiState.value.copy(
                    followableUsers =  _onBoardingUiState.value.followableUsers.map {
                        if(it.id == followsUser.id) {
                            it.copy(isFollowing = followsUser.isFollowing)
                        } else {
                            it
                        }
                    }
                )

                is Result.Success -> Unit
            }
        }
    }

    private fun dismissOnboarding() {
        val hasFollowing = onBoardingUiState.followableUsers.any() {it.isFollowing}

        if(!hasFollowing) {
            TODO("tell user they need ro follow at least 1 person")
        } else {
            _onBoardingUiState.value = _onBoardingUiState.value.copy(
                shouldShowOnBoarding = false,
                followableUsers = emptyList()
            )
            fetchData()
        }
    }

    fun onUiAction(uiAction: HomeUiAction) {
        when (uiAction) {
            is HomeUiAction.FollowUserAction -> followUser(uiAction.user)
            HomeUiAction.LoadMorePostsAction -> Unit
            is HomeUiAction.PostLikeAction -> Unit
            HomeUiAction.RefreshAction -> fetchData()
            HomeUiAction.RemoveOnBoardingAction -> dismissOnboarding()
        }
    }
}


data class HomeRefreshState(
    val isRefreshing: Boolean = false,
    val refreshErrorMessage: String? = null
)

data class PostFeedUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = listOf(),
    val errorMessage: String? = null
)

data class OnBoardingUiState(
    val followableUsers: List<FollowsUser> = listOf(),
    val shouldShowOnBoarding: Boolean = false,
)

// sealed interface for all callbacks to HomeScreen Composable
sealed interface HomeUiAction {
    data class FollowUserAction(val user: FollowsUser): HomeUiAction
    data class PostLikeAction(val postId: Long): HomeUiAction
    data object RemoveOnBoardingAction: HomeUiAction
    data object RefreshAction: HomeUiAction
    data object LoadMorePostsAction: HomeUiAction
}