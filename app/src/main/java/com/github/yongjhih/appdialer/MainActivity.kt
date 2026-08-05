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
import com.github.yongjhih.appdialer.feature.dialer.android.AndroidMainAppWidget
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.util.AppLoader
import com.github.yongjhih.appdialer.util.selfAndChildren

class MainActivity : ComponentActivity() {

    private companion object {
        private const val TAG = "MainActivity"
    }

    private var resetSignal by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        val createStart = System.currentTimeMillis()
        val appStart = AppDialerApplication.appStartTime.let { if (it > 0) it else createStart }
        Log.d("AppDialerTime", "[t=${createStart - appStart}ms] MainActivity.onCreate started")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate (resetSignal=$resetSignal)")

        // Synchronously retrieve disk cache (< 1ms) BEFORE setContent so Frame 1 renders immediately
        val syncStart = System.currentTimeMillis()
        AppLoader.loadInstalledAppsSync(applicationContext)
        val syncElapsed = System.currentTimeMillis() - syncStart
        Log.d("AppDialerTime", "[t=${System.currentTimeMillis() - appStart}ms] AppLoader.loadInstalledAppsSync completed in ${syncElapsed}ms")

        applyTransparentWindow()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        applyTransparentWindow()

        val setContentStart = System.currentTimeMillis()
        Log.d("AppDialerTime", "[t=${setContentStart - appStart}ms] Calling setContent {}...")

        setContent {
            AndroidMainAppWidget(
                resetSignal = resetSignal,
                onDismiss = { finish() },
                onApplyTransparentWindow = { applyTransparentWindow() }
            )
        }

        val setContentEnd = System.currentTimeMillis()
        Log.d("AppDialerTime", "[t=${setContentEnd - appStart}ms] setContent {} finished (took ${setContentEnd - setContentStart}ms)")

        window.decorView.post {
            val frameDrawnTime = System.currentTimeMillis()
            Log.d("AppDialerTime", "=== [t=${frameDrawnTime - appStart}ms] FIRST FRAME FULLY DRAWN ON SCREEN ===")
        }

        findViewById<View>(android.R.id.content)?.selfAndChildren?.forEach { view ->
            view.setBackgroundColor(Color.TRANSPARENT)
            view.background = null
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
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
