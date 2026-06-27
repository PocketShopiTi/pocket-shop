package com.iti.pocketshop.features.productdetails.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductImage
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOption
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import com.iti.pocketshop.features.productdetails.presentation.components.ColourSwatch
import com.iti.pocketshop.features.productdetails.presentation.components.SizeOption
import com.iti.pocketshop.ui.theme.PocketShopTheme
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ProductDetailsRoot(
    productId: String = "8628627538093",
    onBack: () -> Unit = {},
    onFavoriteChanged: (productId: String, isFavorite: Boolean) -> Unit = { _, _ -> },
    onSeeAllReviews: (productId: String) -> Unit = {},
    onAddToCart: (variantId: String, quantity: Int) -> Unit = { _, _ -> },
    viewModel: ProductDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(productId) {
        viewModel.onAction(ProductDetailsAction.ProductChanged(productId))
    }

    ProductDetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                ProductDetailsAction.BackClicked -> onBack()
                ProductDetailsAction.ToggleFavorite -> {
                    onFavoriteChanged(productId, !state.isFavorite)
                    viewModel.onAction(action)
                }
                ProductDetailsAction.SeeAllReviewsClicked -> onSeeAllReviews(productId)
                ProductDetailsAction.AddToCartClicked -> state.selectedVariant
                    ?.takeIf { it.availableForSale }
                    ?.let { onAddToCart(it.id, state.quantity) }
                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
fun ProductDetailsScreen(
    state: ProductDetailsState,
    onAction: (ProductDetailsAction) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (state.product != null) {
                ProductBottomBar(
                    quantity = state.quantity,
                    price = state.selectedVariant?.price,
                    isEnabled = state.selectedVariant?.availableForSale == true,
                    onAction = onAction,
                )
            }
        },
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(innerPadding))
            state.errorMessage != null || state.product == null -> ErrorContent(
                modifier = Modifier.padding(innerPadding),
                onRetry = { onAction(ProductDetailsAction.Retry) },
            )
            else -> ProductContent(
                product = state.product,
                state = state,
                onAction = onAction,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            )
        }
    }
}

@Composable
private fun ProductContent(
    product: ProductDetails,
    state: ProductDetailsState,
    onAction: (ProductDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        item {
            ProductImageGallery(
                images = product.images,
                selectedIndex = state.selectedImageIndex,
                isFavorite = state.isFavorite,
                onAction = onAction,
            )
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Text(
                    text = product.vendor.uppercase(Locale.getDefault()),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 1.1.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                Text(
                    text = product.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 25.sp,
                        lineHeight = 31.sp,
                    ),
                    modifier = Modifier.padding(top = 4.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    RatingSummary(product.rating, product.reviewCount)
                    Text(
                        text = formatMoney(state.selectedVariant?.price),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                    )
                }
                SectionDivider()
                product.options.forEach { option ->
                    ProductOptionSelector(option, state, onAction)
                }
                SectionDivider()
                DescriptionSection(product.description, state.isDescriptionExpanded) {
                    onAction(ProductDetailsAction.ToggleDescription)
                }
                SectionDivider()
                ReviewsSection(product.reviews) {
                    onAction(ProductDetailsAction.SeeAllReviewsClicked)
                }
            }
        }
    }
}

