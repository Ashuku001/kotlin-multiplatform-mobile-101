package com.example.socialapp.android.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.socialapp.android.R
import com.example.socialapp.android.common.theming.LargeSpacing

@Composable
internal fun ScreenLevelLoadingErrorView(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(LargeSpacing)
        ) {
            Text(
                text = stringResource(id = R.string.loading_post_error),
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedButton (
                onClick = onRetry
            ) {
                Text(
                    text = stringResource(id = R.string.retry_button_text)
                )
            }
        }
    }
}