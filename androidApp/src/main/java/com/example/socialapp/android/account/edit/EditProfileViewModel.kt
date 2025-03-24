package com.example.socialapp.android.account.edit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.android.common.fakedata.Profile
import com.example.socialapp.android.common.fakedata.sampleProfiles
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditProfileViewModel: ViewModel() {
    private val _editProfileUiState: MutableState<EditProfileUiState> = mutableStateOf(EditProfileUiState())
    val editProfileUiState: EditProfileUiState get() = _editProfileUiState.value

    private val _bioTextFieldValue: MutableState<TextFieldValue> =  mutableStateOf(TextFieldValue(text = ""))
    val bioTextFieldValue: TextFieldValue get() = _bioTextFieldValue.value

    fun fetchProfile(userId: Long) {
        viewModelScope.launch{
            _editProfileUiState.value = _editProfileUiState.value.copy(isLoading = true)

            delay(1000)

            _editProfileUiState.value = _editProfileUiState.value.copy(
                isLoading = false,
                profile = sampleProfiles.find{it.id.toLong() == userId}
            )

            _bioTextFieldValue.value = _bioTextFieldValue.value.copy(
                text = _editProfileUiState.value.profile?.bio ?: "",
                selection = TextRange(index = _editProfileUiState.value.profile?.bio?.length ?: 0)
            )

        }
    }

    fun uploadProfile(){
        viewModelScope.launch {
            _editProfileUiState.value = _editProfileUiState.value.copy(isLoading = true)

            delay(1000)

            _editProfileUiState.value = _editProfileUiState.value.copy(
                isLoading = false,
                uploadSucceed = true,
            )
        }
    }

    fun onNameChange(inputName: String) {
        _editProfileUiState.value = _editProfileUiState.value.copy(
            isLoading = false,
            profile = _editProfileUiState.value.profile?.copy(
                name = inputName
            )
        )
    }

    fun onBioChange(inputBio: TextFieldValue) {
        _bioTextFieldValue.value = _bioTextFieldValue.value.copy(
            text = inputBio.text,
            selection = TextRange(index = inputBio.text.length)
        )
    }
}

data class EditProfileUiState (
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val uploadSucceed: Boolean = false,
    val errorMessage: String? = null
)