@Composable
private fun ProductOptionSelector(
    option: ProductOption,
    state: ProductDetailsState,
    onAction: (ProductDetailsAction) -> Unit,
) {
    val selectedId = state.selectedOptionValueIds[option.id]
    val selectedLabel = option.values.firstOrNull { it.id == selectedId }?.label.orEmpty()
    val isColourOption = option.values.any { it.swatchArgb != null }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = stringResource(
                if (isColourOption) R.string.product_details_colour else R.string.product_details_size,
                selectedLabel,
            ).uppercase(Locale.getDefault()),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = SectionLabelStyle(),
        )
        Row(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(if (isColourOption) 12.dp else 8.dp),
        ) {
            option.values.forEach { value ->
                val isSelected = selectedId == value.id
                val isAvailable = state.isOptionValueAvailable(option.id, value.id)
                if (isColourOption) {
                    ColourSwatch(
                        colour = Color(requireNotNull(value.swatchArgb)),
                        label = value.label,
                        selected = isSelected,
                        enabled = isAvailable,
                        onClick = {
                            onAction(ProductDetailsAction.OptionSelected(option.id, value.id))
                        },
                    )
                } else {
                    SizeOption(
                        label = value.label,
                        selected = isSelected,
                        enabled = isAvailable,
                        onClick = {
                            onAction(ProductDetailsAction.OptionSelected(option.id, value.id))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductImageGallery(
    images: List<ProductImage>,
    selectedIndex: Int,
    isFavorite: Boolean,
    onAction: (ProductDetailsAction) -> Unit,
) {
    val pageCount = images.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(
        initialPage = selectedIndex.coerceIn(0, pageCount - 1),
        pageCount = { pageCount },
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { onAction(ProductDetailsAction.ImageSelected(it)) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val image = images.getOrNull(page)
            AsyncImage(
                model = image?.url,
                contentDescription = image?.altText ?: stringResource(
                    R.string.product_details_image,
                    page + 1,
                    images.size,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            RoundIconButton(
                contentDescription = stringResource(R.string.product_details_back),
                onClick = { onAction(ProductDetailsAction.BackClicked) },
            ) { ArrowBackIcon() }
            RoundIconButton(
                contentDescription = stringResource(
                    if (isFavorite) R.string.product_details_remove_favorite
                    else R.string.product_details_add_favorite,
                ),
                onClick = { onAction(ProductDetailsAction.ToggleFavorite) },
            ) { HeartIcon(isFavorite) }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(
                            width = if (index == pagerState.currentPage) 20.dp else 6.dp,
                            height = 6.dp,
                        )
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                        ),
                )
            }
        }
    }
}


@Composable
fun ColourSwatch(
    colour: Color,
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val unavailable = stringResource(R.string.product_details_option_unavailable, label)
    Box(
        modifier = Modifier
            .size(36.dp)
            .semantics { if (!enabled) contentDescription = unavailable }
            .clip(CircleShape)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape,
            )
            .padding(3.dp)
            .clip(CircleShape)
            .background(colour.copy(alpha = if (enabled) 1f else 0.35f))
            .clickable(enabled = enabled, onClick = onClick),
    )
}

@Composable
fun SizeOption(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val unavailable = stringResource(R.string.product_details_option_unavailable, label)
    Box(
        modifier = Modifier
            .height(44.dp)
            .semantics { if (!enabled) contentDescription = unavailable }
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
            )
            .border(
                1.dp,
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(20.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.35f),
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
        )
    }
}

@Composable
private fun DescriptionSection(
    description: String,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = stringResource(R.string.product_details_description).uppercase(Locale.getDefault()),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = SectionLabelStyle(),
        )
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 21.sp),
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
        TextButton(
            onClick = onToggle,
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text = stringResource(
                    if (expanded) R.string.product_details_read_less
                    else R.string.product_details_read_more,
                ),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
            )
        }
    }
}

@Composable
private fun ReviewsSection(
    reviews: List<ProductReview>,
    onSeeAll: () -> Unit,
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.product_details_reviews).uppercase(Locale.getDefault()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = SectionLabelStyle(),
            )
            TextButton(onClick = onSeeAll, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = stringResource(R.string.product_details_see_all_reviews),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                )
            }
        }
        reviews.take(2).forEach { review ->
            ReviewCard(review, Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
private fun ReviewCard(review: ProductReview, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = review.avatarUrl,
                    contentDescription = review.author,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outline),
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = review.author,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RatingStars(review.rating.toDouble(), starSize = 10.dp)
                        Text(
                            text = formatDate(review),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        )
                    }
                }
            }
            Text(
                text = review.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 19.sp),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun RatingSummary(rating: Double, reviewCount: Int) {
    val description = stringResource(R.string.product_details_rating, rating, reviewCount)
    Row(
        modifier = Modifier.semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        RatingStars(rating, starSize = 12.dp)
        Text(
            text = "$rating ($reviewCount)",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        )
    }
}

@Composable
private fun RatingStars(rating: Double, starSize: androidx.compose.ui.unit.Dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { index -> StarIcon(filled = index < rating.toInt(), size = starSize) }
    }
}

@Composable
private fun ProductBottomBar(
    quantity: Int,
    price: Money?,
    isEnabled: Boolean,
    onAction: (ProductDetailsAction) -> Unit,
) {
    val quantityDescription = stringResource(R.string.product_details_quantity, quantity)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    QuantityButton(
                        description = stringResource(R.string.product_details_decrease_quantity),
                        onClick = { onAction(ProductDetailsAction.DecreaseQuantity) },
                    ) { MinusIcon() }
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.semantics {
                            contentDescription = quantityDescription
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    QuantityButton(
                        description = stringResource(R.string.product_details_increase_quantity),
                        onClick = { onAction(ProductDetailsAction.IncreaseQuantity) },
                    ) { PlusIcon() }
                }
            }
            Button(
                onClick = { onAction(ProductDetailsAction.AddToCartClicked) },
                enabled = isEnabled,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                BagIcon()
                Text(
                    text = stringResource(
                        R.string.product_details_add_to_cart,
                        formatMoney(price),
                    ),
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    val loadingDescription = stringResource(R.string.product_details_loading)
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = loadingDescription },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.product_details_error),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(R.string.product_details_retry))
        }
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(top = 16.dp),
        color = MaterialTheme.colorScheme.outline,
    )
}

@Composable
fun SectionLabelStyle() = MaterialTheme.typography.labelSmall.copy(
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.88.sp,
    fontWeight = FontWeight.Medium,
)

@Composable
private fun RoundIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(36.dp)
            .semantics { this.contentDescription = contentDescription }
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        shadowElevation = 2.dp,
    ) {
        Box(contentAlignment = Alignment.Center) { icon() }
    }
}

