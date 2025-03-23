package com.example.socialapp.android.auth.login
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.common.data.local.UserSettings
import com.example.socialapp.common.data.local.toUserSettings
import com.example.socialapp.auth.domain.usecase.SignInUseCase
import com.example.socialapp.common.util.Result
import kotlinx.coroutines.launch

// a dataclass to hold the state of login screen email and password
data class LoginUiState(
    var email: String = "", // add default value so ui can render on mount
    var password: String = "",
    var isAuthenticating: Boolean = false,
    var authErrorMessage: String? = null,
    var authenticationSucceed: Boolean = false
)

// manage the login screen UI sate
class LoginViewModel(
    private val signInUseCase: SignInUseCase,
    private val dataStore: DataStore<UserSettings>
): ViewModel() {
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

    fun signIn(){
        viewModelScope.launch {
            _uiState.value = uiState.copy(isAuthenticating = true)

            val authResultData = signInUseCase( _uiState.value.email,  _uiState.value.password)

            _uiState.value = when(authResultData){
                is Result.Error -> {
                    _uiState.value.copy(
                        isAuthenticating =  false,
                        authErrorMessage = authResultData.message
                    )
                }
                is Result.Success<*> -> {
                    // moved to the shared module
//                    dataStore.updateData {
//                        authResultData.data!!.toUserSettings() // map autheresultdata to user settings
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
