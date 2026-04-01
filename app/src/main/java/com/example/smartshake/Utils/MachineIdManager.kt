package com.example.smartshake.Utils

import android.content.Context
import java.util.UUID
import kotlin.uuid.Uuid

object MachineIdManager {
    private const val PREF_NAME = "machine_prefs"
    private const val KEY_MACHINE_ID = "machine_id"
    private const val KEY_DEVICE_NAME = "device_name"
    private const val KEY_DEVICE_ID = "device_id"
    private const val KEY_IS_ASSIGN = "is_assign"

    fun getMachineId(context: Context): String {

        val prefs = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        var machineId = prefs.getString(KEY_MACHINE_ID, null)

        if (machineId == null) {

            // Generate readable Machine ID
            machineId = genrateUniqueId()

            prefs.edit()
                .putString(KEY_MACHINE_ID, machineId)
                .apply()
        }

        return machineId
    }

    fun saveDeviceDetails(context: Context, deviceName: String, deviceId: Int, isAssign: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_DEVICE_NAME, deviceName)
            .putInt(KEY_DEVICE_ID, deviceId)
            .putInt(KEY_IS_ASSIGN, isAssign)
            .apply()
    }

    fun getIsAssign(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_IS_ASSIGN, 0)
    }

    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_DEVICE_ID, 0).toString()
    }

    fun getDeviceName(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DEVICE_NAME, null)
    }

    private fun genrateUniqueId(): String{
        val uuidPart = UUID.randomUUID()
            .toString()
            .substring(0, 8)
            .uppercase()

        return "VM-$uuidPart"
    }
}