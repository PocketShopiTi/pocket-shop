package com.iti.pocketshop.features.productdetails.presentation

internal fun reduceProductDetails(
    state: ProductDetailsState,
    action: ProductDetailsAction,
): ProductDetailsState = when (action) {
    is ProductDetailsAction.ImageSelected -> state.copy(
        selectedImageIndex = action.index.coerceIn(
            minimumValue = 0,
            maximumValue = state.product?.images?.lastIndex?.coerceAtLeast(0) ?: 0,
        ),
    )
    is ProductDetailsAction.OptionSelected -> if (
        state.isOptionValueAvailable(action.optionId, action.valueId)
    ) {
        state.copy(
            selectedOptionValueIds = state.selectedOptionValueIds +
                (action.optionId to action.valueId),
        )
    } else {
        state
    }
    ProductDetailsAction.ToggleDescription -> state.copy(
        isDescriptionExpanded = !state.isDescriptionExpanded,
    )
    ProductDetailsAction.DecreaseQuantity -> state.copy(
        quantity = (state.quantity - 1).coerceAtLeast(1),
    )
    ProductDetailsAction.IncreaseQuantity -> state.copy(
        quantity = (state.quantity + 1).coerceAtMost(99),
    )
    ProductDetailsAction.AddToCartClicked -> if (
        state.selectedVariant?.availableForSale == true
    ) {
        state.copy(isAddedToCart = true)
    } else {
        state
    }
    ProductDetailsAction.CartFeedbackFinished -> state.copy(isAddedToCart = false)
    else -> state
}
