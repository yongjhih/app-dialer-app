package com.github.yongjhih.appdialer

import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.ui.MainAppWidget
import com.github.yongjhih.appdialer.util.selfAndChildren

class MainActivity : ComponentActivity() {

    private var resetSignal by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        disablePendingTransition()
        super.onCreate(savedInstanceState)

        applyTransparentWindow()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        applyTransparentWindow()

        setContent {
            MainAppWidget(
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
    }

    private fun applyTransparentWindow() {
        window.setFormat(PixelFormat.TRANSLUCENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = window.attributes.apply {
            dimAmount = AppDefaults.BACKGROUND_DIM_AMOUNT
            format = PixelFormat.TRANSLUCENT
        }

        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        window.decorView.background = null
    }

    override fun finish() {
        super.finish()
        disablePendingTransition()
    }

    private fun disablePendingTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}
