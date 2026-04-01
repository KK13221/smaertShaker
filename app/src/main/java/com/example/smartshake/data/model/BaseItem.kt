package com.example.smartshake.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BaseItem(
    @SerializedName("machine_base_id_PK") val base_id_PK: Int,
    @SerializedName("base_name") val name: String,
    @SerializedName("price") val priceStr: String,
    @SerializedName("image") val image: String
) : Serializable {
    val price: Int
        get() = priceStr.toDoubleOrNull()?.toInt() ?: 0
}
