package com.example.socialapp.android.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.socialapp.android.common.theming.SocialAppTheme

@Composable
fun LoginScreen (
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNavigateToSignup: () -> Unit,
)
{
    Column (
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
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
        verticalArrangement = Arrangement.spacedBy(LargeSpacing)
        ) {
        CustomTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            hint = R.string.username_hint
        )
        CustomTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            hint = R.string.password_hint,
            keyboardType = KeyboardType.Password,
            isPasswordTextField = true
        )

        Button (
            onClick = {
                onNavigateToSignup()
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
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    SocialAppTheme {
        LoginScreen (
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onNavigateToSignup = {}
        )
    }
}