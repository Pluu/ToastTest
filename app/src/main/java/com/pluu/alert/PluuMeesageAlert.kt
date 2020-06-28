package com.pluu.alert

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.updatePadding
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.pluu.windowinset.BuildConfig

class PluuMeesageAlert private constructor(
    private val builder: Builder
) {
    private val messageView: PluuMessageView

    init {
        messageView = PluuMessageView(builder.parent.context)
        if (!builder.title.isNullOrEmpty()) {
            messageView.setText(builder.title!!)
        }
        messageView.setBackgroundColor(builder.backgroundColor)
    }

    fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val hasFullscreen = isFullscreen(builder.parent)
            if (hasFullscreen || !builder.isFixedParent) {
                if (BuildConfig.DEBUG) {
                    printDebugText()
                }
                if (builder.gravity.isTop()) {
                    messageView.updatePadding(top = messageView.paddingTop + builder.window.safeInsetTop())
                } else {
                    messageView.updatePadding(bottom = messageView.paddingBottom + builder.window.safeInsetBottom())
                }
            }
        }

        builder.parent.addView(
            messageView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                if (builder.gravity.isTop()) Gravity.TOP else Gravity.BOTTOM
            )
        )

        messageView.doOnGlobalLayout {
            translationY = getOriginalPosition(messageView)
            slideInAnimation(this)
        }
    }

    private fun slideInAnimation(view: View) {
        view.animate()
            .translationY(0f)
            .setDuration(builder.animationDuration)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction {
                slideOutAnimation(view)
            }
            .start()
    }

    private fun slideOutAnimation(view: View) {
        view.animate()
            .translationY(getOriginalPosition(view))
            .setStartDelay(builder.showDuration)
            .setDuration(builder.animationDuration)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction {
                view.clearAnimation()
                view.isGone = true
                builder.parent.removeView(view)
            }
            .start()
    }

    private fun getOriginalPosition(
        view: View
    ) = view.measuredHeight.toFloat() * if (builder.gravity.isTop()) {
        -1
    } else {
        1
    }

    class Builder internal constructor(
        private val activity: Activity
    ) {
        private val view = activity.window.decorView
        internal val window = activity.window

        lateinit var parent: ViewGroup
            private set
        internal var gravity = GravityType.TOP
            private set
        internal var title: String? = null
            private set
        internal var backgroundColor = Color.WHITE
            private set
        internal var animationDuration = 200L
            private set
        internal var showDuration = 1500L
            private set
        internal var isFixedParent = false
            private set

        fun setTitle(title: String) = apply {
            this.title = title
        }

        fun build(): PluuMeesageAlert {
            if (!::parent.isInitialized) {
                val parent = findSuitableParent(view)
                requireNotNull(parent) {
                    "No suitable parent found from the given view. Please provide a valid view."
                }
                this.parent = parent
            }
            clearCurrent(parent)

            return PluuMeesageAlert(this)
        }

        fun show() = build().show()

        private fun clearCurrent(parent: ViewGroup) {
            val count = parent.childCount
            (count - 1 downTo 0).forEach { index ->
                val childView: View? = parent.getChildAt(index)
                if (childView is PluuMessageView && childView.windowToken != null) {
                    parent.removeView(childView)
                }
            }
        }

        private fun findSuitableParent(view: View): ViewGroup? {
            var parentView: View? = view
            var fallback: ViewGroup? = null
            do {
                if (parentView is CoordinatorLayout) {
                    return parentView
                } else if (parentView is FrameLayout) {
                    fallback = if (parentView.getId() == android.R.id.content) {
                        return parentView
                    } else {
                        parentView
                    }
                }
                if (parentView != null) {
                    val parent = parentView.parent
                    parentView = if (parent is View) parent else null
                }
            } while (parentView != null)
            return fallback
        }

        fun setGravity(gravity: GravityType) = apply {
            this.gravity = gravity
        }

        fun setBackgroundColor(@ColorInt color: Int) = apply {
            this.backgroundColor = color
        }

        fun setBackgroundColorRes(@ColorRes colorRes: Int) = apply {
            this.backgroundColor = ContextCompat.getColor(view.context, colorRes)
        }

        fun setParent(parent: ViewGroup) = apply {
            this.parent = parent
            this.isFixedParent = true
        }
    }

    private fun GravityType.isTop() = this == GravityType.TOP

    enum class GravityType { TOP, BOTTOM }

    companion object {
        fun make(activity: Activity): Builder {
            return Builder(activity)
        }

        fun make(activity: Activity, title: String): Builder {
            return make(activity)
                .setTitle(title)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // DEBUG
    ///////////////////////////////////////////////////////////////////////////

    @RequiresApi(Build.VERSION_CODES.P)
    private fun printDebugText() {
        if (builder.gravity.isTop()) {
            messageView.setText(buildString {
                append(builder.title)
                append(System.lineSeparator())
                append("top=${builder.window.safeInsetTop()}")
            })
        } else {
            messageView.setText(buildString {
                append(builder.title)
                append(System.lineSeparator())
                append("bottom=${builder.window.safeInsetBottom()}")
            })
        }
    }
}