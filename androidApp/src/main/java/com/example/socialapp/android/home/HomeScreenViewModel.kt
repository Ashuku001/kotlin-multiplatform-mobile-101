package com.example.socialapp.android.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.socialapp.account.domain.model.Profile
import com.example.socialapp.android.common.util.Constants
import com.example.socialapp.android.common.util.DefaultPagingManager
import com.example.socialapp.android.common.util.Event
import com.example.socialapp.android.common.util.EventBus
import com.example.socialapp.android.common.util.PagingManager
import kotlinx.coroutines.launch
import com.example.socialapp.common.domain.model.FollowsUser
import com.example.socialapp.follows.domain.usecase.GetFollowableUsersUseCase
import com.example.socialapp.common.util.Result
import com.example.socialapp.common.domain.model.Post
import com.example.socialapp.follows.domain.usecase.FollowOrUnfollowUseCase
import com.example.socialapp.post.domain.usecase.GetPostsUseCase
import com.example.socialapp.post.domain.usecase.LikeOrUnlikePostUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class HomeScreenViewModel (
    private val getFollowableUsersUseCase: GetFollowableUsersUseCase,
    private val followOrUnfollowUseCase: FollowOrUnfollowUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val likePostsUseCase: LikeOrUnlikePostUseCase
): ViewModel() {
    private val _postFeedUiState: MutableState<PostFeedUiState> = mutableStateOf(PostFeedUiState())
    val postFeedUiState: PostFeedUiState get() = _postFeedUiState.value

    private val _homeRefreshState: MutableState<HomeRefreshState> = mutableStateOf(HomeRefreshState())
    val homeRefreshState: HomeRefreshState get() = _homeRefreshState.value

    private val _onBoardingUiState: MutableState<OnBoardingUiState> = mutableStateOf(OnBoardingUiState())
    val onBoardingUiState: OnBoardingUiState get() = _onBoardingUiState.value

    private val pagingManager by lazy {createPagingManager()}

    init {
        fetchData()

        // listen to updates of posts and update the home screen state
        EventBus.events.onEach {
            when (it) {
                is Event.PostUpdated -> updatePost(it.post)
                is Event.ProfileUpdated -> updateCurrentUserProfile(it.profile)
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchData(){
        _homeRefreshState.value = _homeRefreshState.value.copy(isRefreshing = true)

        // viewModel has a supervisor scope so if deferred getFollowableUsersUseCase fail it wont affect load first page posts
        viewModelScope.launch {
            // suspend fun to get users
            // val users = getFollowableUsersUseCase()

            // deferred fun: will run at the end of this fun execution
            val onBoardingDeferred = async {
                getFollowableUsersUseCase()
            }

            pagingManager.apply {
                reset() // to first page
                loadItems() // load items
            }

            handleOnBoardingResult(onBoardingDeferred.await())
            _homeRefreshState.value = _homeRefreshState.value.copy(isRefreshing = false)

        }
    }

    private fun createPagingManager(): PagingManager<Post> {
        return DefaultPagingManager(
            onRequest = {page ->
                getPostsUseCase(page, Constants.DEFAULT_REQUEST_PAGE_SIZE)
            },
            onSuccess = {posts, page ->
                _postFeedUiState.value = if(posts.isEmpty()) { // no more posts available
                    _postFeedUiState.value.copy(endReached = true)
                } else {
                    // on first landing there are some posts already loaded
                    if (page == Constants.INITIAL_PAGE_NUMBER) {
                        _postFeedUiState.value.copy(
                            posts = emptyList()
                        )
                    }
                    _postFeedUiState.value.copy(
                        posts = _postFeedUiState.value.posts + posts,
                        endReached = posts.size < Constants.DEFAULT_REQUEST_PAGE_SIZE // reached end if 10 posts were not returned
                    )
                }
            },
            onError = {cause, page ->
                if(page == Constants.INITIAL_PAGE_NUMBER) { // at initial page the refresh failed
                    _homeRefreshState.value = _homeRefreshState.value.copy(
                        refreshErrorMessage =  cause // show error
                    )
                } else { // loading more pages failed
                    _postFeedUiState.value = _postFeedUiState.value.copy(
                        errorMessage = cause
                    )
                }
            },
            onLoadStateChange = {isLoading ->
                _postFeedUiState.value = _postFeedUiState.value.copy(
                    isLoading = isLoading
                )
            },
        )
    }

    private fun loadMorePosts() {
        if (postFeedUiState.endReached) return
        viewModelScope.launch {
            pagingManager.loadItems()
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

    private fun likeOrUnlike(post: Post){
        viewModelScope.launch {
            val count = if (post.isLiked) -1 else +1
            _postFeedUiState.value = _postFeedUiState.value.copy(
                posts = _postFeedUiState.value.posts.map {
                    if(it.postId == post.postId) {
                        it.copy(likesCount = post.likesCount.plus(count), isLiked = !post.isLiked)
                    } else {
                        it
                    }
                }
            )

            val result = likePostsUseCase(post)

            when (result) {
                is Result.Error -> {
                    updatePost(post)
                }
                is Result.Success -> Unit
            }

        }
    }

    private fun updatePost(post: Post) {
        _postFeedUiState.value = _postFeedUiState.value.copy(
            posts = _postFeedUiState.value.posts.map {
                if(it.postId == post.postId){
                    post
                } else {
                    it
                }

            }
        )
    }

    private fun updateCurrentUserProfile(profile: Profile) {
        _postFeedUiState.value = _postFeedUiState.value.copy(
            posts = _postFeedUiState.value.posts.map {
                if(it.userId == profile.id){
                    it.copy(
                        userName = profile.name,
                        userImageUrl = profile.imageUrl
                    )
                } else {
                    it
                }

            }
        )
    }

    fun onUiAction(uiAction: HomeUiAction) {
        when (uiAction) {
            is HomeUiAction.FollowUserAction -> followUser(uiAction.user)
            HomeUiAction.LoadMorePostsAction -> loadMorePosts()
            is HomeUiAction.PostLikeAction -> likeOrUnlike(uiAction.post)
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
    val endReached: Boolean = false,
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
    data class PostLikeAction(val post: Post): HomeUiAction
    data object RemoveOnBoardingAction: HomeUiAction
    data object RefreshAction: HomeUiAction
    data object LoadMorePostsAction: HomeUiAction
}