@Composable
private fun QuantityButton(
    description: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .semantics { contentDescription = description }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { icon() }
}

@Composable
private fun ArrowBackIcon() {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(Modifier.size(18.dp)) {
        val startX = if (isRtl) size.width * 0.72f else size.width * 0.28f
        val endX = if (isRtl) size.width * 0.28f else size.width * 0.72f
        drawLine(color, Offset(startX, size.height / 2), Offset(endX, size.height / 2), 1.6.dp.toPx())
        drawLine(color, Offset(startX, size.height / 2), Offset(size.width / 2, size.height * 0.25f), 1.6.dp.toPx(), StrokeCap.Round)
        drawLine(color, Offset(startX, size.height / 2), Offset(size.width / 2, size.height * 0.75f), 1.6.dp.toPx(), StrokeCap.Round)
    }
}

@Composable
private fun HeartIcon(filled: Boolean) {
    val color = MaterialTheme.colorScheme.primary
    Canvas(Modifier.size(17.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2, size.height * 0.88f)
            cubicTo(size.width * 0.12f, size.height * 0.63f, size.width * 0.03f, size.height * 0.38f, size.width * 0.18f, size.height * 0.20f)
            cubicTo(size.width * 0.31f, size.height * 0.05f, size.width * 0.46f, size.height * 0.12f, size.width / 2, size.height * 0.26f)
            cubicTo(size.width * 0.54f, size.height * 0.12f, size.width * 0.69f, size.height * 0.05f, size.width * 0.82f, size.height * 0.20f)
            cubicTo(size.width * 0.97f, size.height * 0.38f, size.width * 0.88f, size.height * 0.63f, size.width / 2, size.height * 0.88f)
            close()
        }
        if (filled) drawPath(path, color) else drawPath(path, color, style = Stroke(1.7.dp.toPx()))
    }
}

@Composable
private fun StarIcon(filled: Boolean, size: androidx.compose.ui.unit.Dp = 12.dp) {
    val color = MaterialTheme.colorScheme.primary
    Canvas(Modifier.size(size)) {
        val path = Path()
        repeat(10) { point ->
            val radius = if (point % 2 == 0) this.size.minDimension / 2 else this.size.minDimension * 0.22f
            val angle = Math.toRadians((-90 + point * 36).toDouble())
            val offset = Offset(
                x = this.size.width / 2 + (kotlin.math.cos(angle) * radius).toFloat(),
                y = this.size.height / 2 + (kotlin.math.sin(angle) * radius).toFloat(),
            )
            if (point == 0) path.moveTo(offset.x, offset.y) else path.lineTo(offset.x, offset.y)
        }
        path.close()
        if (filled) drawPath(path, color) else drawPath(path, color, style = Stroke(1.dp.toPx()))
    }
}

@Composable
private fun MinusIcon() {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(Modifier.size(13.dp)) {
        drawLine(color, Offset(1.dp.toPx(), size.height / 2), Offset(size.width - 1.dp.toPx(), size.height / 2), 1.dp.toPx(), StrokeCap.Round)
    }
}

@Composable
private fun PlusIcon() {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(Modifier.size(13.dp)) {
        val stroke = 1.dp.toPx()
        drawLine(color, Offset(stroke, size.height / 2), Offset(size.width - stroke, size.height / 2), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width / 2, stroke), Offset(size.width / 2, size.height - stroke), stroke, StrokeCap.Round)
    }
}

@Composable
private fun BagIcon() {
    val color = MaterialTheme.colorScheme.onPrimary
    Canvas(Modifier.size(15.dp)) {
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.18f, size.height * 0.3f),
            size = Size(size.width * 0.64f, size.height * 0.58f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()),
            style = Stroke(1.dp.toPx()),
        )
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(size.width * 0.34f, size.height * 0.08f),
            size = Size(size.width * 0.32f, size.height * 0.42f),
            style = Stroke(1.dp.toPx()),
        )
    }
}

@Composable
private fun formatMoney(money: Money?): String = remember(money, Locale.getDefault()) {
    money?.let {
        runCatching {
            NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance(it.currencyCode)
                maximumFractionDigits = if (it.amount % 1.0 == 0.0) 0 else 2
            }.format(it.amount)
        }.getOrDefault("${it.amount} ${it.currencyCode}")
    }.orEmpty()
}

@Composable
private fun formatDate(review: ProductReview): String = remember(review.date, Locale.getDefault()) {
    java.time.LocalDate.of(review.date.year, review.date.monthNumber, review.date.dayOfMonth)
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1180)
@Composable
private fun ProductDetailsPreview() {
    val product = ProductDetailsMockData.create("preview-product")
    PocketShopTheme(isDarkTheme = false) {
        ProductDetailsScreen(
            state = ProductDetailsState(
                productId = product.id,
                product = product,
                selectedOptionValueIds = mapOf("colour" to "ecru", "size" to "m"),
                isLoading = false,
            ),
            onAction = {},
        )
    }
}
