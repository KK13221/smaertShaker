package com.example.smartshake.data.model

data class AssignListResponse(
    val status: Boolean,
    val devices: List<AssignDevice>?
)

data class AssignDevice(
    val device_id_PK: Int,
    val device_name: String,
    val is_assign: Int
)
