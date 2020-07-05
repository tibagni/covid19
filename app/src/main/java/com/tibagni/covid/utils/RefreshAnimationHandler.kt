package com.tibagni.covid.utils

import android.content.Context
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.tibagni.covid.R

class RefreshAnimationHandler(
    context: Context,
    private val refreshItem: MenuItem
) {
    private val animation = AnimationUtils.loadAnimation(context, R.anim.spin_clockwise)
    var isRefreshing: Boolean = false
        set(value) {
            if (value == field)
                return

            field = value
            if (value) {
                stopAnimation()
                refreshItem.setActionView(R.layout.refresh_action_view)
                refreshItem.actionView?.startAnimation(animation)
            }
        }

    init {
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                if (!isRefreshing) {
                    stopAnimation()
                }
            }
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
        })
        animation.repeatCount = Animation.INFINITE
    }

    private fun stopAnimation() {
        refreshItem.actionView?.clearAnimation()
        refreshItem.actionView = null
    }
}