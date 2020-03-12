package com.iqonic.shophop.models

data class CartTotalResponse(
        val cart_contents_tax: Double,
//    val cart_contents_taxes: CartContentsTaxes,
        val cart_contents_total: String,
        val discount_tax: Int,
        val discount_total: Double,
        val fee_tax: Int,
        val fee_taxes: List<Any>,
        val fee_total: String,
        val shipping_tax: Double,
//    val shipping_taxes: ShippingTaxes,
        val shipping_total: Double,
        val subtotal: String,
        val subtotal_tax: Double,
        val total: String,
        val total_tax: Int
)
