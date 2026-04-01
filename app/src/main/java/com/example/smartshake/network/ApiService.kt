package com.example.smartshake.network

import com.example.smartshake.data.model.BaseItem
import com.example.smartshake.data.model.FlavourItem
import com.example.smartshake.data.model.QrRequest
import com.example.smartshake.data.model.QrResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("api/machine-flavor-stock/device/{deviceId}")
    fun getMachineStock(@Path("deviceId") deviceId: String): Call<com.example.smartshake.data.model.MachineStockResponse>

    @POST("create-qr")
    fun createQrCode(
        @Body request: QrRequest
    ): Call<QrResponse>

    @POST("api/devices")
    fun registerDevice(
        @Body request: com.example.smartshake.data.model.DeviceRequest
    ): Call<Any>

    @POST("api/devices/assignList")
    fun checkDeviceAssignment(
        @Query("device_name") deviceName: String
    ): Call<com.example.smartshake.data.model.AssignListResponse>
}