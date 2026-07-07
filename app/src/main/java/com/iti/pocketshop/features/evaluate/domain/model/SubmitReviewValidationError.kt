package com.iti.pocketshop.features.evaluate.domain.model

sealed class SubmitReviewValidationError {
    object EmptyCustomerName : SubmitReviewValidationError()
    object EmptyTitle : SubmitReviewValidationError()
    object EmptyBody : SubmitReviewValidationError()
    object InvalidRating : SubmitReviewValidationError()
}