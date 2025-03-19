package com.example.socialapp.android.auth.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.socialapp.android.common.components.CustomTextField
import com.example.socialapp.android.common.theming.ExtraLargeSpacing
import com.example.socialapp.android.common.theming.LargeSpacing
import com.example.socialapp.android.common.theming.MediumSpacing
import com.example.socialapp.android.R
import com.example.socialapp.android.common.theming.ButtonHeight
import com.example.socialapp.android.common.theming.SmallSpacing
import com.example.socialapp.android.common.theming.SocialAppTheme

@Composable
fun LoginScreen (
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNavigateToSignup: () -> Unit,
    onSignInClick: () -> Unit,
    onNavigateToHome: () -> Unit,
)
{
    val context = LocalContext.current
    // column layout vertically arrange UI components
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column (
            modifier = modifier
                .fillMaxSize()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()) // allow scrolling
                .background( // apply color
                    color = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.background
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .padding(
                    top = ExtraLargeSpacing + LargeSpacing,
                    start = LargeSpacing + MediumSpacing,
                    end = LargeSpacing + MediumSpacing,
                    bottom = LargeSpacing
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LargeSpacing, Alignment.CenterVertically)
        ) {
            // text inputs
            CustomTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                hint = R.string.email_hint,
                keyboardType = KeyboardType.Email,
            )
            CustomTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                hint = R.string.password_hint,
                keyboardType = KeyboardType.Password,
                isPasswordTextField = true,
            )

            // button
            Button (
                onClick = {
                    onSignInClick()
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(ButtonHeight),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(id = R.string.login_button_label))
            }

            Row  (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SmallSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don't have an account", style = MaterialTheme.typography.bodyMedium)

                Text(
                    text = "Create Account",
                    modifier = Modifier.clickable {
                        onNavigateToSignup()
                    },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }

    LaunchedEffect (
        key1 = uiState.authenticationSucceed,
        key2 = uiState.authErrorMessage,
        block = {
            if (uiState.authenticationSucceed) {
                onNavigateToHome()
            }

            if (uiState.authErrorMessage != null) {
                Toast.makeText(context, uiState.authErrorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    )

}

@Preview
@Composable
fun SignUpScreenPreview() {
    SocialAppTheme {
        LoginScreen (
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onNavigateToSignup = {},
            onSignInClick = {},
            onNavigateToHome = {}
        )
    }
}