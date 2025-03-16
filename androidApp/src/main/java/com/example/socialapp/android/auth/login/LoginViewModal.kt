package com.example.socialapp.android.auth.login
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class LoginUiState(
    var email: String = "",
    var password: String = ""
)

class LoginViewModal : ViewModel() {
    private val _uiState: MutableState<LoginUiState> = mutableStateOf(LoginUiState())
    val uiState: LoginUiState get() = _uiState.value

    fun updateEmail(input: String) {
        _uiState.value = _uiState.value.copy(email = input)
    }

    fun updatePassword(input: String) {
        _uiState.value = _uiState.value.copy(password = input)
    }
}
