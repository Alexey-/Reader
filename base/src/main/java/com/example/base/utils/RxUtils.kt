package com.example.base.utils

import io.reactivex.Single
import io.reactivex.disposables.Disposable

/** This function is only needed to avoid rxjava warning about ignoring disposable */
fun Disposable.noUnsubscribe() {

}