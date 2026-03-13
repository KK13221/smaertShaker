package com.smartshake.vending.data

/**
 * A line item in the order — one flavour with a chosen quantity.
 */
data class OrderItem(
    val flavour: Flavour,
    val quantity: Int
) {
    /** Line-item total price in rupees. */
    val lineTotal: Int get() = flavour.priceRupees * quantity
}
