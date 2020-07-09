package com.tibagni.covid.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tibagni.covid.R

/**
 * A RecyclerView that shows an emptyView when it is empty
 */
class EmptyableRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private var emptyView: View? = null
    private val emptyViewId: Int

    init {
        context.theme.obtainStyledAttributes(attrs,
            R.styleable.EmptyableRecyclerView,
            0,
            0).apply {
            try {
                emptyViewId = getResourceId(R.styleable.EmptyableRecyclerView_emptyView, View.NO_ID)
            } finally {
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (emptyViewId != View.NO_ID) {
            emptyView = (parent as View).findViewById(emptyViewId)
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                updateEmptyView(adapter)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                updateEmptyView(adapter)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                updateEmptyView(adapter)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                updateEmptyView(adapter)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                updateEmptyView(adapter)
            }
        })
        super.setAdapter(adapter)
        adapter?.let {
            updateEmptyView(adapter)
        }
    }

    private fun updateEmptyView(adapter: Adapter<*>) {
        val isEmpty = adapter.itemCount == 0
        emptyView?.visibility = if (isEmpty) VISIBLE else GONE
    }

}