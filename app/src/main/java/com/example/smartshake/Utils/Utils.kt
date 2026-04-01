package com.example.smartshake.Utils

import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class Utils {
    companion object{
        fun hideSystemBars(activity: Activity) {

            val window = activity.window

            val windowInsetsController =
                WindowCompat.getInsetsController(window, window.decorView)

            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            windowInsetsController.hide(
                WindowInsetsCompat.Type.systemBars()
            )
        }
    }
}