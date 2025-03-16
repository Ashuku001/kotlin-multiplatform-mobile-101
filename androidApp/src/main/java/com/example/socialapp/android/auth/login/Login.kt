package com.example.socialapp.android.auth.login

import androidx.compose.runtime.Composable
import com.example.socialapp.android.auth.destinations.signupDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun login(
    navigator: DestinationsNavigator
) {
    val viewModel: LoginViewModal = koinViewModel()
    LoginScreen(
        uiState =  viewModel.uiState,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onNavigateToSignup = {
            navigator.navigate(signupDestination)
        }
    )
}