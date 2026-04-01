package com.example.smartshake.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MachineStockResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("flavors") val flavors: List<FlavourItem>,
    @SerializedName("bases") val bases: List<BaseItem>
) : Serializable
