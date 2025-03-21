package com.example.socialapp.android.common.components

import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FollowsButton(
    modifier: Modifier = Modifier,
    @StringRes text: Int,
    onClick: () -> Unit,
    isOutlined: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        colors = if (isOutlined) {
            ButtonDefaults.outlinedButtonColors()
        } else {
            ButtonDefaults.buttonColors()
        },
        border = if (isOutlined) {
            ButtonDefaults.outlinedButtonBorder
        } else {
             null
        },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = stringResource(id = text),
            style = TextStyle(
                fontSize = 12.sp,
            )
        )
    }
}