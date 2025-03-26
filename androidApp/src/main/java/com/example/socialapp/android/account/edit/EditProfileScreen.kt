package com.example.socialapp.android.account.edit

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.socialapp.android.R
import com.example.socialapp.android.common.components.CircleImage
import com.example.socialapp.android.common.components.CustomTextField
import com.example.socialapp.android.common.components.ScreenLevelErrorView
import com.example.socialapp.android.common.components.ScreenLevelLoadingView
import com.example.socialapp.android.common.theming.ButtonHeight
import com.example.socialapp.android.common.theming.ExtraLargeSpacing
import com.example.socialapp.android.common.theming.LargeSpacing
import com.example.socialapp.android.common.theming.SmallElevation
import com.example.socialapp.android.common.theming.Typography
import kotlinx.coroutines.selects.select

@Composable
fun EditProfileScreen (
    modifier: Modifier = Modifier,
    editProfileUiState: EditProfileUiState,
    onNameChange: (String) -> Unit,
    bioTextFieldValue: TextFieldValue,
    onBioChange: (TextFieldValue) -> Unit,
    onUploadSucceed: () -> Unit,
    userId: Long,
    onUiAction: (EditProfileUiAction) -> Unit
) {
    val context = LocalContext.current

    // use select image from gallery
    var selectedImage by remember { mutableStateOf<Uri?>(null) } // the uri of image
    val pickImage = rememberLauncherForActivityResult( // the image picker
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImage = uri } // store the uri to the selected Image
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if(editProfileUiState.profile != null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        color = if (isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.background
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                    .padding(ExtraLargeSpacing),
                verticalArrangement = Arrangement.spacedBy(LargeSpacing),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(

                ) {
                    CircleImage(
                        modifier = modifier.size(120.dp),
                        imageUrl = editProfileUiState.profile.imageUrl,
                        onClick = {}
                    )
                    IconButton(
                        onClick = {
                            // lauch the system image picker
                            pickImage.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = modifier
                            .align(Alignment.BottomEnd)
                            .shadow(
                                elevation = SmallElevation,
                                shape = RoundedCornerShape(percent = 25)
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(percent = 25)
                            )
                            .size(40.dp)
                    ) {
                        Icon (
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                }

                Spacer(modifier = modifier.height(LargeSpacing))

                CustomTextField(
                    value = editProfileUiState.profile.name,
                    onValueChange = onNameChange,
                    hint = R.string.username_hint
                )

                BioTextFiled(
                    value =bioTextFieldValue,
                    onValueChange = onBioChange
                )

                Button (
                    onClick = {

                        selectedImage?.let {
                            onUiAction(EditProfileUiAction.UpdateProfileAction(imageUri = it))
                        } ?: run {
                            onUiAction(EditProfileUiAction.UpdateProfileAction())
                        }
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(ButtonHeight),
                    elevation  = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text (
                        text = stringResource(id = R.string.upload_changes_text),
                    )
                }
            }
        }

        if(editProfileUiState.profile == null && editProfileUiState.errorMessage != null) {
            ScreenLevelErrorView(onRetry = { onUiAction(EditProfileUiAction.FetchProfileAction(userId)) })
        }

        if(editProfileUiState.isLoading) {
            ScreenLevelLoadingView()
        }
    }

    LaunchedEffect(
        key1 = Unit, block = { onUiAction(EditProfileUiAction.FetchProfileAction(userId)) }
    )

    LaunchedEffect(
        key1 = editProfileUiState.uploadSucceed,
        key2 = editProfileUiState.errorMessage,
        block = {
            if(editProfileUiState.uploadSucceed) {
                onUploadSucceed()
            }
            // the error after uploading profile i.e., there is profile but the upload failed
            if (editProfileUiState.profile != null && editProfileUiState.errorMessage != null) {
                Toast.makeText(
                    context,
                    editProfileUiState.errorMessage,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioTextFiled(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    println("THE VALUE $value")
    TextField(
        value = value,
        onValueChange = { onValueChange(
            TextFieldValue(
                text = it.text,
                selection = TextRange(it.text.length)
            )
        ) },
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(text = stringResource(id = R.string.user_bio_hint), style = Typography.bodyMedium)
        },
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

