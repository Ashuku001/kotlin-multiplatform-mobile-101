package com.example.socialapp.android.auth.signup
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class SignUpUIState(
    var username: String = "",
    var email: String = "",
    var password: String = ""
)

class SignupViewModel : ViewModel() {
    private val _uiState: MutableState<SignUpUIState> = mutableStateOf(SignUpUIState())
    val uiState: SignUpUIState get() = _uiState.value

    fun updateUsername(input: String) {
        _uiState.value = _uiState.value.copy(username = input)
    }

    fun updateEmail(input: String) {
        _uiState.value = _uiState.value.copy(email = input)
    }

    fun updatePassword(input: String) {
        _uiState.value = _uiState.value.copy(password = input)
    }
}
