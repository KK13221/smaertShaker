package com.example.smartshake

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.smartshake.Utils.Utils
import com.example.smartshake.data.model.FlavourItem
import com.example.smartshake.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable immersive mode
        Utils.hideSystemBars(this)
        
        setContentView(R.layout.activity_main)

        val startShake = findViewById<FrameLayout>(R.id.layoutStart)
        startShake.setOnClickListener {
            val intent = Intent(this, Flavour::class.java)
            startActivity(intent)
            /*getFlavours()*/
        }
    }
}