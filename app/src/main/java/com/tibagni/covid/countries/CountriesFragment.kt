package com.tibagni.covid.countries

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tibagni.covid.R
import com.tibagni.covid.repository.LoadingStatus
import com.tibagni.covid.utils.ResourceHelper
import com.tibagni.covid.view.Expandable
import com.tibagni.covid.utils.format
import com.tibagni.covid.utils.formatDate
import com.tibagni.covid.view.EmptyView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.countries_fragment.view.*
import kotlinx.android.synthetic.main.country_summary_item.view.*

@AndroidEntryPoint
class CountriesFragment : Fragment(), SearchView.OnQueryTextListener {

    private val viewModel: CountriesViewModel by viewModels({ requireActivity() })

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.country_summary_menu, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sort) {
            SortCountriesDialogFragment().show(parentFragmentManager, "sort_dialog")
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.countries_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val countriesAdapter = CountriesAdapter()

        view.country_summary_items.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = countriesAdapter
        }

        viewModel.sortedCountriesSummary.observe(viewLifecycleOwner) {
            countriesAdapter.refreshData(it)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            view.emptyView.emptyState = when (it.status) {
                LoadingStatus.Status.LOADING -> EmptyView.STATE_LOADING
                LoadingStatus.Status.ERROR -> EmptyView.STATE_ERROR
                LoadingStatus.Status.SUCCESS -> EmptyView.STATE_NO_DATA
            }

            if (it.status == LoadingStatus.Status.ERROR) {
                Toast.makeText(context, getString(R.string.load_fail), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.refresh(false)
    }

    private inner class CountriesAdapter(items: List<CountrySummary> = arrayListOf()) :
        RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {
        val countries = items.map { Expandable(it) }.toMutableList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.country_summary_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = countries.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(countries[position])
        }

        fun refreshData(newCountries: List<CountrySummary>) {
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val old = countries[oldItemPosition].content
                    val new = newCountries[newItemPosition]
                    return old.countryCode == new.countryCode
                }

                override fun getOldListSize() = countries.size
                override fun getNewListSize() = newCountries.size

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    countries[oldItemPosition].content == newCountries[newItemPosition]
            })
            with(this.countries) {
                clear()
                addAll(newCountries.map { Expandable(it) })
            }
            diffResult.dispatchUpdatesTo(this)
        }

        private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val context: Context = view.context
            val titleTxt: TextView = view.title
            val subtitleTxt: TextView = view.subtitle
            val pinIcon: ImageView = view.pin_icon
            val flagImg: ImageView = view.flag
            val newTxt: TextView = view.new_txt
            val totalTxt: TextView = view.total_txt
            val newDeathsTxt: TextView = view.new_deaths_txt
            val totalDeathsTxt: TextView = view.total_deaths_txt
            val newRecoveredTxt: TextView = view.new_recovered_txt
            val totalRecoveredTxt: TextView = view.total_recovered_txt
            val expandableView: View = view.expandable

            init {
                view.title_area.setOnClickListener {
                    val expandable = countries[adapterPosition]
                    expandable.toggle()
                    notifyItemChanged(adapterPosition)
                }

                pinIcon.setOnClickListener {
                    val countrySummary = countries[adapterPosition].content
                    countrySummary.isPinned = !countrySummary.isPinned
                    viewModel.updateCountrySummary(countrySummary)
                    notifyItemChanged(adapterPosition)
                }
            }

            fun bind(countrySummary: Expandable<CountrySummary>) {
                titleTxt.text = countrySummary.content.countryName
                newTxt.text = countrySummary.content.newConfirmed.format(context)
                totalTxt.text = countrySummary.content.totalConfirmed.format(context)
                newDeathsTxt.text = countrySummary.content.newDeaths.format(context)
                totalDeathsTxt.text = countrySummary.content.totalDeaths.format(context)
                newRecoveredTxt.text = countrySummary.content.newRecovered.format(context)
                totalRecoveredTxt.text = countrySummary.content.totalRecovered.format(context)
                flagImg.setImageResource(
                    ResourceHelper.getCountryFlag(
                        countrySummary.content.countryCode
                    )
                )

                pinIcon.setImageResource(
                    if (countrySummary.content.isPinned) {
                        R.drawable.ic_bookmark_black_24dp
                    } else {
                        R.drawable.ic_bookmark_border_black_24dp
                    }
                )

                if (countrySummary.isExpanded) {
                    subtitleTxt.text = context.getString(
                        R.string.updated_at,
                        countrySummary.content.updatedAt.formatDate()
                    )
                    expandableView.visibility = View.VISIBLE
                } else {
                    when (viewModel.sortingState.sortBy) {
                        CountrySummarySortingState.SortField.CASES -> subtitleTxt.text =
                            context.getString(
                                R.string.subtitle_summary_cases,
                                countrySummary.content.totalConfirmed.format(context),
                                countrySummary.content.newConfirmed.format(context)
                            )
                        CountrySummarySortingState.SortField.DEATHS -> subtitleTxt.text =
                            context.getString(
                                R.string.subtitle_summary_deaths,
                                countrySummary.content.totalDeaths.format(context),
                                countrySummary.content.newDeaths.format(context)
                            )
                        CountrySummarySortingState.SortField.RECOVERED -> subtitleTxt.text =
                            context.getString(
                                R.string.subtitle_summary_recovered,
                                countrySummary.content.totalRecovered.format(context),
                                countrySummary.content.newRecovered.format(context)
                            )
                        else -> subtitleTxt.text = context.getString(
                            R.string.subtitle_summary,
                            countrySummary.content.newConfirmed.format(context),
                            countrySummary.content.newDeaths.format(context)
                        )
                    }

                    expandableView.visibility = View.GONE
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filter(newText ?: "")
        return true
    }

}