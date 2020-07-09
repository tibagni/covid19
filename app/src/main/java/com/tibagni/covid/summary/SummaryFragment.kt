package com.tibagni.covid.summary

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF
import com.tibagni.covid.R
import com.tibagni.covid.countries.CountrySummarySortingState
import com.tibagni.covid.repository.LoadingStatus
import com.tibagni.covid.utils.*
import com.tibagni.covid.view.onSelectionChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.summary_fragment.view.*
import kotlinx.android.synthetic.main.summary_item.view.*
import kotlinx.android.synthetic.main.top_countries.view.*

@AndroidEntryPoint
class SummaryFragment : Fragment() {

    private val viewModel: SummaryViewModel by viewModels({ requireActivity() })
    private var refreshAnimationHandler: RefreshAnimationHandler? = null

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
            refreshAnimationHandler?.isRefreshing = it.status == LoadingStatus.Status.LOADING
            view.loading_view.visibility =
                if (it.status == LoadingStatus.Status.LOADING) View.VISIBLE else View.GONE

            if (it.status == LoadingStatus.Status.ERROR) {
                Toast.makeText(context, getString(R.string.load_fail), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.topCountries.observe(viewLifecycleOwner) {
            it?.let {
                bindChartData(it, view.top5_chart)
                loadFlags(it) {
                    if (it.none { e -> (e.flag == null && e.countryCode.isNotEmpty()) }) {
                        bindChartData(it, view.top5_chart) // Bind again with the flags
                    }
                }
            }
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.top_5_sort_by,
            android.R.layout.simple_spinner_dropdown_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            view.top5_sort_by.adapter = it
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.top_5_period,
            android.R.layout.simple_spinner_dropdown_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            view.top5_period.adapter = it
        }

        val top5FilterChanged = {
            viewModel.updateTopCountriesSortingState(
                CountrySummarySortingState.SortField.values()[view.top5_sort_by.selectedItemId.toInt()],
                CountrySummarySortingState.SortPeriod.values()[view.top5_period.selectedItemId.toInt()]
            )
        }
        view.top5_sort_by.onSelectionChanged { top5FilterChanged() }
        view.top5_period.onSelectionChanged { top5FilterChanged() }

        view.top5_sort_by.setSelection(viewModel.sortingState?.sortBy?.ordinal ?: 0)
        view.top5_period.setSelection(viewModel.sortingState?.period?.ordinal ?: 0)

        initChart(view.top5_chart)
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

    private fun initChart(chart: PieChart) {
        chart.setDrawEntryLabels(false)
        chart.description = null
        chart.rotationAngle = 0f;
        chart.isRotationEnabled = false;
        val l = chart.legend
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.textSize = 12f
        l.isWordWrapEnabled = true
        l.setDrawInside(false)
    }

    private fun bindChartData(top5Data: List<Top5CountrySummary>, chart: PieChart) {
        val entries = top5Data.map { PieEntry(it.stat.toFloat(), it.countryName, it.flag) }
        val chartDataSet = PieDataSet(entries, "")
        val chartData = PieData(chartDataSet)

        chartDataSet.colors = resources.getStringArray(R.array.top5_chart_colors)
            .map { c -> Color.parseColor(c) }

        chartData.setDrawValues(true)
        chartData.setValueTextSize(14f)
        chartDataSet.iconsOffset = MPPointF(25f, 0f)

        chart.data = chartData
        chart.invalidate()
    }

    private fun loadFlags(top5Data: List<Top5CountrySummary>, callback: () -> Unit) {
        top5Data.forEach {
            Glide
                .with(requireContext())
                .load("https://www.countryflags.io/${it.countryCode}/flat/64.png")
                .into(object : CustomTarget<Drawable>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(d: Drawable, t: Transition<in Drawable>?) {
                        it.flag = d
                        callback()
                    }
                })
        }
    }
}