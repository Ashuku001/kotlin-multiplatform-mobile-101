package com.example.socialapp.android.account.follows

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.android.common.fakedata.sampleUsers
import com.example.socialapp.common.domain.model.FollowsUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FollowsViewModel: ViewModel() {
    private val _uiState: MutableState<FollowsUiState> = mutableStateOf(FollowsUiState())
    val uiState: FollowsUiState get() = _uiState.value

    fun fetchFollows(userId: Long, followsType: Int){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true
            )

            delay(1000)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                followsUsers = sampleUsers.map { it.toFollowsUser() }
            )
        }
    }
}

data class FollowsUiState (
    val isLoading: Boolean = false,
    val followsUsers: List<FollowsUser> = listOf(),
    val errorMessage: String? = null
)