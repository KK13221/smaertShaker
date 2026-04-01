package com.example.smartshake

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.smartshake.data.model.BaseItem
import com.example.smartshake.data.model.FlavourItem
import com.example.smartshake.network.ApiClient
import com.google.android.material.button.MaterialButton
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseBaseActivity : AppCompatActivity() {

    private var selectedBaseItem: BaseItem? = null
    private var basePriceFromFlavours = 0
    private var bases = mutableListOf<BaseItem>()

    // nullable — never throws UninitializedPropertyAccessException
    private var cardMilk: FrameLayout? = null
    private var cardWater: FrameLayout? = null
    private var tvPrice: TextView? = null
    private var btnBack: MaterialButton? = null
    private var btnContinue: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_base)   // always first
        hideSystemBars()

        cardMilk    = findViewById(R.id.cardMilk)
        cardWater   = findViewById(R.id.card_water)
        tvPrice     = findViewById(R.id.tv_price)
        btnBack     = findViewById(R.id.btn_back)
        btnContinue = findViewById(R.id.btn_continue)

        updateContinueButton()

        val selectedFlavours = intent.getSerializableExtra("selected_flavours") as? ArrayList<FlavourItem>
        basePriceFromFlavours = intent.getIntExtra("base_price", 0)
        tvPrice?.text = "₹ $basePriceFromFlavours"
        btnBack?.setOnClickListener { finish() }

        btnContinue?.setOnClickListener {
            if (selectedBaseItem == null) {
                Toast.makeText(this, "Please select a base", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val intent = Intent(this, PaymentActivity::class.java).apply {
                putExtra("selected_flavours", selectedFlavours)
                putExtra("selected_base_item", selectedBaseItem)
            }
            startActivity(intent)
        }

        val passedBases = intent.getSerializableExtra("bases") as? ArrayList<BaseItem>
        if (passedBases != null) {
            bases.clear()
            bases.addAll(passedBases)
            setupBaseUI()
        } else {
            Toast.makeText(this, "Error: No bases found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBaseUI() {
        val milkItem = bases.find { it.name.lowercase() == "milk" }
        val waterItem = bases.find { it.name.lowercase() == "water" }

        // Update Milk UI
        milkItem?.let { item ->
            findViewById<TextView>(R.id.tv_milk_name)?.text = item.name
            findViewById<ImageView>(R.id.iv_milk_image)?.let { iv ->
                Glide.with(this).load(item.image).placeholder(R.drawable.milk).into(iv)
            }
            cardMilk?.setOnClickListener { updateSelection(item) }
        }

        // Update Water UI
        waterItem?.let { item ->
            findViewById<TextView>(R.id.tv_water_name)?.text = item.name
            findViewById<ImageView>(R.id.iv_water_image)?.let { iv ->
                Glide.with(this).load(item.image).placeholder(R.drawable.water).into(iv)
            }
            cardWater?.setOnClickListener { updateSelection(item) }
        }
    }

    private fun updateSelection(selected: BaseItem) {
        selectedBaseItem = selected

        val milkCardBg  = cardMilk?.getChildAt(0)
        val waterCardBg = cardWater?.getChildAt(0)

        if (selected.name.lowercase() == "milk") {
            milkCardBg?.setBackgroundResource(R.drawable.base_card_bg_selected)
            waterCardBg?.setBackgroundResource(R.drawable.base_card_bg)
        } else {
            waterCardBg?.setBackgroundResource(R.drawable.base_card_bg_selected)
            milkCardBg?.setBackgroundResource(R.drawable.base_card_bg)
        }
        tvPrice?.text = "₹ ${basePriceFromFlavours + selected.price}"

        updateContinueButton()
    }

    private fun updateContinueButton() {
        val btn = btnContinue ?: return  // safely bail if somehow null
        val isSelected = selectedBaseItem != null
        btn.isEnabled = isSelected
        btn.alpha = if (isSelected) 1f else 0.5f
        btn.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(if (isSelected) "#B8E04A" else "#626262")
        )
    }

    private fun hideSystemBars() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}