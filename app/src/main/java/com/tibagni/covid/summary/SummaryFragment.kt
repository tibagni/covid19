package com.tibagni.covid.summary

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.tibagni.covid.R
import com.tibagni.covid.repository.LoadingStatus
import com.tibagni.covid.utils.RefreshAnimationHandler
import com.tibagni.covid.utils.format
import com.tibagni.covid.utils.formatDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.summary_fragment.view.*
import kotlinx.android.synthetic.main.summary_item.view.*

@AndroidEntryPoint
class SummaryFragment : Fragment() {

    private val viewModel: SummaryViewModel by viewModels()
    private lateinit var refreshAnimationHandler: RefreshAnimationHandler

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.summary_menu, menu)
        val refreshMenuItem = menu.findItem(R.id.refresh)
        refreshAnimationHandler = RefreshAnimationHandler(requireContext(), refreshMenuItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh) {
            viewModel.refreshSummary()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.summary_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.updated_at.text = getString(R.string.loading)
        bindCasesSummary(view, 0, 0)
        bindDeathsSummary(view, 0, 0)
        bindRecoveredSummary(view, 0, 0)

        viewModel.summary.observe(viewLifecycleOwner) {
            it?.let {
                view.updated_at.text = getString(R.string.updated_at, it.updatedAt.formatDate())
                bindCasesSummary(view, it.newConfirmed, it.totalConfirmed)
                bindDeathsSummary(view, it.newDeaths, it.totalDeaths)
                bindRecoveredSummary(view, it.newRecovered, it.totalRecovered)
            }
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            refreshAnimationHandler.isRefreshing = it.status == LoadingStatus.Status.LOADING
            view.loading_view.visibility =
                if (it.status == LoadingStatus.Status.LOADING) View.VISIBLE else View.GONE

            if (it.status == LoadingStatus.Status.ERROR) {
                Toast.makeText(context, getString(R.string.load_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindCasesSummary(view: View, new: Int, total: Int) {
        bindSummaryView(
            view.item_cases,
            R.drawable.ic_coronavirus_black_24dp,
            getString(R.string.cases_title),
            new,
            total
        )
    }

    private fun bindDeathsSummary(view: View, new: Int, total: Int) {
        bindSummaryView(
            view.item_deaths,
            R.drawable.ic_rip_black_24dp,
            getString(R.string.deaths_title),
            new,
            total
        )
    }

    private fun bindRecoveredSummary(view: View, new: Int, total: Int) {
        bindSummaryView(
            view.item_recovered,
            R.drawable.ic_healing_black_24dp,
            getString(R.string.recovered_title),
            new,
            total
        )
    }

    private fun bindSummaryView(itemView: View, icon: Int, title: String, new: Int, total: Int) {
        itemView.icon.setImageResource(icon)
        itemView.title.text = title
        itemView.new_txt.text = new.format(context)
        itemView.total_txt.text = total.format(context)
    }
}