package com.example.socialapp.android.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.socialapp.android.R
import com.example.socialapp.android.common.theming.ButtonHeight

@Composable
internal fun ScreenLevelErrorView(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            modifier = modifier,
            text = stringResource(id = R.string.could_not_load_profile) ,
            style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center)
        )

        Button(
            onClick = { onRetry() },
            modifier = modifier.height(ButtonHeight),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = stringResource(id = R.string.retry_button_text))
        }
    }
}