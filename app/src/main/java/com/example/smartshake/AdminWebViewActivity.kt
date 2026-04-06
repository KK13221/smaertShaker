package com.example.smartshake

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.smartshake.Utils.Utils

class AdminWebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide system bars for a clean panel look
        Utils.hideSystemBars(this)
        
        setContentView(R.layout.activity_admin_web_view)

        val webView: WebView = findViewById(R.id.webViewAdmin)
        
        // Configure WebView settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        
        webView.webViewClient = WebViewClient()
        
        // Load the admin panel URL
        webView.loadUrl("http://139.59.57.87:3003/")
    }

    override fun onBackPressed() {
        val webView: WebView = findViewById(R.id.webViewAdmin)
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
