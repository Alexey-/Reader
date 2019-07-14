package com.example.base.ui.base

import android.content.Intent
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.provider.Settings
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AlertDialog
import android.view.View
import com.example.base.R
import com.example.base.core.MainSchedulers
import com.example.base.model.base.INetworkResource
import com.example.base.model.base.NetworkResource
import com.example.base.model.network.errors.ServerCode
import com.example.base.model.network.errors.ServerErrorException
import com.example.base.ui.alerts.SnackbarManager
import com.example.base.ui.view.ErrorView

abstract class NetworkResourceFragment<DataType, T : ViewDataBinding> : BaseFragment<T>() {

    protected enum class ScreenState {
        NOT_LOADED,
        NOT_LOADED_AND_ERROR,
        LOADED_AND_EMPTY,
        LOADED;

        fun isAtLeast(state: ScreenState): Boolean {
            return ordinal >= state.ordinal
        }
    }

    private var currentState: NetworkResource.State? = null
    private var currentData: DataType? = null
    private var isInitialDataReported: Boolean = false

    protected abstract val networkResource: INetworkResource<DataType>

    protected abstract val swipeRefreshLayout: SwipeRefreshLayout?

    protected abstract val dataView: View

    protected abstract val emptyDataView: View?

    protected abstract val errorView: ErrorView?

    protected abstract fun isDataEmpty(data: DataType): Boolean

    protected abstract fun onDataChanged(data: DataType)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout?.setOnRefreshListener { networkResource.update() }
        screenState = ScreenState.NOT_LOADED
    }

    override fun onStart() {
        super.onStart()
        isInitialDataReported = false

        networkResource.updateIfNeeded()

        disposeOnStop(networkResource.observable
            .observeOn(MainSchedulers.uiScheduler)
            .subscribe { snapshot ->
                val previousData = currentData
                currentState = snapshot.state
                currentData = snapshot.data

                swipeRefreshLayout?.isRefreshing = currentState!!.isUpdating

                refreshScreenState()

                if (currentData != null && (previousData == null || snapshot.isDataChanged || !isInitialDataReported)) {
                    isInitialDataReported = true
                    onDataChanged(currentData!!)
                }
            })

        disposeOnStop(networkResource.errorObservable
            .observeOn(MainSchedulers.uiScheduler)
            .subscribe { error ->
                displayUpdateError(error)
            })
    }

    private fun refreshScreenState() {
        val state = currentState ?: return

        screenState = when {
            state.lastSuccessfulUpdate == null && state.lastError == null -> ScreenState.NOT_LOADED
            state.lastSuccessfulUpdate == null && state.lastError != null -> ScreenState.NOT_LOADED_AND_ERROR
            currentData == null || isDataEmpty(currentData!!) -> ScreenState.LOADED_AND_EMPTY
            else -> ScreenState.LOADED
        }
    }

    private var screenState: ScreenState = ScreenState.NOT_LOADED
        set(value) {
            if (field != value) {
                field = value

                when (value) {
                    ScreenState.NOT_LOADED -> {
                        dataView.visibility = View.GONE
                        emptyDataView?.visibility = View.GONE
                        errorView?.visibility = View.GONE
                    }
                    ScreenState.NOT_LOADED_AND_ERROR -> {
                        dataView.visibility = View.GONE
                        emptyDataView?.visibility = View.GONE
                        errorView?.visibility = View.VISIBLE
                        errorView?.error = currentState!!.lastError
                    }
                    ScreenState.LOADED_AND_EMPTY -> {
                        dataView.visibility = View.GONE
                        emptyDataView?.visibility = View.VISIBLE
                        errorView?.visibility = View.GONE
                    }
                    ScreenState.LOADED -> {
                        dataView.visibility = View.VISIBLE
                        emptyDataView?.visibility = View.GONE
                        errorView?.visibility = View.GONE
                    }
                }
            }
        }

    private fun displayUpdateError(error: Throwable) {
        if (screenState.isAtLeast(ScreenState.LOADED_AND_EMPTY)) {
            val context = context!!
            if (error is ServerErrorException && error.serverError.code === ServerCode.NETWORK_ERROR) {
                SnackbarManager.show(context, SnackbarManager.PredefinedSnackbar.NO_INTERNET)
            } else {
                SnackbarManager.show(context, R.string.generic_unknown_error, SnackbarManager.SnackbarStyle.ERROR,
                    SnackbarManager.SnackbarButton(context.getString(R.string.generic_details)) {
                        AlertDialog.Builder(context)
                            .setTitle(R.string.generic_unknown_error)
                            .setMessage(error.message)
                            .setPositiveButton(R.string.generic_ok, null)
                            .create().show()
                    })
            }
        }
    }

}
