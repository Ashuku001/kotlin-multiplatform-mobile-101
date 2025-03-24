package com.example.socialapp.android.account.profile

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.android.common.fakedata.SamplePost
import com.example.socialapp.android.common.fakedata.Profile
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.android.common.fakedata.sampleProfiles
import com.example.socialapp.common.domain.model.Post
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {
    private val _userUiInfoState: MutableState<UserInfoUiState> = mutableStateOf(UserInfoUiState())
    val userUiInfoUiState: UserInfoUiState get() = _userUiInfoState.value
    private val _profilePostUiState: MutableState<ProfilePostUiState> = mutableStateOf(
        ProfilePostUiState()
    )
    val profilePostUiState: ProfilePostUiState get() = _profilePostUiState.value

    fun fetchProfile(userId: Long) {
        // Launch a new coroutine
        viewModelScope.launch {
            delay(1000)

            _userUiInfoState.value = _userUiInfoState.value.copy(
                isLoading = false,
                profile = sampleProfiles.find{it.id.toLong() == userId}
            )

            _profilePostUiState.value = _profilePostUiState.value.copy(
                isLoading = false,
                posts = samplePosts.map { it.toPost() }
            )
        }
    }
}

data class UserInfoUiState(
    val isLoading: Boolean = true,
    val profile: Profile? = null,
    var errorMessage: String? = null,
)

data class ProfilePostUiState(
    val isLoading: Boolean = true,
    val posts: List<Post> = listOf(),
    var errorMessage: String? = null
)


