package com.iti.pocketshop.features.productdetails.domain.entity

import com.iti.pocketshop.features.home.domain.models.Product

data class ComparisonTableRow(
    val feature: String,
    val currentProductValue: String,
    val comparedProductsValues: Map<String, String>
)

data class AiComparisonResult(
    val comparisonSummary: String,
    val currentProductPros: List<String>,
    val currentProductCons: List<String>,
    val comparedProductsPros: Map<String, List<String>>,
    val comparedProductsCons: Map<String, List<String>>,
    val recommendation: String,
    val summaryTable: List<ComparisonTableRow> = emptyList(),
    val suggestedProducts: List<Product> = emptyList()
)
