package com.example.socialapp.android.account.follows

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.android.common.fakedata.sampleUsers
import com.example.socialapp.android.common.util.Constants
import com.example.socialapp.android.common.util.DefaultPagingManager
import com.example.socialapp.android.common.util.PagingManager
import com.example.socialapp.common.domain.model.FollowsUser
import com.example.socialapp.follows.domain.usecase.GetFollowsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FollowsViewModel(
    private val getFollowsUseCase: GetFollowsUseCase
): ViewModel() {
    private val _uiState: MutableState<FollowsUiState> = mutableStateOf(FollowsUiState())
    val uiState: FollowsUiState get() = _uiState.value

    private lateinit var pagingManager: PagingManager<FollowsUser>

    fun fetchFollows(userId: Long, followsType: Int){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true
            )
            if (!::pagingManager.isInitialized) {
                pagingManager = createPagingManager(userId, followsType)
                pagingManager.loadItems()
            }
        }
    }

    private fun createPagingManager(userId: Long, followsType: Int): PagingManager<FollowsUser> {
        return DefaultPagingManager(
            onRequest = { page ->
                getFollowsUseCase(
                    userId = userId,
                    page = page,
                    pageSize = Constants.DEFAULT_REQUEST_PAGE_SIZE,
                    followsType = followsType
                )
            },
            onSuccess = { follows, _ ->
                _uiState.value  =  _uiState.value .copy(
                    isLoading = false,
                    followsUsers =  _uiState.value .followsUsers + follows,
                    endReached = follows.size < Constants.DEFAULT_REQUEST_PAGE_SIZE
                )
            },
            onError = { message, _ ->
                _uiState.value  =  _uiState.value .copy(
                    isLoading = false,
                    errorMessage = message
                )
            },
            onLoadStateChange = {
                _uiState.value  =  _uiState.value .copy(isLoading = it)
            }
        )
    }

    private fun loadMoreFollows() {
        if (uiState.endReached) return
        viewModelScope.launch {
            pagingManager.loadItems()
        }
    }

    fun onUiAction(action: FollowsUiAction) {
        when (action) {
            is FollowsUiAction.FetchFollowsAction -> {
                fetchFollows(action.userId, action.followsType)
            }
            is FollowsUiAction.LoadMoreFollowsAction -> {
                loadMoreFollows()
            }
        }
    }

}

sealed interface FollowsUiAction{
    data class FetchFollowsAction(val userId: Long, val followsType: Int): FollowsUiAction
    data object LoadMoreFollowsAction: FollowsUiAction
}

data class FollowsUiState (
    val isLoading: Boolean = false,
    val followsUsers: List<FollowsUser> = listOf(),
    val errorMessage: String? = null,
    val endReached: Boolean = false
)