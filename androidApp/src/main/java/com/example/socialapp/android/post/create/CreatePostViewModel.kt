package com.example.socialapp.android.post.create

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.socialapp.android.common.util.Event
import com.example.socialapp.android.common.util.EventBus
import com.example.socialapp.android.common.util.ImageBytesReader
import com.example.socialapp.common.util.Result
import com.example.socialapp.post.domain.usecase.CreatePostUseCase
import kotlinx.coroutines.launch

class CreatePostViewModel(
    private val createPostUseCase: CreatePostUseCase,
    private val imageBytesReader: ImageBytesReader
): ViewModel() {
    private val _createPostUiState: MutableState<CreatePostUiState> = mutableStateOf(CreatePostUiState())
    val createPostUiState: CreatePostUiState get() = _createPostUiState.value

    private fun onCaptionChange(input: String) {
        _createPostUiState.value = _createPostUiState.value.copy(
            caption = input
        )
    }

    private suspend fun uploadPost(imageBytes: ByteArray?) {
        if(imageBytes == null){
            return
        }
        val result = createPostUseCase(
            imageBytes = imageBytes,
            caption = _createPostUiState.value.caption,
        )
        when (result) {
            is Result.Error -> {
                _createPostUiState.value = _createPostUiState.value.copy(
                    errorMessage = result.message,
                    isLoading = false
                )
            }
            is Result.Success -> {
                EventBus.send(event = Event.PostCreated(post = result.data!!))
                _createPostUiState.value = _createPostUiState.value.copy(
                    postCreated = true,
                    isLoading = false
                )
            }
        }
    }

    private fun readImageBytes(imageUri: Uri) {
        _createPostUiState.value = _createPostUiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch {
            when(val result = imageBytesReader.readImageBytes(imageUri)){
                is Result.Error -> {
                    _createPostUiState.value = _createPostUiState.value.copy(
                        isLoading =  false,
                        errorMessage = result.message
                    )
                }
                is Result.Success -> {
                    uploadPost(imageBytes = result.data,)
                }
            }
        }
    }


    fun onUiAction(action: CreatePostUiAction) {
        when(action) {
            is CreatePostUiAction.CaptionChanged -> onCaptionChange(action.input)
            is CreatePostUiAction.CreatePostAction -> readImageBytes(action.imageUri)
        }
    }

}


data class CreatePostUiState (
    val isLoading: Boolean = false,
    val postCreated: Boolean = false,
    val errorMessage: String? = null,
    val caption: String = ""
)

sealed interface CreatePostUiAction{
    data class CaptionChanged(val input: String): CreatePostUiAction
    class CreatePostAction(val imageUri: Uri): CreatePostUiAction
}