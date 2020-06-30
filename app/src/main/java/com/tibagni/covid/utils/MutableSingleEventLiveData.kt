package com.tibagni.covid.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * A version of LiveData that will only notify the update once
 */
class MutableSingleEventLiveData<T>: MutableLiveData<T>() {
    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer<T> { t ->
            if (pending) {
                pending = false
                observer.onChanged(t)
            }
        })
    }

    override fun setValue(t: T?) {
        pending = true
        super.setValue(t)
    }
}