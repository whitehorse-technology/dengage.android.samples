package com.iqonic.shophop.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RequestModel {
    /**
     * Request for login
     */

    @SerializedName("password")
    @Expose
    var password: String? = null

    /**
     * Request for post product review
     */
    @SerializedName("product_id")
    @Expose
    var product_id: String? = null
    @SerializedName("review")
    @Expose
    var review: String? = null
    @SerializedName("reviewer")
    @Expose
    var reviewer: String? = null
    @SerializedName("reviewer_email")
    @Expose
    var reviewer_email: String? = null
    @SerializedName("rating")
    @Expose
    var rating: String? = null

    /**
     * Request for update order
     */
    @SerializedName("status")
    @Expose
    var status: String? = null

    /**
     * Request for delete product review
     */
    @SerializedName("force")
    @Expose
    var force: Boolean? = null

    /**
     * Request for update review
     */
    @SerializedName("id")
    @Expose
    var id: String? = null


    /**
     * Request for add item to cart
     */

    @SerializedName("quantity")
    @Expose
    var quantity: Int? = null

    @SerializedName("cart_item_data")
    @Expose
    var data: CartItemRequest? = null


    /**
     * Request for Create /update cart item
     */
    @SerializedName("first_name")
    @Expose
    var firstName:String?=null

    @SerializedName("last_name")
    @Expose
    var lastName:String?=null

    @SerializedName("email")
    @Expose
    var userEmail:String?=null

    @SerializedName("profile_image")
    @Expose
    var image:String?=null





}
