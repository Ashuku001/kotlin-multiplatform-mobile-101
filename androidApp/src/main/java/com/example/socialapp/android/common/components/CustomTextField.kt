package com.example.socialapp.android.common.components

import androidx.compose.runtime.Composable
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.socialapp.android.common.theming.SocialAppTheme
import com.example.socialapp.android.R
import com.example.socialapp.android.common.theming.Typography
import com.example.socialapp.android.common.theming.TextFieldHeight

// Standardize text input field
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPasswordTextField: Boolean = false,
    isSingleLine: Boolean = true,
    @StringRes hint: Int, // resource id
) {
    // maintains an internal state to toggle password visibility
    var isPasswordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().height(TextFieldHeight),
        textStyle = Typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType
        ),
        singleLine = isSingleLine,
        colors = TextFieldDefaults.colors(
//            containerColor = if (isSystemInDarkTheme()) {
//                MaterialTheme.colorScheme.surface
//            } else {
//                Gray
//            },
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        // display a passwordEyIcon
        trailingIcon = if (isPasswordTextField) {
            {
                PasswordEyeIcon(
                        isPasswordVisible = isPasswordVisible,
                    ) {
                    isPasswordVisible = !isPasswordVisible
                }
            }
        } else {
            null
        },
        visualTransformation = if (isPasswordTextField && !isPasswordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        placeholder = {
            Text(text = stringResource(id = hint), style = Typography.bodyMedium)
        },
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun PasswordEyeIcon(
    isPasswordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit
) {
    val image = if (isPasswordVisible) {
        painterResource(id = R.drawable.show_eye_icon_filled)
    } else {
        painterResource(id = R.drawable.hide_eye_icon_filled)
    }

    IconButton(onClick = onPasswordVisibilityToggle) {
        Icon(painter = image, contentDescription = null)
    }
}

@Preview
@Composable
fun CustomTextFieldPreview() {
    SocialAppTheme {
        CustomTextField(
            value = "",
            onValueChange = {},
            hint = R.string.email_hint
        )
    }
}
