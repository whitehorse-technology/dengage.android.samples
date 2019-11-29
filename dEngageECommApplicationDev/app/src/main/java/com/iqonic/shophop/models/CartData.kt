package com.iqonic.shophop.models



class Key {
    var key: String? = ""
     var product_id: Int = 0
    var quantity: Int = 0
    var variation: List<Any> = emptyList()
    var variation_id: Int = 0
    var data_hash: String = ""
    var product_name: String = ""
    var product_price: String = "0.0"
    var sale_price: String = ""
    var product_title: String = ""
    var product_image: String = ""
    var cartTotal: CartTotalResponse? = null
    var cartCount: String? = ""
    var product_color: String = ""
    var product_size: String = ""
}

