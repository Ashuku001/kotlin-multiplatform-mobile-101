package com.example.socialapp.android.post.create

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.socialapp.post.domain.usecase.AddPostCommentUseCase

class PostCreateViewModel(
    private val addPostCommentUseCase: AddPostCommentUseCase,
): ViewModel() {
    private val _postCreateUiState: MutableState<CreatePostUiState> = mutableStateOf(CreatePostUiState())
    val postCreateUiState: CreatePostUiState get() = _postCreateUiState.value
}

data class CreatePostUiState(
    val caption: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val postCreated: Boolean = false
)