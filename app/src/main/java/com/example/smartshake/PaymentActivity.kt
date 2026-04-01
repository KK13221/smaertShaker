package com.example.smartshake

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartshake.Utils.Utils
import com.google.android.material.button.MaterialButton
import com.bumptech.glide.Glide
import com.example.smartshake.data.model.BaseItem
import com.example.smartshake.data.model.FlavourItem
import com.example.smartshake.network.ApiClient
import com.example.smartshake.data.model.QrRequest
import com.example.smartshake.data.model.QrResponse
import com.example.smartshake.Utils.QrCropTransformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {

    private var selectedFlavours: ArrayList<FlavourItem>? = null
    private var selectedBaseItem: BaseItem? = null
    private var lastFetchedAmount = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.hideSystemBars(this)
        setContentView(R.layout.activity_payment)

        // Receive data from Intent
        @Suppress("DEPRECATION")
        selectedFlavours = intent.getSerializableExtra("selected_flavours") as? ArrayList<FlavourItem>
        @Suppress("DEPRECATION")
        selectedBaseItem = intent.getSerializableExtra("selected_base_item") as? BaseItem

        setupUI()
    }

    private fun setupUI() {
        // Back Button
        findViewById<MaterialButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Flavours RecyclerView
        val rvFlavours = findViewById<RecyclerView>(R.id.rv_flavour_summary)
        selectedFlavours?.let { flavours ->
            val selection = flavours.filter { it.scoops > 0 }
            
            rvFlavours.layoutManager = LinearLayoutManager(this)
            rvFlavours.adapter = FlavourAdapter(selection, R.layout.item_payment_flavour) { 
                updateSummary() 
            }
        }

        // Base Section
        val tvBaseName = findViewById<TextView>(R.id.tv_base_name)
        val tvBasePrice = findViewById<TextView>(R.id.tv_base_price)
        val ivBaseImage = findViewById<ImageView>(R.id.iv_base_image)

        tvBaseName.text = selectedBaseItem?.name?.replaceFirstChar { it.uppercase() } ?: "None"
        tvBasePrice.text = "₹ ${selectedBaseItem?.price ?: 0}"
        
        selectedBaseItem?.let { item ->
            Glide.with(this).load(item.image).placeholder(R.drawable.mk).into(ivBaseImage)
        }

        updateSummary()
    }

    private fun updateSummary() {
        var totalCalories = 0
        var totalProtein = 0
        var totalCarbs = 0
        var totalFat = 0
        var totalFibre = 0
        var totalSugar = 0

        selectedFlavours?.forEach { flavour ->
            val scoops = flavour.scoops
            val calories = flavour.Colories?.toIntOrNull() ?: 0
            val protein = flavour.Protein?.toIntOrNull() ?: 0
            val carbs = flavour.Carbs?.toIntOrNull() ?: 0
            val fat = flavour.Fat?.toIntOrNull() ?: 0
            val fibre = flavour.Fibre?.toIntOrNull() ?: 0
            val sugar = flavour.Sugar?.toIntOrNull() ?: 0
            
            totalCalories += scoops * calories
            totalProtein += scoops * protein
            totalCarbs += scoops * carbs
            totalFat += scoops * fat
            totalFibre += scoops * fibre
            totalSugar += scoops * sugar
        }

        // Update Nutritional Info
        findViewById<TextView>(R.id.tv_calories).text = "$totalCalories kcal"
        findViewById<TextView>(R.id.tv_protein).text = "${totalProtein} g"
        findViewById<TextView>(R.id.tv_carbs).text = "${totalCarbs} g"
        findViewById<TextView>(R.id.tv_fat).text = "${totalFat} g"
        findViewById<TextView>(R.id.tv_fibre).text = "${totalFibre} g"
        findViewById<TextView>(R.id.tv_sugar).text = "${totalSugar} g"

        // Update Order Summary
        val flavoursTotal = selectedFlavours?.sumOf { it.scoops * (it.price.toDoubleOrNull()?.toInt() ?: 0) } ?: 0
        val subtotal = flavoursTotal + (selectedBaseItem?.price ?: 0)
        val tax = (subtotal * 0.04).toInt()
        val total = subtotal + tax

        findViewById<TextView>(R.id.tv_subtotal).text = "₹ $subtotal"
        findViewById<TextView>(R.id.tv_tax).text = "₹ $tax"
        findViewById<TextView>(R.id.tv_total).text = "₹ $total"

        // Fetch QR Code for the total amount
        if (total != lastFetchedAmount) {
            fetchQrCode(total)
        }
    }

    private fun fetchQrCode(amount: Int) {
        lastFetchedAmount = amount
        val request = QrRequest(amount)

        val ivQrCode = findViewById<ImageView>(R.id.iv_qr_code)
        val lottieLoading = findViewById<com.airbnb.lottie.LottieAnimationView>(R.id.lottie_loading)

        ivQrCode.visibility = android.view.View.INVISIBLE
        lottieLoading.visibility = android.view.View.VISIBLE

        ApiClient.apiService.createQrCode(request).enqueue(object : Callback<QrResponse> {
            override fun onResponse(call: Call<QrResponse>, response: Response<QrResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val it = response.body()!!
                    // Remove background and padding to show only the QR
                    ivQrCode.setPadding(0, 0, 0, 0)
                    ivQrCode.background = null

                    Glide.with(this@PaymentActivity)
                        .load(it.qr_image)
                        .transform(QrCropTransformation())
                        .into(object : com.bumptech.glide.request.target.CustomTarget<android.graphics.drawable.Drawable>() {
                            override fun onResourceReady(
                                resource: android.graphics.drawable.Drawable,
                                transition: com.bumptech.glide.request.transition.Transition<in android.graphics.drawable.Drawable>?
                            ) {
                                lottieLoading.visibility = android.view.View.GONE
                                ivQrCode.visibility = android.view.View.VISIBLE
                                ivQrCode.setImageDrawable(resource)
                            }

                            override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                                ivQrCode.setImageDrawable(placeholder)
                            }

                            override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                                lottieLoading.visibility = android.view.View.GONE
                                ivQrCode.visibility = android.view.View.VISIBLE
                                ivQrCode.setImageDrawable(errorDrawable)
                            }
                        })
                } else {
                    lottieLoading.visibility = android.view.View.GONE
                    ivQrCode.visibility = android.view.View.VISIBLE
                }
            }

            override fun onFailure(call: Call<QrResponse>, t: Throwable) {
                lottieLoading.visibility = android.view.View.GONE
                ivQrCode.visibility = android.view.View.VISIBLE
                
                // Silently fail or log error
                android.util.Log.e("PaymentActivity", "Failed to fetch QR code: ${t.message}")
            }
        })
    }
}