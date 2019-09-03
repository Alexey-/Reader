package com.example.base.ui.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.base.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

abstract class BaseFragment : Fragment() {

    val routingActivity: RoutingActivity
        get() = activity as RoutingActivity

    private var disposeOnStop = CompositeDisposable()
    private var disposeOnDestroyView = CompositeDisposable()
    private var disposeOnDestroy = CompositeDisposable()

    protected fun disposeOnStop(disposable: Disposable) {
        disposeOnStop.add(disposable)
    }

    protected fun disposeOnDestroyView(disposable: Disposable) {
        disposeOnDestroyView.add(disposable)
    }

    protected fun disposeOnDestroy(disposable: Disposable) {
        disposeOnDestroy.add(disposable)
    }

    protected var toolbar: Toolbar? = null

    fun setupToolbar(toolbar: Toolbar) {
        this.toolbar = toolbar
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { v ->
            if (!onBackPressed()) {
                routingActivity.popFragment()
            }
        }
        refreshTitle()
    }

    abstract val title: String?

    protected fun refreshTitle() {
        toolbar?.findViewById<TextView>(R.id.title_view)?.text = title
    }

    var isStarted: Boolean = false
        private set

    override fun onStart() {
        super.onStart()
        Timber.v("Fragment started: " + javaClass.simpleName)
        isStarted = true
    }

    override fun onStop() {
        super.onStop()
        Timber.v("Fragment stopped: " + javaClass.simpleName)
        isStarted = false
        disposeOnStop.dispose()
        disposeOnStop = CompositeDisposable()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposeOnDestroyView.dispose()
        disposeOnDestroyView = CompositeDisposable()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeOnDestroy.dispose()
        disposeOnDestroy = CompositeDisposable()
    }

    open fun onBackPressed(): Boolean {
        return false
    }

}
