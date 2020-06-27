package com.pluu.alert

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.example.toastexanoke.R
import com.example.toastexanoke.databinding.WidgetMessageBinding

class PluuMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: WidgetMessageBinding

    init {
        val view = inflate(context, R.layout.widget_message, this)
        binding = WidgetMessageBinding.bind(view)
    }

    fun setText(text: String) {
        binding.tvMessage.text = text
    }
}