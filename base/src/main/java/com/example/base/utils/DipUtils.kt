package com.example.base.utils

import com.example.base.core.BaseApplication

fun Int.toPx(): Int {
    return Math.round(this * BaseApplication.context.resources.displayMetrics.density)
}

fun Float.toPx(): Float {
    return this * BaseApplication.context.resources.displayMetrics.density
}

fun Int.toDp(): Int {
    return Math.round(this / BaseApplication.context.resources.displayMetrics.density)
}

fun Float.toDp(): Float {
    return this / BaseApplication.context.resources.displayMetrics.density
}

