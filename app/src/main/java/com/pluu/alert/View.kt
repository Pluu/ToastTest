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

fun isFullscreen(topLeftView: View): Boolean {
    val location = IntArray(2)
    topLeftView.getLocationOnScreen(location)
    return location[0] == 0 && location[1] == 0
}