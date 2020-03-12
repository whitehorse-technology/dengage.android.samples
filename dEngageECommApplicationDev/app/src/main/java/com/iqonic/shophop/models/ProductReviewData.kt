package com.iqonic.shophop.models

import java.io.Serializable

data class ProductReviewData(
    val _links: ProductReviewLinks = ProductReviewLinks(),
    var date_created: String = "",
    val date_created_gmt: String = "",
    val id: Int = 0,
    var product_id: Int = 0,
    var rating: Int = 0,
    var review: String = "",
    var name: String = "",
    val reviewer_avatar_urls: ReviewerAvatarUrls = ReviewerAvatarUrls(),
    var email: String = "",
    var verified: Boolean = false

)

data class ProductReviewLinks(
        val collection: List<ProductReviewCollection> = listOf(),
        val self: List<ProductReviewSelf> = listOf(),
        val up: List<Up> = listOf()
)

data class ProductReviewSelf(
        val href: String = ""
)

data class ProductReviewCollection(
        val href: String = ""
)


data class Up(
        val href: String = ""
)

data class ReviewerAvatarUrls(
        val `24`: String = "",
        val `48`: String = "",
        val `96`: String = ""
)

data class DeletedReviewData(
    val deleted: Boolean,
    val previous: ProductReviewData
) : Serializable