package com.example.socialapp.android.account.edit

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.account.domain.model.Profile
import com.example.socialapp.account.domain.usecase.GetProfileUseCase
import com.example.socialapp.account.domain.usecase.UpdateProfileUseCase
import com.example.socialapp.android.common.util.Event
import com.example.socialapp.android.common.util.EventBus
import com.example.socialapp.android.common.util.ImageBytesReader
import com.example.socialapp.common.util.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val imageBytesReader: ImageBytesReader
): ViewModel() {
    private val _editProfileUiState: MutableState<EditProfileUiState> = mutableStateOf(EditProfileUiState())
    val editProfileUiState: EditProfileUiState get() = _editProfileUiState.value

    private val _bioTextFieldValue: MutableState<TextFieldValue> =  mutableStateOf(TextFieldValue(text = ""))
    val bioTextFieldValue: TextFieldValue get() = _bioTextFieldValue.value

    private fun fetchProfile(userId: Long) {
        viewModelScope.launch{
            _editProfileUiState.value = _editProfileUiState.value.copy(isLoading = true)

            when (val result = getProfileUseCase(profileId = userId).first()) {
                is Result.Error -> {
                    _editProfileUiState.value = _editProfileUiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Result.Success -> {
                    println("FETCHED PROFILE ${result.data}")
                    _editProfileUiState.value = _editProfileUiState.value.copy(
                        isLoading = false,
                        profile = result.data
                    )

                    _bioTextFieldValue.value = _bioTextFieldValue.value.copy(
                        text = result.data?.bio ?: "empty bio",
                        selection = TextRange(index = _editProfileUiState.value.profile?.bio?.length ?: 0)
                    )
                }
            }
            println("profile bio${_editProfileUiState.value.profile?.bio}")
        }
    }

    private suspend fun uploadProfile(imageBytes: ByteArray?, profile: Profile){
            _editProfileUiState.value = _editProfileUiState.value.copy(isLoading = true)

            delay(1000)

            val result = updateProfileUseCase(
                profile = profile.copy(bio = _bioTextFieldValue.value.text),
                imageBytes = imageBytes
            )

            when (result) {
                is Result.Error -> {
                    _editProfileUiState.value = _editProfileUiState.value.copy(
                        isLoading = false,
                        uploadSucceed = false,
                        errorMessage = result.message
                    )
                }
                is Result.Success -> {
                    EventBus.send(Event.ProfileUpdated(result.data!!))
                    _editProfileUiState.value = _editProfileUiState.value.copy(
                        isLoading = false,
                        uploadSucceed = true,
                    )
                }
            }
    }

    private fun readImageBytes(imageUri: Uri) {
        val profile = _editProfileUiState.value.profile ?: return
        _editProfileUiState.value = _editProfileUiState.value.copy(
            isLoading =  true
        )

        viewModelScope.launch {
            if(imageUri == Uri.EMPTY) {
                uploadProfile(null, profile)
                return@launch
            }

            when(val result = imageBytesReader.readImageBytes(imageUri)){
                is Result.Error -> {
                    _editProfileUiState.value = _editProfileUiState.value.copy(
                        isLoading =  false,
                        errorMessage = result.message
                    )
                }
                is Result.Success -> {
                    uploadProfile(imageBytes = result.data, profile = profile)
                }
            }

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

    fun onUiAction(uiAction: EditProfileUiAction) {
        when (uiAction) {
            is EditProfileUiAction.FetchProfileAction -> fetchProfile(uiAction.userId)
            is EditProfileUiAction.UpdateProfileAction -> readImageBytes(imageUri = uiAction.imageUri)
        }
    }
}

data class EditProfileUiState (
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val uploadSucceed: Boolean = false,
    val errorMessage: String? = null
)

sealed interface EditProfileUiAction{
    data class FetchProfileAction(val userId: Long): EditProfileUiAction
    data class UpdateProfileAction(val imageUri: Uri = Uri.EMPTY): EditProfileUiAction
}