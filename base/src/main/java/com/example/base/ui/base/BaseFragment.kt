package com.example.base.ui.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding: T
        get() = _binding ?: throw Exception("View not created")

    protected abstract val layoutId: Int

    var isStarted: Boolean = false
        private set

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

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

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
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeOnDestroy.dispose()
        disposeOnDestroy = CompositeDisposable()
    }

}
