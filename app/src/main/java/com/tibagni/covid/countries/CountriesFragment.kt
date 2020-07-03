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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tibagni.covid.R
import com.tibagni.covid.repository.LoadingStatus
import com.tibagni.covid.utils.Expandable
import com.tibagni.covid.utils.format
import com.tibagni.covid.utils.formatDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.countries_fragment.view.*
import kotlinx.android.synthetic.main.countries_fragment.view.swipe_container
import kotlinx.android.synthetic.main.country_summary_item.view.*

@AndroidEntryPoint
class CountriesFragment : Fragment(), SearchView.OnQueryTextListener {

    private val viewModel: CountriesViewModel by viewModels({requireActivity()})

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

        view.swipe_container.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.sortedCountriesSummary.observe(viewLifecycleOwner) {
            countriesAdapter.refreshData(it)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            view.swipe_container.isRefreshing = (it.status == LoadingStatus.Status.LOADING)
            if (it.status == LoadingStatus.Status.ERROR) {
                Toast.makeText(context, getString(R.string.load_fail), Toast.LENGTH_SHORT).show()
            }
        }
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
            with(this.countries) {
                clear()
                addAll(newCountries.map { Expandable(it) })
            }
            notifyDataSetChanged()
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
                titleTxt.setOnClickListener {
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

                pinIcon.setImageResource(
                    if (countrySummary.content.isPinned) {
                        R.drawable.ic_bookmark_black_24dp
                    } else {
                        R.drawable.ic_bookmark_border_black_24dp
                    }
                )

                val flagUrl =
                    "https://www.countryflags.io/${countrySummary.content.countryCode}/flat/64.png"
                Glide.with(context).load(flagUrl).into(flagImg)

                if (countrySummary.isExpanded) {
                    subtitleTxt.text = context.getString(
                        R.string.updated_at,
                        countrySummary.content.updatedAt.formatDate()
                    )
                    expandableView.visibility = View.VISIBLE
                } else {
                    when (viewModel.sortingState.sortBy) {
                        SortingState.SortField.CASES ->  subtitleTxt.text = context.getString(
                            R.string.subtitle_summary_cases,
                            countrySummary.content.totalConfirmed.format(context),
                            countrySummary.content.newConfirmed.format(context)
                        )
                        SortingState.SortField.DEATHS ->  subtitleTxt.text = context.getString(
                            R.string.subtitle_summary_deaths,
                            countrySummary.content.totalDeaths.format(context),
                            countrySummary.content.newDeaths.format(context)
                        )
                        SortingState.SortField.RECOVERED ->  subtitleTxt.text = context.getString(
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