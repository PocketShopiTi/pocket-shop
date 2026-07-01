package com.iti.pocketshop.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.iti.pocketshop.R
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct


@Composable
fun RemoveFavoriteDialog(
    onConfirm: (FavoriteProduct) -> Unit,
) {
    var productPendingRemoval by remember { mutableStateOf<FavoriteProduct?>(null) }

    ObserveEvent(DeleteFavoriteDialogController.events) { favoriteProduct ->
        productPendingRemoval = favoriteProduct
    }

    productPendingRemoval?.let { product ->
        Dialog(
            onDismissRequest = {
                productPendingRemoval = null
            }
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 8.dp,
                modifier = Modifier,
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.lottie_remove_favorite)
                    )
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .size(180.dp),
                    )
                    Text(
                        text = stringResource(R.string.remove_favorite_dialog_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            R.string.remove_favorite_dialog_body,
                            product.title
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                productPendingRemoval = null
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = stringResource(R.string.remove_favorite_dialog_cancel),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Button(
                            onClick = {
                                onConfirm(product)
                                productPendingRemoval = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.remove_favorite_dialog_confirm),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}