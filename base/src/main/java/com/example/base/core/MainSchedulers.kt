package com.example.base.core

import androidx.annotation.VisibleForTesting
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import java.util.concurrent.Executors

object MainSchedulers {

    var databaseScheduler = Schedulers.io()
        private set
    var networkScheduler = Schedulers.from(Executors.newFixedThreadPool(3))
        private set
    var uiScheduler = AndroidSchedulers.mainThread()
        private set

    @VisibleForTesting
    fun setSchedulers(databaseScheduler: Scheduler, networkScheduler: Scheduler, uiScheduler: Scheduler) {
        this.databaseScheduler = databaseScheduler
        this.networkScheduler = networkScheduler
        this.uiScheduler = uiScheduler
    }

}
