package com.example.smartshake

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import com.example.smartshake.Utils.MachineIdManager
import com.example.smartshake.Utils.Utils
import com.example.smartshake.databinding.ActivityUniqueIdBinding

class UniqueId : AppCompatActivity() {

    private lateinit var binding: ActivityUniqueIdBinding
    private lateinit var uniqueId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (MachineIdManager.getIsAssign(this) == 1) {
            navigateToMain()
            return
        }

        binding = ActivityUniqueIdBinding.inflate(layoutInflater)
        Utils.hideSystemBars(this)
        setContentView(binding.root)

        uniqueId = MachineIdManager.getMachineId(this)

        binding.tvUniqueId?.text = uniqueId
        binding.tvUniqueId?.setTextColor(ContextCompat.getColor(this, R.color.grey))

        registerDevice()

        binding.btnContinue?.setOnClickListener {
            checkAssignmentAndNavigate()
        }
    }

    private fun registerDevice() {
        val request = com.example.smartshake.data.model.DeviceRequest(device_name = uniqueId)
        
        com.example.smartshake.network.ApiClient.apiService.registerDevice(request)
            .enqueue(object : retrofit2.Callback<Any> {
                override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                    if (response.isSuccessful) {
                        binding.tvUniqueId?.setTextColor(ContextCompat.getColor(this@UniqueId, R.color.white))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                    android.widget.Toast.makeText(this@UniqueId, "Network error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun checkAssignmentAndNavigate() {
        com.example.smartshake.network.ApiClient.apiService.checkDeviceAssignment(uniqueId)
            .enqueue(object : retrofit2.Callback<com.example.smartshake.data.model.AssignListResponse> {
                override fun onResponse(
                    call: retrofit2.Call<com.example.smartshake.data.model.AssignListResponse>,
                    response: retrofit2.Response<com.example.smartshake.data.model.AssignListResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val assignResponse = response.body()
                        if (assignResponse?.status == true && assignResponse.devices?.isNotEmpty() == true) {
                            val device = assignResponse.devices[0]
                            if (device.is_assign == 1) {
                                MachineIdManager.saveDeviceDetails(this@UniqueId, device.device_name, device.device_id_PK, device.is_assign)
                                navigateToMain()
                            } else {
                                android.widget.Toast.makeText(this@UniqueId, "The device is not assigned to any person", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            android.widget.Toast.makeText(this@UniqueId, "The device is not assigned to any person", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        android.widget.Toast.makeText(this@UniqueId, "Error: ${response.code()}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<com.example.smartshake.data.model.AssignListResponse>, t: Throwable) {
                    android.widget.Toast.makeText(this@UniqueId, "Network error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}