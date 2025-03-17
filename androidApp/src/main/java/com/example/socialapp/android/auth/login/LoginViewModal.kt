package com.example.socialapp.android.auth.login
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

// a dataclass to hold the state of login screen email and password
data class LoginUiState(
    var email: String = "", // add default value so ui can render on mount
    var password: String = ""
)

// manage the login screen UI sate
class LoginViewModel : ViewModel() {
    // a mutable state of the data class holding email and password so any change trigger recomposition in the UI
    private val _uiState: MutableState<LoginUiState> = mutableStateOf(LoginUiState())
    val uiState: LoginUiState get() = _uiState.value

    // fun for state updates
    fun updateEmail(input: String) {
        // create a new state copy with updated value
        _uiState.value = _uiState.value.copy(email = input)
    }

    fun updatePassword(input: String) {
        _uiState.value = _uiState.value.copy(password = input)
    }
}
