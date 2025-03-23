package com.example.socialapp.android

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.common.data.local.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel (
    private val dataStore: DataStore<UserSettings>
): ViewModel() {

    // tie stateIn to viewModelScope as long as viewmodel is alive it will hold and update ui state
    val uiState: StateFlow<MainActivityUiState> = dataStore.data.map {
        MainActivityUiState.Success(it)
    }.stateIn( // convert flow into a StateFlow
        scope = viewModelScope, // the collection live as longs as the viewModel
        initialValue = MainActivityUiState.Loading, // Start at LoadingState
        started = SharingStarted.WhileSubscribed(500)
    )
}

sealed interface MainActivityUiState {
    data object Loading: MainActivityUiState

    data class Success(val currentUser: UserSettings) : MainActivityUiState
}