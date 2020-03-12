package com.iqonic.shophop.models

import java.io.Serializable

class MyOrderData : Serializable {
    var currency: String = ""
    var customer_id: Int = 0
    var date_completed: String? = ""
    var date_created: String = ""
    var discount_total: Double = 0.00
    var id: Int = 0
    var line_items: ArrayList<LineItem> = ArrayList()
    var payment_method: String = ""
    var payment_method_title: String = ""
    var shipping: Shipping?=null
    var shipping_tax: String = ""
    var shipping_total: Double = 0.0
    var status: String = ""
    var total: Double = 0.00
    var transaction_id: String = ""
}


class LineItem  : Serializable{
    var id: Int = 0
    var name: String = ""
    var price: Int = 0
    var product_id: Int = 0
    var quantity: Int = 0
    var sku: String = ""
    var subtotal: String = ""
    var subtotal_tax: String = ""
    var tax_class: String = ""
    var total: Double = 0.0
    var total_tax: String = ""
    var variation_id: Int = 0
    var image: String = ""
    var size: String = ""
    var color: String = ""


}
