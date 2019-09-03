package com.example.reader

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.StrictMode
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.base.core.BaseApplication
import com.example.reader.di.DaggerMainComponent
import com.example.reader.di.MainModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import javax.inject.Inject

class MainApplication : BaseApplication(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    internal lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>
    @Inject
    internal lateinit var dispatchingSupportFragmentInjector: DispatchingAndroidInjector<Fragment>

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

    }

    override fun onCreate() {
        super.onCreate()
        context = this

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeathOnNetwork()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            WebView.setWebContentsDebuggingEnabled(true)

            Timber.plant(Timber.DebugTree())
        }
        Timber.d("Application started")

        JodaTimeAndroid.init(this)

        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException) {
                Timber.e(throwable, "Undeliverable exeception")
            } else {
                val currentThread = Thread.currentThread()
                currentThread.uncaughtExceptionHandler.uncaughtException(currentThread, throwable)
            }
        }

        val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            Timber.e(e, "Uncaught exception")
            exceptionHandler.uncaughtException(thread, e)
        }

        val mainComponent = DaggerMainComponent.builder()
            .mainModule(MainModule())
            .build()
        mainComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingActivityInjector
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingSupportFragmentInjector
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        Timber.d("Activity started: ${activity.javaClass.simpleName}")
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
        Timber.d("Activity stopped: ${activity.javaClass.simpleName}")
    }

}