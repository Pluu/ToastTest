package com.pluu.alert

import android.os.Build
import android.view.Window
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
fun Window.safeInsetTop() = decorView.rootWindowInsets?.systemWindowInsetTop ?: 0

@RequiresApi(Build.VERSION_CODES.P)
fun Window.safeInsetBottom() = decorView.rootWindowInsets?.systemWindowInsetBottom ?: 0
