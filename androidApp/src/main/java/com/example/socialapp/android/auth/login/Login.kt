package com.example.socialapp.android.auth.login

import androidx.compose.runtime.Composable
import com.example.socialapp.android.destinations.HomeDestination
import com.example.socialapp.android.destinations.loginDestination
import com.example.socialapp.android.destinations.signupDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

// a bridge between dependency injection (providing the viewModel referenced by koinViewModel) and
// the UI to render the state of our viewModel
// ensure login screen receives its state and behavior from the view model (state manager) keeping
// UI reactive and modular i.e., UI and State are managed separately and connected via koinViewModel()
@Destination  // login annotated as a navigation destination will be called via DestinationsNavigator
@Composable
fun login(
    navigator: DestinationsNavigator
) {
    // inject login view model
    val viewModel: LoginViewModel = koinViewModel() // returns an Instance of LoginViewModel as referenced in app module dependency injection
    // call the screen with current ui state and callbacks to update the state and navigate to signup
    LoginScreen(
        uiState =  viewModel.uiState, // current ui state from the viewModel i.e., email and password
        onEmailChange = viewModel::updateEmail, // updating the current state of the viewmodel
        onPasswordChange = viewModel::updatePassword,
        onNavigateToSignup = {
            navigator.navigate(signupDestination) {
                popUpTo(loginDestination.route) {
                    inclusive = true
                }
            }// a lambda for navigation
        },
        onSignInClick = viewModel::signIn,
        onNavigateToHome = {
            navigator.navigate(HomeDestination){
                popUpTo(loginDestination.route) {
                    inclusive = true
                }
            }
        }
    )
}