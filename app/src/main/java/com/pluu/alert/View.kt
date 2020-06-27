package com.pluu.alert

import android.view.View
import android.view.ViewTreeObserver

inline fun <reified T : View> T.doOnGlobalLayout(
    crossinline action: T.() -> Unit
) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action()
        }
    })
}