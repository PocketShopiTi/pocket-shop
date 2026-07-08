package com.iti.pocketshop.features.productlist.data

import com.iti.pocketshop.features.home.data.toDomain
import com.iti.pocketshop.features.productlist.domain.ProductListPage
import com.iti.pocketshop.shopify.GetCollectionProductsQuery
import com.iti.pocketshop.shopify.GetProductListQuery

fun GetProductListQuery.Data.toDomain(): ProductListPage {
    return ProductListPage(
        products = products.nodes.map { it.productFields.toDomain() },
        hasNextPage = products.pageInfo.hasNextPage,
        endCursor = products.pageInfo.endCursor
    )
}

fun GetCollectionProductsQuery.Data.toDomain(): ProductListPage {
    val products = collection?.products
    return ProductListPage(
        products = products?.nodes?.map { it.productFields.toDomain() }.orEmpty(),
        hasNextPage = products?.pageInfo?.hasNextPage ?: false,
        endCursor = products?.pageInfo?.endCursor
    )
}
