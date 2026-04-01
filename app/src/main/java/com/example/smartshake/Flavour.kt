// Flavour.kt
package com.example.smartshake

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

import com.example.smartshake.data.model.FlavourItem
import com.example.smartshake.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Flavour : AppCompatActivity() {

    private val flavours = mutableListOf<FlavourItem>()
    private lateinit var adapter: FlavourAdapter

    private lateinit var recyclerFlavours: RecyclerView
    private lateinit var tvPrice: TextView
    private lateinit var btnBack: MaterialButton
    private lateinit var btnContinue: MaterialButton

    private val basesList = mutableListOf<com.example.smartshake.data.model.BaseItem>()
    private var totalPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()
        setContentView(R.layout.activity_flavour)

        // Find views
        recyclerFlavours = findViewById(R.id.recycler_flavours)
        tvPrice          = findViewById(R.id.tv_price)
        btnBack          = findViewById(R.id.btn_back)
        btnContinue      = findViewById(R.id.btn_continue)

        adapter = FlavourAdapter(flavours) { updateTotal() }

        recyclerFlavours.apply {
            layoutManager = LinearLayoutManager(
                this@Flavour,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = this@Flavour.adapter
            setHasFixedSize(true)
        }

        updateTotal()
        fetchFlavours()

        btnBack.setOnClickListener { finish() }

        btnContinue.setOnClickListener {
            val selected = flavours.filter { it.scoops > 0 }
            if (selected.isEmpty()) {
                Toast.makeText(this, "Please select at least one scoop", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // navigate to next screen and pass selected flavours along with bases
            val intent = Intent(this, ChooseBaseActivity::class.java).apply {
                putExtra("selected_flavours", ArrayList(selected))
                putExtra("base_price", totalPrice)
                putExtra("bases", ArrayList(basesList))
            }
            startActivity(intent)
        }
    }

    private fun fetchFlavours() {
        val lottieLoading = findViewById<com.airbnb.lottie.LottieAnimationView>(R.id.lottie_loading)
        lottieLoading.visibility = android.view.View.VISIBLE
        recyclerFlavours.visibility = android.view.View.INVISIBLE

        val deviceId = com.example.smartshake.Utils.MachineIdManager.getDeviceId(this)
        
        ApiClient.apiService.getMachineStock(deviceId).enqueue(object : Callback<com.example.smartshake.data.model.MachineStockResponse> {
            override fun onResponse(
                call: Call<com.example.smartshake.data.model.MachineStockResponse>,
                response: Response<com.example.smartshake.data.model.MachineStockResponse>
            ) {
                lottieLoading.visibility = android.view.View.GONE
                recyclerFlavours.visibility = android.view.View.VISIBLE

                if (response.isSuccessful && response.body()?.status == true) {
                    response.body()?.flavors?.let {
                        flavours.clear()
                        flavours.addAll(it)
                        adapter.notifyDataSetChanged()
                        updateTotal()
                    }
                    response.body()?.bases?.let {
                        basesList.clear()
                        basesList.addAll(it)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("Flavour", "Failed to fetch stock: ${response.code()}, $errorBody")
                    Toast.makeText(this@Flavour, "Failed to fetch stock: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<com.example.smartshake.data.model.MachineStockResponse>, t: Throwable) {
                lottieLoading.visibility = android.view.View.GONE
                recyclerFlavours.visibility = android.view.View.VISIBLE
                Toast.makeText(this@Flavour, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTotal() {
        val totalScoops = flavours.sumOf { it.scoops }
        totalPrice = flavours.sumOf { it.scoops * (it.price.toDoubleOrNull()?.toInt() ?: 0) }
        tvPrice.text = "₹ $totalPrice"

        val isEnabled = totalScoops > 0
        btnContinue.isEnabled = isEnabled
        btnContinue.alpha = if (isEnabled) 1f else 0.5f

        btnContinue.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(if (isEnabled) "#B8E04A" else "#626262")
        )
    }

    private fun hideSystemBars() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}