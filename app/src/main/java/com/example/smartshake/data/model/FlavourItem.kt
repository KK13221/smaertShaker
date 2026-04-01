package com.example.smartshake.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FlavourItem(
    @SerializedName("id") val flavors_id_PK: Int,
    @SerializedName("image") val image: String,
    @SerializedName("flavor_name") val name: String,
    @SerializedName("price") val price: String,
    @SerializedName("stock") val stock_level: Int,
    var scoops: Int = 0,
    val Colories: String? = null,
    val Protein: String? = null,
    val Carbs: String? = null,
    val Fat: String? = null,
    val Fibre: String? = null,
    val Sugar: String? = null
) : Serializable