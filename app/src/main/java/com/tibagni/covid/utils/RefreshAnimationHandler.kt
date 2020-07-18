package com.tibagni.covid.utils

import android.content.Context
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.tibagni.covid.R

class RefreshAnimationHandler(
    context: Context,
    private val lifecycle: Lifecycle,
    private val refreshItem: MenuItem
): LifecycleObserver {
    private val animation = AnimationUtils.loadAnimation(context, R.anim.spin_clockwise)
    var isRefreshing: Boolean = false
        set(value) {
            if (value == field)
                return

            field = value
            if (value) {
                stopAnimation()
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    refreshItem.setActionView(R.layout.refresh_action_view)
                    refreshItem.actionView?.startAnimation(animation)
                }
            }
        }

    init {
        lifecycle.addObserver(this)
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

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        isRefreshing = false
        stopAnimation()
    }
}