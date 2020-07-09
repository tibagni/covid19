package com.tibagni.covid.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.tibagni.covid.R
import kotlinx.android.synthetic.main.empty_view.view.*

class EmptyView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs)  {

    companion object {
        const val STATE_NO_DATA = 0
        const val STATE_LOADING = 1
        const val STATE_ERROR = 2
    }

    var emptyState: Int = STATE_NO_DATA
    set(value) {
        field = value
        applyState(value)
        invalidate()
    }

    init {
        inflate(context, R.layout.empty_view, this)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.EmptyView)
        emptyState = attributes.getInt(R.styleable.EmptyView_empty_state, STATE_NO_DATA)
        applyState(emptyState)
        attributes.recycle()
    }

    private fun applyState(state: Int) {
        textView.text = when (state) {
            STATE_NO_DATA -> context.getString(R.string.no_data_state)
            STATE_LOADING -> context.getString(R.string.loading_state)
            STATE_ERROR -> context.getString(R.string.error_state)
            else -> context.getString(R.string.no_data_state)
        }

        if (state == STATE_LOADING) {
            imageView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE

            imageView.setImageResource(when (state) {
                STATE_ERROR -> R.drawable.ic_error_outline_black_24dp
                STATE_NO_DATA -> R.drawable.ic_sentiment_dissatisfied_black_24dp
                else -> View.NO_ID
            })
        }

    }
}