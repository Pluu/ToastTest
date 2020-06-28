package com.pluu.alert

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.updatePadding
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

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
        messageView.doOnApplyWindowInsets { v, windowInsets, initialPadding ->
            if (builder.gravity.isTop()) {
                messageView.setText(buildString {
                    append(builder.title)
                    append(System.lineSeparator())
                    append("top=${windowInsets.systemWindowInsetTop}")
                })
                v.updatePadding(top = windowInsets.systemWindowInsetTop + initialPadding.top)
            } else {
                messageView.setText(buildString {
                    append(builder.title)
                    append(System.lineSeparator())
                    append("bottom=${windowInsets.systemWindowInsetBottom}")
                })
                v.updatePadding(bottom = windowInsets.systemWindowInsetBottom + initialPadding.bottom)
            }
        }

//        if (builder.gravity.isTop()) {
//            messageView.applySystemWindowInsetsToPadding(top = true)
//        } else {
//            messageView.applySystemWindowInsetsToPadding(bottom = true)
//        }

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
        private val view: View
    ) {
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
        }
    }

    private fun GravityType.isTop() = this == GravityType.TOP

    enum class GravityType { TOP, BOTTOM }

    companion object {
        fun make(activity: Activity): Builder {
            return Builder(activity.window.decorView)
        }

        fun make(view: ViewGroup): Builder {
            return Builder(view).setParent(view)
        }

        fun make(activity: Activity, title: String): Builder {
            return make(activity)
                .setTitle(title)
        }

        fun make(view: ViewGroup, title: String): Builder {
            return make(view)
                .setTitle(title)
        }
    }
}