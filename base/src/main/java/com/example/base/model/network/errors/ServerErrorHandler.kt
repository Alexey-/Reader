package com.example.base.model.network.errors

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.example.base.R
import com.example.base.ui.alerts.SnackbarManager
import com.example.base.utils.InternetConnectionUtils
import timber.log.Timber

object ServerErrorHandler {

    fun handleError(
        throwable: Throwable,
        context: Context,
        important: Boolean,
        customErrorHandler: ((ServerError) -> Boolean)? = null,
        customUnknownErrorHandler: ((Throwable) -> Boolean)? = null
    ) {
        if (throwable is ServerErrorException) {
            val serverError = throwable.serverError
            Timber.e("Server error received $serverError")
            val handled = customErrorHandler?.invoke(serverError) ?: false
            if (!handled) {
                if (serverError.code == ServerCode.NETWORK_ERROR) {
                    handleNetworkError(context, serverError, important)
                } else {
                    handleUnknownError(context, throwable, important)
                }
            }
        } else {
            Timber.e(throwable, "Unknown server error")
            val handled = customUnknownErrorHandler?.invoke(throwable) ?: false
            if (!handled) {
                handleUnknownError(context, throwable, important)
            }
        }
    }


    private fun handleNetworkError(context: Context, error: ServerError, important: Boolean) {
        if (important) {
            val builder = AlertDialog.Builder(context)
            if (InternetConnectionUtils.isNetworkAvailable(context)) {
                builder.setMessage(R.string.generic_network_error)
                builder.setPositiveButton(R.string.generic_ok, null)
            } else {
                builder.setMessage(R.string.generic_no_internet)
                builder.setPositiveButton(R.string.generic_settings) { _, _ ->
                    context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }
                builder.setNegativeButton(R.string.generic_cancel, null)
            }
            builder.create().show()
        } else {
            if (InternetConnectionUtils.isNetworkAvailable(context)) {
                SnackbarManager.show(context, context.getString(R.string.generic_network_error), SnackbarManager.SnackbarStyle.ERROR)
            } else {
                SnackbarManager.show(context, SnackbarManager.PredefinedSnackbar.NO_INTERNET)
            }
        }
    }

    private fun handleUnknownError(context: Context, error: Throwable, important: Boolean) {
        if (important) {
            var extendedMessage = error.javaClass.simpleName
            if (!error.message.isNullOrBlank()) {
                extendedMessage += ": " + error.message
            }
            val finalMessage = extendedMessage

            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.generic_unknown_error)
            builder.setPositiveButton(R.string.generic_ok, null)
            builder.setNegativeButton(R.string.generic_details) { _, _ ->
                val detailsBuilder = AlertDialog.Builder(context)
                detailsBuilder.setMessage(finalMessage)
                detailsBuilder.setPositiveButton(R.string.generic_ok, null)
                detailsBuilder.create().show()
            }
            builder.create().show()
        } else {
            SnackbarManager.show(context, context.getString(R.string.generic_unknown_error), SnackbarManager.SnackbarStyle.ERROR)
        }
    }

}
