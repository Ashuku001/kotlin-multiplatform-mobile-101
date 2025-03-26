package com.example.socialapp.android.account.edit

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination
fun EditProfile (
    navigator: DestinationsNavigator,
    userId: Long,
) {
    val viewModel: EditProfileViewModel = koinViewModel()

    EditProfileScreen(
        editProfileUiState = viewModel.editProfileUiState,
        onNameChange = viewModel::onNameChange,
        bioTextFieldValue = viewModel.bioTextFieldValue,
        onBioChange = viewModel::onBioChange,
        onUploadSucceed = { navigator.navigateUp() },
        onUiAction =  viewModel::onUiAction,
        userId = userId
    )
}