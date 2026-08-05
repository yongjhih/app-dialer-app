package com.github.yongjhih.appdialer

import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.github.yongjhih.appdialer.feature.dialer.android.AndroidMainAppWidget
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.util.AppLoader
import com.github.yongjhih.appdialer.util.selfAndChildren
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private companion object {
        private const val TAG = "MainActivity"
    }

    private var resetSignal by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate (resetSignal=$resetSignal)")

        // Pre-warm AppLoader cache in background thread immediately on activity launch
        lifecycleScope.launch(Dispatchers.IO) {
            AppLoader.loadInstalledApps(applicationContext)
        }

        applyTransparentWindow()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        applyTransparentWindow()

        setContent {
            AndroidMainAppWidget(
                resetSignal = resetSignal,
                onDismiss = { finish() },
                onApplyTransparentWindow = { applyTransparentWindow() }
            )
        }

        findViewById<View>(android.R.id.content)?.selfAndChildren?.forEach { view ->
            view.setBackgroundColor(Color.TRANSPARENT)
            view.background = null
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        resetSignal++
        Log.d(TAG, "onNewIntent (resetSignal=$resetSignal)")
    }

    private fun applyTransparentWindow() {
        window.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            val dimAmount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AppDefaults.BACKGROUND_DIM_AMOUNT
            } else {
                0.6f
            }
            setDimAmount(dimAmount)
            setFormat(PixelFormat.TRANSLUCENT)
        }
    }
}
