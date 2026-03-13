package com.smartshake.vending.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.smartshake.vending.R

/**
 * The liquid base chosen for the shake.
 */
enum class Base(
    @StringRes val labelRes: Int,
    @DrawableRes val imageRes: Int
) {
    MILK(
        labelRes  = R.string.base_milk,
        imageRes  = R.drawable.img_milk          // add to res/drawable
    ),
    WATER(
        labelRes  = R.string.base_water,
        imageRes  = R.drawable.img_water         // add to res/drawable
    )
}
