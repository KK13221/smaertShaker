package com.smartshake.vending.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.smartshake.vending.R

/**
 * Represents a shake flavour available in the vending machine.
 *
 * @param id            Unique identifier (used as map key in ViewModel)
 * @param name          Display name shown on the card
 * @param priceRupees   Price in INR
 * @param textColorRes  Color resource for name text on the cream card background
 * @param imageRes      Drawable/mipmap resource for the flavour food photo
 */
data class Flavour(
    val id: String,
    val name: String,
    val priceRupees: Int,
    @ColorRes val textColorRes: Int,
    @DrawableRes val imageRes: Int
) {
    companion object {
        /**
         * Default catalogue of flavours.
         * Replace imageRes values with your actual drawable resources.
         */
        fun catalogue(): List<Flavour> = listOf(
            Flavour(
                id          = "chocolate",
                name        = "Chocolate",
                priceRupees = 120,
                textColorRes = R.color.ss_chocolate_text,
                imageRes    = R.drawable.img_chocolate     // add to res/drawable
            ),
            Flavour(
                id          = "vanilla",
                name        = "Vanilla",
                priceRupees = 110,
                textColorRes = R.color.ss_vanilla_text,
                imageRes    = R.drawable.img_vanilla
            ),
            Flavour(
                id          = "banana",
                name        = "Banana",
                priceRupees = 115,
                textColorRes = R.color.ss_banana_text,
                imageRes    = R.drawable.img_banana
            ),
            Flavour(
                id          = "strawberry",
                name        = "Strawberry",
                priceRupees = 125,
                textColorRes = R.color.ss_strawberry_text,
                imageRes    = R.drawable.img_strawberry
            ),
            Flavour(
                id          = "coffee",
                name        = "Coffee",
                priceRupees = 130,
                textColorRes = R.color.ss_coffee_text,
                imageRes    = R.drawable.img_coffee
            )
        )
    }
}
