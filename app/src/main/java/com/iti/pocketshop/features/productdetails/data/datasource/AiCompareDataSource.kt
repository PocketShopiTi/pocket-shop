package com.iti.pocketshop.features.productdetails.data.datasource



interface AiCompareDataSource {

    suspend fun generateContent(prompt: String): String
}