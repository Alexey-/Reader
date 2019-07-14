package com.example.base.ui.alerts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.example.base.R

object SnackbarManager {

    class SnackbarButton(
        val message: String,
        val action: () -> Unit
    )

    enum class SnackbarStyle(
        @ColorRes internal val backgroundColor: Int,
        @ColorRes internal val textColor: Int,
        @ColorRes internal val actionColor: Int
    ) {
        INFO(R.color.primary, R.color.text_primary, R.color.text_inverse),
        SUCCESS(R.color.secondary, R.color.text_primary, R.color.text_primary),
        ERROR(R.color.error, R.color.text_primary, R.color.text_primary)
    }

    private var previousDisplayedSnackbar: Snackbar? = null

    fun show(context: Context, @StringRes message: Int, style: SnackbarStyle = SnackbarManager.SnackbarStyle.INFO, button: SnackbarButton? = null): Snackbar {
        return show(context, context.getString(message), style, button)
    }

    fun show(context: Context, message: String, style: SnackbarStyle = SnackbarManager.SnackbarStyle.INFO, button: SnackbarButton? = null): Snackbar {
        previousDisplayedSnackbar?.dismiss()

        val rootView = (context as Activity).findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        button?.let {
            snackbar
                .setAction(button.message) { button.action() }
                .setActionTextColor(ContextCompat.getColor(context, style.actionColor))
        }

        snackbar.findTextView().apply {
            setTextColor(ContextCompat.getColor(context, style.textColor))
            maxLines = 10
            gravity = Gravity.CENTER_VERTICAL
        }
        snackbar.setBackgroundColor(style.backgroundColor)
        previousDisplayedSnackbar = snackbar
        snackbar.show()
        return snackbar
    }

    enum class PredefinedSnackbar {
        NO_INTERNET;
    }

    private const val MIN_INTERVAL_BETWEEN_INTERNET_ALERTS_SEC = 15
    private var lastNoInternetAlert = 0L

    fun show(context: Context, snackbar: PredefinedSnackbar): Snackbar? {
        return when (snackbar) {
            SnackbarManager.PredefinedSnackbar.NO_INTERNET -> {
                val currentTime = System.currentTimeMillis()
                val timeSinceLastAlert = currentTime - lastNoInternetAlert
                if (timeSinceLastAlert / 1000 > MIN_INTERVAL_BETWEEN_INTERNET_ALERTS_SEC) {
                    lastNoInternetAlert = currentTime
                    show(context,
                        context.getString(R.string.generic_no_internet),
                        SnackbarManager.SnackbarStyle.INFO,
                        SnackbarButton(context.getString(R.string.generic_settings)) {
                            context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                        })
                } else {
                    null
                }
            }
        }
    }

}

fun Snackbar.findTextView(): TextView {
    return view.findViewById(com.google.android.material.R.id.snackbar_text)
}

fun Snackbar.setBackgroundColor(@ColorRes colorRes: Int): Snackbar {
    view.setBackgroundColor(ContextCompat.getColor(view.context, colorRes))
    return this
}