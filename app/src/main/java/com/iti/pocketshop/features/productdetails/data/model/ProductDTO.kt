package com.iti.pocketshop.features.productdetails.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("id")
    val id: Long,

    @SerialName("title")
    val title: String,

    @SerialName("body_html")
    val bodyHtml: String,

    @SerialName("vendor")
    val vendor: String,

    @SerialName("product_type")
    val productType: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("handle")
    val handle: String,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("published_at")
    val publishedAt: String,

    @SerialName("template_suffix")
    val templateSuffix: String?,

    @SerialName("published_scope")
    val publishedScope: String,

    @SerialName("tags")
    val tags: String,

    @SerialName("status")
    val status: String,

    @SerialName("admin_graphql_api_id")
    val adminGraphqlApiId: String,

    @SerialName("variants")
    val variants: List<VariantDto>,

    @SerialName("options")
    val options: List<OptionDto>,

    @SerialName("images")
    val images: List<ImageDto>,

    @SerialName("image")
    val image: ImageDto?
)@Serializable

data class VariantDto(
    @SerialName("id")
    val id: Long,

    @SerialName("product_id")
    val productId: Long,

    @SerialName("title")
    val title: String,

    @SerialName("price")
    val price: String,

    @SerialName("position")
    val position: Int,

    @SerialName("inventory_policy")
    val inventoryPolicy: String,

    @SerialName("compare_at_price")
    val compareAtPrice: String?,

    @SerialName("option1")
    val option1: String?,

    @SerialName("option2")
    val option2: String?,

    @SerialName("option3")
    val option3: String?,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("taxable")
    val taxable: Boolean,

    @SerialName("barcode")
    val barcode: String?,

    @SerialName("fulfillment_service")
    val fulfillmentService: String,

    @SerialName("grams")
    val grams: Int,

    @SerialName("inventory_management")
    val inventoryManagement: String?,

    @SerialName("requires_shipping")
    val requiresShipping: Boolean,

    @SerialName("sku")
    val sku: String,

    @SerialName("weight")
    val weight: Double,

    @SerialName("weight_unit")
    val weightUnit: String,

    @SerialName("inventory_item_id")
    val inventoryItemId: Long,

    @SerialName("inventory_quantity")
    val inventoryQuantity: Int,

    @SerialName("old_inventory_quantity")
    val oldInventoryQuantity: Int,

    @SerialName("admin_graphql_api_id")
    val adminGraphqlApiId: String,

    @SerialName("image_id")
    val imageId: Long?
)

@Serializable
data class OptionDto(
    @SerialName("id")
    val id: Long,

    @SerialName("product_id")
    val productId: Long,

    @SerialName("name")
    val name: String,

    @SerialName("position")
    val position: Int,

    @SerialName("values")
    val values: List<String>
)

@Serializable
data class ImageDto(
    @SerialName("id")
    val id: Long,

    @SerialName("alt")
    val alt: String?,

    @SerialName("position")
    val position: Int,

    @SerialName("product_id")
    val productId: Long,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("admin_graphql_api_id")
    val adminGraphqlApiId: String,

    @SerialName("width")
    val width: Int,

    @SerialName("height")
    val height: Int,

    @SerialName("src")
    val src: String,

    @SerialName("variant_ids")
    val variantIds: List<Long>
)