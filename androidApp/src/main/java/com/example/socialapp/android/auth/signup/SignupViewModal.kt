package com.example.socialapp.android.auth.signup
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.common.data.local.UserSettings
import com.example.socialapp.common.data.local.toUserSettings
import com.example.socialapp.auth.domain.usecase.SignUpUseCase
import com.example.socialapp.common.util.Result
import kotlinx.coroutines.launch

data class SignUpUIState(
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var isAuthenticating: Boolean = false,
    var authErrorMessage: String? = null,
    var authenticationSucceed: Boolean = false
)

class SignupViewModel (
    private val signUpUseCase: SignUpUseCase,
    private val dataStore: DataStore<UserSettings>
) : ViewModel() {
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

    fun signUp() {
        viewModelScope.launch {
            _uiState.value = uiState.copy(isAuthenticating = true)

            val authResultData = signUpUseCase( _uiState.value.email,  _uiState.value.username, _uiState.value.password)

            _uiState.value = when(authResultData){
                is Result.Error -> {
                    _uiState.value.copy(
                        isAuthenticating =  false,
                        authErrorMessage = authResultData.message
                    )
                }
                is Result.Success<*> -> {
                    // moved to shared module
//                    dataStore.updateData {
//                        // force unwrap !! we are sure data will never be null
//                        authResultData.data!!.toUserSettings()
//                    }
                    _uiState.value.copy(
                        isAuthenticating = false,
                        authenticationSucceed = true
                    )
                }
            }
        }
    }
}
