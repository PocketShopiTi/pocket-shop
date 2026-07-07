package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewEditorSheet(
    editingReview: ProductReview?,
    initialCustomerName: String,
    customerId: String,
    isSubmitting: Boolean,
    onAction: (ProductDetailsAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var customerName by remember(editingReview?.id, initialCustomerName) {
        mutableStateOf(editingReview?.author ?: initialCustomerName)
    }
    var title by remember(editingReview?.id) { mutableStateOf(editingReview?.title.orEmpty()) }
    var body by remember(editingReview?.id) { mutableStateOf(editingReview?.body.orEmpty()) }
    var rating by remember(editingReview?.id) { mutableIntStateOf(editingReview?.rating ?: 0) }

    ModalBottomSheet(
        onDismissRequest = { onAction(ProductDetailsAction.ReviewEditorDismissed) },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(
                    if (editingReview == null) {
                        R.string.review_form_title
                    } else {
                        R.string.product_details_edit_review
                    },
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
            )
            RatingPicker(
                rating = rating,
                onRatingSelected = { rating = it },
                enabled = !isSubmitting,
            )
            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text(stringResource(R.string.review_customer_name_label)) },
                singleLine = true,
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.review_title_label)) },
                singleLine = true,
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text(stringResource(R.string.review_body_label)) },
                enabled = !isSubmitting,
                minLines = 4,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = { onAction(ProductDetailsAction.ReviewEditorDismissed) },
                    enabled = !isSubmitting,
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = {
                        onAction(
                            ProductDetailsAction.ReviewSubmitted(
                                customerId = customerId,
                                customerName = customerName,
                                rating = rating,
                                title = title,
                                body = body,
                            ),
                        )
                    },
                    enabled = !isSubmitting && rating > 0,
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(stringResource(R.string.review_submit_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingPicker(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    enabled: Boolean,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(5) { index ->
            val star = index + 1
            Row(
                modifier = Modifier
                    .size(34.dp)
                    .clickable(
                        enabled = enabled,
                        role = Role.Button,
                    ) { onRatingSelected(star) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                StarIcon(
                    filled = star <= rating,
                    iconSize = 24.dp,
                )
            }
        }
    }
}
