package com.iti.pocketshop.features.productdetails.data.repository

import com.iti.pocketshop.features.productdetails.data.datasource.AiCompareDataSource
import com.iti.pocketshop.features.productdetails.domain.entity.AiComparisonResult
import com.iti.pocketshop.features.productdetails.domain.entity.ComparisonTableRow
import com.iti.pocketshop.features.productdetails.domain.repository.AiCompareRepository
import org.json.JSONObject
import javax.inject.Inject

class AiCompareRepositoryImpl @Inject constructor(
    private val dataSource: AiCompareDataSource,
) : AiCompareRepository {

    override suspend fun compareProducts(
        currentProductTitle: String,
        currentProductDesc: String,
        currentProductVendor: String,
        currentProductPrice: String,
        currentProductRating: Double,
        selectedProductsInfo: Map<String, String>
    ): Result<AiComparisonResult> = try {
        val prompt = buildComparisonPrompt(
            currentProductTitle = currentProductTitle,
            currentProductDesc = currentProductDesc,
            currentProductVendor = currentProductVendor,
            currentProductPrice = currentProductPrice,
            currentProductRating = currentProductRating,
            selectedProductsInfo = selectedProductsInfo
        )

        val rawText = dataSource.generateContent(prompt)

        val cleanedText = rawText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val jsonObject = JSONObject(cleanedText)

        val currentProductObj = jsonObject.optJSONObject("currentProduct")
        val currentPros = mutableListOf<String>()
        val currentCons = mutableListOf<String>()
        if (currentProductObj != null) {
            val prosArray = currentProductObj.optJSONArray("pros")
            if (prosArray != null) {
                for (i in 0 until prosArray.length()) {
                    currentPros.add(prosArray.getString(i))
                }
            }
            val consArray = currentProductObj.optJSONArray("cons")
            if (consArray != null) {
                for (i in 0 until consArray.length()) {
                    currentCons.add(consArray.getString(i))
                }
            }
        }

        val comparedProductsPros = mutableMapOf<String, List<String>>()
        val comparedProductsCons = mutableMapOf<String, List<String>>()
        
        val comparedProductsObj = jsonObject.optJSONObject("comparedProducts")
        if (comparedProductsObj != null) {
            comparedProductsObj.keys().forEach { key ->
                val productObj = comparedProductsObj.optJSONObject(key)
                if (productObj != null) {
                    val prosArray = productObj.optJSONArray("pros")
                    val pros = mutableListOf<String>()
                    if (prosArray != null) {
                        for (i in 0 until prosArray.length()) {
                            pros.add(prosArray.getString(i))
                        }
                    }

                    comparedProductsPros[key] = pros

                    val consArray = productObj.optJSONArray("cons")
                    val cons = mutableListOf<String>()
                    if (consArray != null) {
                        for (i in 0 until consArray.length()) {
                            cons.add(consArray.getString(i))
                        }
                    }
                    comparedProductsCons[key] = cons
                }
            }
        }

        val summaryTableObjArray = jsonObject.optJSONArray("summaryTable")
        val summaryTableList = mutableListOf<ComparisonTableRow>()
        if (summaryTableObjArray != null) {
            for (i in 0 until summaryTableObjArray.length()) {
                val rowObj = summaryTableObjArray.optJSONObject(i)
                if (rowObj != null) {
                    val feature = rowObj.optString("feature", "")
                    val currentProductValue = rowObj.optString("currentProductValue", "")
                    val comparedProductsValuesObj = rowObj.optJSONObject("comparedProductsValues")
                    val comparedValuesMap = mutableMapOf<String, String>()
                    if (comparedProductsValuesObj != null) {
                        comparedProductsValuesObj.keys().forEach { k ->
                            comparedValuesMap[k] = comparedProductsValuesObj.optString(k, "")
                        }
                    }
                    summaryTableList.add(ComparisonTableRow(feature, currentProductValue, comparedValuesMap))
                }
            }
        }

        val result = AiComparisonResult(
            comparisonSummary = stripMarkdown(jsonObject.optString("comparisonSummary", "No summary available.")),
            currentProductPros = currentPros,
            currentProductCons = currentCons,
            comparedProductsPros = comparedProductsPros,
            comparedProductsCons = comparedProductsCons,
            recommendation = stripMarkdown(jsonObject.optString("recommendation", "No recommendation available.")),
            summaryTable = summaryTableList,
            suggestedProducts = emptyList()
        )
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun stripMarkdown(text: String): String {
        return text
            .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
            .replace(Regex("\\*(.+?)\\*"), "$1")
            .replace(Regex("__(.+?)__"), "$1")
            .replace(Regex("_(.+?)_"), "$1")
            .replace(Regex("#{1,6}\\s*"), "")
            .trim()
    }

    private fun buildComparisonPrompt(
        currentProductTitle: String,
        currentProductDesc: String,
        currentProductVendor: String,
        currentProductPrice: String,
        currentProductRating: Double,
        selectedProductsInfo: Map<String, String>,
    ): String {
        val selectedProductsText = selectedProductsInfo.entries.joinToString(separator = "\n\n---\n\n") {
            "Title: ${it.key}\n${it.value}"
        }

        return """
        You are an expert shopping assistant. Compare the Current Product against the Selected Products and provide the output ONLY as a valid JSON object.
        Do NOT use markdown formatting (no **, __, #, *, or backticks) anywhere in the JSON values. Write all text as plain sentences only.
        Do not include markdown code blocks, just the raw JSON.

        Current Product:
        Title: $currentProductTitle
        Brand/Vendor: $currentProductVendor
        Price: $currentProductPrice
        Rating: $currentProductRating / 5
        Description: $currentProductDesc

        Selected Products to Compare:
        $selectedProductsText

        Provide a thorough comparison considering price-to-value, brand reputation, features, and user needs.
        Format required (respond ONLY with this JSON, no other text):
        {
            "comparisonSummary": "A detailed summary comparing the Current Product with all Selected Products, considering price, brand, and features",
            "currentProduct": {
                "pros": ["Pro 1", "Pro 2"],
                "cons": ["Con 1", "Con 2"]
            },
            "comparedProducts": {
                "Product Title 1": {
                    "pros": ["Pro 1"],
                    "cons": ["Con 1"]
                },
                "Product Title 2": {
                    "pros": ["Pro 1"],
                    "cons": ["Con 1"]
                }
            },
            "recommendation": "Which product to buy and why, mentioning price and value",
            "summaryTable": [
                {
                    "feature": "A feature name (e.g. Price, Rating, Brand)",
                    "currentProductValue": "Value for current product",
                    "comparedProductsValues": {
                        "Product Title 1": "Value for product 1",
                        "Product Title 2": "Value for product 2"
                    }
                }
            ]
        }
        """.trimIndent()
    }
}