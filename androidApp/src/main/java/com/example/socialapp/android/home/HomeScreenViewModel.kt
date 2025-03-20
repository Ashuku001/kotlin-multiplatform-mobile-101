package com.example.socialapp.android.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.android.auth.login.LoginUiState
import com.example.socialapp.android.common.fakedata.Post
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.android.common.fakedata.sampleUsers
import com.example.socialapp.android.home.onboarding.OnBoardingUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PostUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = listOf(),
    val errorMessage: String? = null
) {

}

class HomeScreenViewModel: ViewModel() {
    private val _postUiState: MutableState<PostUiState> = mutableStateOf(PostUiState())
    val postUiState: PostUiState get() = _postUiState.value

    private val _onBoardingUiState: MutableState<OnBoardingUiState> = mutableStateOf(OnBoardingUiState())
    val onBoardingUiState: OnBoardingUiState get() = _onBoardingUiState.value

    init {
        fetchData()
    }

    fun fetchData(){
        _onBoardingUiState.value = _onBoardingUiState.value.copy(isLoading = true)
        _postUiState.value = _postUiState.value.copy(isLoading = true)

        viewModelScope.launch {
            delay(1000)

            _onBoardingUiState.value = _onBoardingUiState.value.copy(
                isLoading = false,
                users = sampleUsers,
                shouldShowOnBoarding = true,

            )
            _postUiState.value = _postUiState.value.copy(
                isLoading = false,
                posts = samplePosts,
                )
        }
    }

}

