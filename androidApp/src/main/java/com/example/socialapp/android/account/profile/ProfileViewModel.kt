package com.example.socialapp.android.account.profile

import com.example.socialapp.account.domain.model.Profile
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.account.domain.usecase.GetProfileUseCase
import com.example.socialapp.android.common.util.Constants
import com.example.socialapp.android.common.util.DefaultPagingManager
import com.example.socialapp.android.common.util.Event
import com.example.socialapp.android.common.util.EventBus
import com.example.socialapp.android.common.util.PagingManager
import com.example.socialapp.common.domain.model.Post
import com.example.socialapp.common.util.Result
import com.example.socialapp.follows.domain.usecase.FollowOrUnfollowUseCase
import com.example.socialapp.post.domain.usecase.GetUserPostsUseCase
import com.example.socialapp.post.domain.usecase.LikeOrUnlikePostUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val followOrUnfollowUseCase: FollowOrUnfollowUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase,
    private val likeOrUnlikePostUseCase: LikeOrUnlikePostUseCase
): ViewModel() {
    private val _userUiInfoState: MutableState<UserInfoUiState> = mutableStateOf(UserInfoUiState())
    val userUiInfoUiState: UserInfoUiState get() = _userUiInfoState.value
    private val _profilePostUiState: MutableState<ProfilePostUiState> = mutableStateOf(
        ProfilePostUiState()
    )
    val profilePostUiState: ProfilePostUiState get() = _profilePostUiState.value

    // lateinitialization
    private lateinit var pagingManager: PagingManager<Post>

    init {
        EventBus.events
            .onEach {
                when (it) {
                    is Event.ProfileUpdated -> updateProfile(it.profile)
                    is Event.PostUpdated -> updatePost(it.post)
                    is Event.PostCreated -> Unit
                }
            }.launchIn(viewModelScope)
    }

    private fun fetchProfile(userId: Long) {
        // Launch a new coroutine
        viewModelScope.launch {
            getProfileUseCase(profileId = userId) // returns a flow emitting loaidng, success or error states
                .onEach { // for each emitted value
                    when(it){
                        is Result.Error -> {
                            _userUiInfoState.value = _userUiInfoState.value.copy(
                                isLoading = false,
                                errorMessage = it.message
                            )
                        }
                        is Result.Success -> {
                            // loading profile was successful
                            _userUiInfoState.value = _userUiInfoState.value.copy(
                                isLoading = false,
                                profile = it.data
                            )
                            // load the profile's posts
                            fetchProfilePosts(profileId = userId)
                        }
                    }
                }.collect() // trigger execution of the flow
        }
    }

    private fun followUser(profile: Profile) {
        viewModelScope.launch {
            val count = if (profile.isFollowing) -1 else +1

            // update user
            _userUiInfoState.value = _userUiInfoState.value.copy(
                profile = _userUiInfoState.value.profile?.copy(
                    isFollowing =  !profile.isFollowing,
                    followersCount = profile.followingCount.plus(count),
                )
            )

            val result = followOrUnfollowUseCase(
                followedUserId = profile.id,
                shouldFollow = !profile.isFollowing
            )

            when(result) {
                is Result.Error -> {
                    _userUiInfoState.value = _userUiInfoState.value.copy(
                        profile = _userUiInfoState.value.profile?.copy(
                            isFollowing =  profile.isFollowing,
                            followersCount = profile.followingCount,

                        )
                    )
                }
                is Result.Success -> Unit
            }
        }
    }

    private fun loadMorePosts() {
        if (_profilePostUiState.value.endReached) return

        if (!::pagingManager.isInitialized) {
            return // Avoid calling loadItems() on an uninitialized pagingManager
        }

        viewModelScope.launch {
            pagingManager.loadItems()
        }
    }

    // called at initial load of profile screen
    private suspend fun fetchProfilePosts(profileId: Long) {
        if (_profilePostUiState.value.isLoading || _profilePostUiState.value.posts.isNotEmpty()) return // is initializing or already initialized

        if(!::pagingManager.isInitialized) { // paging manager was not initialized
            pagingManager = createPagingManager(profileId = profileId)
        }
        pagingManager.loadItems()
    }

    private fun createPagingManager(profileId: Long): PagingManager<Post> {
        println("fetching more posts $profileId")
        return DefaultPagingManager(
            onRequest = {page ->
                getUserPostsUseCase(userId = profileId, pageSize = Constants.DEFAULT_REQUEST_PAGE_SIZE, page = page)
            },
            onSuccess = {posts, _ ->
                _profilePostUiState.value = if (posts.isEmpty()) {
                   _profilePostUiState.value.copy(
                       endReached = true,
                    )
                } else {
                    _profilePostUiState.value.copy(
                        posts = _profilePostUiState.value.posts + posts,
                        endReached = posts.size < Constants.DEFAULT_REQUEST_PAGE_SIZE,
                    )
                }
            },
            onError = {case, _ ->
                _profilePostUiState.value.copy(
                    errorMessage = case,
                )
            },
            onLoadStateChange = {isLoading ->
                _profilePostUiState.value.copy(
                   isLoading = isLoading
                )
            }
        )
    }

    private fun likeOrUnlike(post: Post) {
        viewModelScope.launch {
            val count = if (post.isLiked) -1 else +1

            val updatedPost = post.copy(
                isLiked = !post.isLiked,
                likesCount = post.likesCount.plus(count)
            )

            updatePost(updatedPost)

            val result = likeOrUnlikePostUseCase(post = post)

            when(result){
                is Result.Error -> {
                    updatePost(post)
                }
                is Result.Success -> {
                    EventBus.send(Event.PostUpdated(updatedPost))
                }
            }
        }
    }

    private fun updatePost(post: Post) {
        _profilePostUiState.value = _profilePostUiState.value.copy(
            posts = _profilePostUiState.value.posts.map {
                if (it.postId == post.postId) post else it
            }
        )
    }

    private fun updateProfile(profile: Profile) {
        _userUiInfoState.value = _userUiInfoState.value.copy(
            profile = profile
        )

        _profilePostUiState.value = _profilePostUiState.value.copy(
            posts = _profilePostUiState.value.posts.map {
                    if(it.userId == profile.id) {
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

    fun onUiAction(uiAction: ProfileUiAction) {
        when(uiAction){
            is ProfileUiAction.FetchProfileAction -> fetchProfile(uiAction.profileId)
            is ProfileUiAction.FollowUserAction -> followUser(uiAction.profile)
            is ProfileUiAction.LoadMorePostsAction -> loadMorePosts()
            is ProfileUiAction.PostLikeAction -> likeOrUnlike(uiAction.post)
        }
    }
}

data class UserInfoUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    var errorMessage: String? = null,
)

data class ProfilePostUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = listOf(),
    var errorMessage: String? = null,
    val endReached: Boolean = false
)


sealed interface ProfileUiAction {
    data class FetchProfileAction(val profileId: Long): ProfileUiAction

    data class FollowUserAction(val profile: Profile): ProfileUiAction

    data object LoadMorePostsAction: ProfileUiAction

    data class PostLikeAction(val post: Post): ProfileUiAction
}

