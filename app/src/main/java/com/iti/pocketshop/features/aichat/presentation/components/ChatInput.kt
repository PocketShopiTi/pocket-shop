package com.iti.pocketshop.features.aichat.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R


@Composable
fun ChatInput(
    text: String,
    selectedImageUri: Uri?,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    isSpeechRecognitionRunning: Boolean,
    onStartSpeechRecognition: () -> Unit,
    onStopSpeechRecognition: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val canSend = enabled && (text.isNotBlank() || selectedImageUri != null)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isSpeechRecognitionRunning) {
            WaveIndicator()
        }
        Box {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        translationY = (-64).dp.toPx()
                    }
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = stringResource(R.string.selected_image_preview),
                        modifier = Modifier
                            .padding(top = 10.dp, end = 10.dp)
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = onRemoveImage,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                            containerColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.remove_image),
                            modifier = Modifier
                                .size(16.dp)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChanged,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large,
                    leadingIcon = {
                        IconButton(
                            onClick = onPickImage,
                            modifier = Modifier
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = stringResource(R.string.add_image),
                            )
                        }
                    },
                    placeholder = { Text(stringResource(R.string.ask_about_products)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    maxLines = 4,
                    enabled = enabled,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (canSend) {
                                onSend()
                            }
                        }
                    )
                )
                IconButton(
                    onClick = {
                        if (text.isNotBlank() || selectedImageUri != null) {
                            onSend()
                        } else if (isSpeechRecognitionRunning) {
                            onStopSpeechRecognition()
                        } else {
                            onStartSpeechRecognition()
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = if (text.isNotBlank() || selectedImageUri != null) {
                            Icons.AutoMirrored.Filled.Send
                        } else if (isSpeechRecognitionRunning) {
                            Icons.Default.Stop
                        } else {
                            Icons.Default.Mic
                        },
                        contentDescription = stringResource(R.string.send)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WaveIndicator() {
    LinearWavyProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        amplitude = 20f,
        wavelength = 36.dp,
    )
}
