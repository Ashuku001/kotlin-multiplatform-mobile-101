package com.example.socialapp.android.post

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.android.common.fakedata.Comment
import com.example.socialapp.android.common.fakedata.sampleComments
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.common.domain.model.Post
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PostDetailScreenViewModel: ViewModel() {
    private val _postUiState: MutableState<PostUiState> = mutableStateOf(PostUiState())
    val postUiState: PostUiState get() = _postUiState.value

    private val _commentsUiState: MutableState<CommentsUiState> = mutableStateOf(CommentsUiState())
    val commentsUiState: CommentsUiState get() = _commentsUiState.value

    fun fetchData(postId: Long) {
        _postUiState.value = postUiState.copy(isLoading = true)

        _commentsUiState.value = _commentsUiState.value.copy(isLoading = true)

        // launch a new coroutine using scope
        viewModelScope.launch {


            // assimilate a request
            delay(500)

            _postUiState.value = _postUiState.value.copy(
                isLoading = false,
                post = samplePosts.map{it.toDomainPost()}.find { it.postId == postId }
            )

            _commentsUiState.value = _commentsUiState.value.copy(
                isLoading = false,
                comments = sampleComments
            )
        }
    }
}

data class PostUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val errorMessage: String? = null
)

data class CommentsUiState(
    val isLoading: Boolean = false,
    val comments: List<Comment> = listOf(),
    val errorMessage: String? = null
)