package com.tibagni.covid.summary

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tibagni.covid.R
import com.tibagni.covid.repository.LoadingStatus
import com.tibagni.covid.utils.format
import com.tibagni.covid.utils.formatDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.summary_fragment.view.*
import kotlinx.android.synthetic.main.summary_header.view.*
import kotlinx.android.synthetic.main.summary_item.view.*
import kotlinx.android.synthetic.main.summary_item.view.title

@AndroidEntryPoint
class SummaryFragment : Fragment() {

    private val viewModel: SummaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.summary_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val summaryItemsAdapter = SummaryItemsAdapter(
            arrayOf(
                SummaryItemData(
                    R.drawable.ic_coronavirus_black_24dp,
                    getString(R.string.cases_title),
                    0,
                    0
                ),
                SummaryItemData(
                    R.drawable.ic_rip_black_24dp,
                    getString(R.string.deaths_title),
                    0,
                    0
                ),
                SummaryItemData(
                    R.drawable.ic_healing_black_24dp,
                    getString(R.string.recovered_title),
                    0,
                    0
                )
            )
        )

        view.summary_items.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = summaryItemsAdapter
        }

        view.swipe_container.setOnRefreshListener {
            viewModel.refreshSummary()
        }

        viewModel.summary.observe(viewLifecycleOwner) {
            it?.let {
                summaryItemsAdapter.refreshData(
                    it.updatedAt,
                    arrayOf(
                        SummaryItemData(
                            R.drawable.ic_coronavirus_black_24dp,
                            getString(R.string.cases_title),
                            it.newConfirmed,
                            it.totalConfirmed
                        ),
                        SummaryItemData(
                            R.drawable.ic_rip_black_24dp,
                            getString(R.string.deaths_title),
                            it.newDeaths,
                            it.totalDeaths
                        ),
                        SummaryItemData(
                            R.drawable.ic_healing_black_24dp,
                            getString(R.string.recovered_title),
                            it.newRecovered,
                            it.totalRecovered
                        )
                    )
                )
            }
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            view.swipe_container.isRefreshing = (it.status == LoadingStatus.Status.LOADING)
            if (it.status == LoadingStatus.Status.ERROR) {
                Toast.makeText(context, getString(R.string.load_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class SummaryItemsAdapter(private var itemsData: Array<SummaryItemData>) :
        RecyclerView.Adapter<SummaryItemsAdapter.ViewHolder>() {
        private val headerData = SummaryHeaderData(0)

        companion object {
            const val TYPE_HEADER = 0
            const val TYPE_ITEM = 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return if (viewType == TYPE_HEADER) {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.summary_header, parent, false)
                HeaderViewHolder(view)
            } else {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.summary_item, parent, false)
                ItemViewHolder(view)
            }
        }

        override fun getItemCount() = itemsData.size + 1 // Consider header

        override fun getItemViewType(position: Int): Int {
            return if (position == 0) TYPE_HEADER else TYPE_ITEM
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (getItemViewType(position) == TYPE_HEADER) {
                val vh = holder as HeaderViewHolder
                vh.updatedAtTxt.text =
                    vh.context.getString(R.string.updated_at, headerData.updatedAt.formatDate())
            } else {
                val vh = holder as ItemViewHolder
                val itemData = itemsData[position - 1] // header is at 0

                vh.icon.setImageResource(itemData.icon)
                vh.titleTxt.text = itemData.title
                vh.newTxt.text = itemData.new.format(vh.context)
                vh.totalTxt.text = itemData.total.format(vh.context)
            }
        }

        fun refreshData(updatedAt: Long, newItemsData: Array<SummaryItemData>) {
            headerData.updatedAt = updatedAt
            itemsData = newItemsData
            notifyDataSetChanged()
        }

        private abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val context: Context = view.context
        }

        private class ItemViewHolder(view: View) : ViewHolder(view) {
            val icon: ImageView = view.icon
            val titleTxt: TextView = view.title
            val newTxt: TextView = view.new_txt
            val totalTxt: TextView = view.total_txt
        }

        private class HeaderViewHolder(view: View) : ViewHolder(view) {
            val updatedAtTxt: TextView = view.updated_at
        }
    }

    private data class SummaryItemData(
        val icon: Int,
        val title: String,
        val new: Int,
        val total: Int
    )

    private data class SummaryHeaderData(var updatedAt: Long)
}