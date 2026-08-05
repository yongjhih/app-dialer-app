package com.github.yongjhih.appdialer.feature.dialer.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.github.yongjhih.appdialer.feature.dialer.R
import com.github.yongjhih.appdialer.model.AppDefaults
import com.github.yongjhih.appdialer.model.AppModel
import com.github.yongjhih.appdialer.ui.AppLauncher

class AndroidAppLauncher(
    private val context: Context,
    private val onDismiss: () -> Unit = {}
) : AppLauncher {

    private companion object {
        private const val TAG = "AndroidAppLauncher"
    }

    override fun launchApp(app: AppModel) {
        try {
            Log.i(TAG, "Launching app: ${app.label} (${app.packageName}/${app.className})")
            val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
                onDismiss()
            } else {
                Log.w(TAG, "Launch intent not found for package: ${app.packageName}")
                Toast.makeText(context, context.getString(R.string.cannot_launch_app, app.label), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error launching app: ${app.label}", e)
            Toast.makeText(context, context.getString(R.string.error_launching_app, e.message.orEmpty()), Toast.LENGTH_SHORT).show()
        }
    }

    override fun openAppDetails(app: AppModel) {
        try {
            Log.i(TAG, "Opening app details: ${app.label} (${app.packageName})")
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(AppDefaults.PACKAGE_URI_SCHEME + app.packageName)
            )
            context.startActivity(intent)
            onDismiss()
        } catch (e: Exception) {
            Log.e(TAG, "Error opening app details for: ${app.packageName}", e)
            Toast.makeText(context, context.getString(R.string.cannot_open_app_settings, app.label), Toast.LENGTH_SHORT).show()
        }
    }

    override fun openSystemAppSettings() {
        try {
            Log.i(TAG, "Opening AppDialer system app settings")
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(AppDefaults.PACKAGE_URI_SCHEME + context.packageName)
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening system app settings", e)
            Toast.makeText(context, R.string.cannot_open_settings, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
