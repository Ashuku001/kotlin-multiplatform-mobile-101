package com.example.socialapp.android.post

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.android.common.util.Constants
import com.example.socialapp.android.common.util.DefaultPagingManager
import com.example.socialapp.android.common.util.Event
import com.example.socialapp.android.common.util.EventBus
import com.example.socialapp.android.common.util.PagingManager
import com.example.socialapp.common.domain.model.Post
import com.example.socialapp.common.util.Result
import com.example.socialapp.post.domain.model.PostComment
import com.example.socialapp.post.domain.usecase.AddPostCommentUseCase
import com.example.socialapp.post.domain.usecase.GetPostCommentsUseCase
import com.example.socialapp.post.domain.usecase.GetPostUseCase
import com.example.socialapp.post.domain.usecase.LikeOrUnlikePostUseCase
import com.example.socialapp.post.domain.usecase.RemovePostCommentUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PostDetailScreenViewModel(
    private val getPostUseCase: GetPostUseCase,
    private val getPostCommentsUseCase: GetPostCommentsUseCase,
    private val likeOrUnlikePostUseCase: LikeOrUnlikePostUseCase,
    private val addPostCommentUseCase: AddPostCommentUseCase,
    private val removePostCommentUseCase: RemovePostCommentUseCase
): ViewModel() {
    private val _postUiState: MutableState<PostUiState> = mutableStateOf(PostUiState())
    val postUiState: PostUiState get() = _postUiState.value

    private val _commentsUiState: MutableState<CommentsUiState> = mutableStateOf(CommentsUiState())
    val commentsUiState: CommentsUiState get() = _commentsUiState.value

    private lateinit var pagingManager: PagingManager<PostComment>

    // listen to like events
    init {
        EventBus.events.onEach {
            when(it) {
                is Event.PostUpdated -> updatePost(it.post)
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchData(postId: Long) {
        // launch a new coroutine using scope
        viewModelScope.launch {
            delay(500)
            when(val result = getPostUseCase(postId = postId)) {
                is Result.Error -> {
                    _postUiState.value = _postUiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Result.Success -> {
                    _postUiState.value = _postUiState.value.copy(
                        isLoading = false,
                        post = result.data,
                        errorMessage = null
                    )

                    fetchPostsComments(postId)
                }
            }


        }
    }

    private suspend fun fetchPostsComments(postId: Long) {
        if (commentsUiState.isLoading || commentsUiState.comments.isNotEmpty()) return // called only once on initial load

        if(!::pagingManager.isInitialized) {
            pagingManager = createPagingManager(postId)
        }

        pagingManager.loadItems()
    }

    private fun loadMoreComments() {
        println("HERE $_commentsUiState.value.endReached)")
        if (_commentsUiState.value.endReached) return

        viewModelScope.launch { pagingManager.loadItems() }
    }

    private fun likeOrUnlikePost(post: Post) {
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

    private fun createPagingManager(postId: Long): PagingManager<PostComment>{
        return DefaultPagingManager(
            onRequest = {page ->
                getPostCommentsUseCase(
                    postId = postId,
                    page = page,
                    pageSize = Constants.DEFAULT_COMMENTS_PAGE_SIZE
                )
            },
            onSuccess = {comments, _ ->
                _commentsUiState.value = _commentsUiState.value.copy(
                    isLoading = false,
                    comments = _commentsUiState.value.comments + comments,
                    endReached = comments.size < Constants.DEFAULT_COMMENTS_PAGE_SIZE
                )
            },
            onError = {message, _ ->
                _commentsUiState.value = _commentsUiState.value.copy(
                    isLoading = false,
                    errorMessage = message
                )
            },
            onLoadStateChange = {isLoading ->
                _commentsUiState.value = _commentsUiState.value.copy(
                    isLoading = isLoading,
                )
            }

        )
    }

    private fun updatePost(post: Post) {
        _postUiState.value = _postUiState.value.copy(
            post = post
        )

    }

    private fun addNewComment(comment: String) {
        viewModelScope.launch{
            val post = postUiState.post ?: return@launch // Nothing to comment on

            _commentsUiState.value = _commentsUiState.value.copy(isAddingNewComment = true)
            delay(1000)

            val result = addPostCommentUseCase(
                postId = post.postId,
                content = comment
            )

            when(result){
                is Result.Error -> {
                    _commentsUiState.value = _commentsUiState.value.copy(
                        isAddingNewComment = false,
                        errorMessage = result.message
                    )
                }
                is Result.Success -> {
                    val newComment = result.data ?: return@launch
                    val updatedComments = listOf(newComment) + _commentsUiState.value.comments // display at the top
                    _commentsUiState.value = _commentsUiState.value.copy(
                        isAddingNewComment = false,
                        comments = updatedComments
                    )

                    val updatedPost = post.copy(
                        commentsCount = post.commentsCount.plus(1) // other screens about the new comment
                    )

                    EventBus.send(Event.PostUpdated(updatedPost))
                }
            }
        }
    }

    private fun removeComment(postComment: PostComment) {
        viewModelScope.launch{
            val post = postUiState.post ?: return@launch // Nothing to comment on

            // remove the comment from the state
            val updatedComments = _commentsUiState.value.comments.filter{
                it.commentId != postComment.commentId
            }

            _commentsUiState.value = _commentsUiState.value.copy(
                comments = updatedComments
            )

            val result = removePostCommentUseCase(
                postId = post.postId,
                commentId = postComment.commentId
            )

            when(result){
                is Result.Error -> {
                    _commentsUiState.value = _commentsUiState.value.copy(
                        isAddingNewComment = false,
                        errorMessage = result.message,
                        comments = _commentsUiState.value.comments // original comments list
                    )
                }
                is Result.Success -> {
                    val updatedPost = post.copy(
                        commentsCount = post.commentsCount.minus(1) // other screens about the new comment
                    )
                    EventBus.send(Event.PostUpdated(updatedPost))
                }
            }
        }
    }

    fun onUiAction(action: PostDetailUiAction) {
        when (action) {
            is PostDetailUiAction.FetchPostAction -> fetchData(action.postId)
            PostDetailUiAction.LoadMoreComments -> loadMoreComments()
            is PostDetailUiAction.LikeOrUnlikePostAction -> likeOrUnlikePost(action.post)
            is PostDetailUiAction.AddPostCommentAction ->addNewComment(action.comment)
            is PostDetailUiAction.RemoveCommentAction ->removeComment(action.comment)
        }
    }
}

data class PostUiState(
    val isLoading: Boolean = true,
    val post: Post? = null,
    val errorMessage: String? = null
)

data class CommentsUiState(
    val isLoading: Boolean = false,
    val comments: List<PostComment> = listOf(),
    val errorMessage: String? = null,
    val endReached: Boolean = false,
    val isAddingNewComment: Boolean = false
)

sealed interface PostDetailUiAction{
    data class FetchPostAction(val postId: Long): PostDetailUiAction
    data object LoadMoreComments: PostDetailUiAction
    data class LikeOrUnlikePostAction(val post: Post): PostDetailUiAction
    data class AddPostCommentAction(val comment: String): PostDetailUiAction
    data class RemoveCommentAction(val comment: PostComment): PostDetailUiAction
}