package com.tibagni.covid.countries

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tibagni.covid.R
import com.tibagni.covid.repository.LoadingStatus
import com.tibagni.covid.utils.format
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.countries_fragment.view.*
import kotlinx.android.synthetic.main.countries_fragment.view.swipe_container
import kotlinx.android.synthetic.main.country_summary_item.view.*
import kotlinx.android.synthetic.main.summary_fragment.view.*

@AndroidEntryPoint
class CountriesFragment : Fragment() {

    companion object {
        fun newInstance() = CountriesFragment()
    }

    private val viewModel: CountriesViewModel by viewModels()

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

        viewModel.countriesSummary.observe(viewLifecycleOwner) {
            countriesAdapter.refreshData(it)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            view.swipe_container.isRefreshing = (it.status == LoadingStatus.Status.LOADING)
            if (it.status == LoadingStatus.Status.ERROR) {
                Toast.makeText(context, getString(R.string.load_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class CountriesAdapter(private val countries: ArrayList<CountrySummary> = arrayListOf()) :
        RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

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
                addAll(newCountries)
            }
            notifyDataSetChanged()
        }

        private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val context: Context = view.context
            val titleTxt: TextView = view.title
            val newTxt: TextView = view.new_txt
            val totalTxt: TextView = view.total_txt
            val newDeathsTxt: TextView = view.new_deaths_txt
            val totalDeathsTxt: TextView = view.total_deaths_txt
            val newRecoveredTxt: TextView = view.new_recovered_txt
            val totalRecoveredTxt: TextView = view.total_recovered_txt

            fun bind(countrySummary: CountrySummary) {
                titleTxt.text = countrySummary.countryName
                newTxt.text = countrySummary.newConfirmed.format(context)
                totalTxt.text = countrySummary.totalConfirmed.format(context)
                newDeathsTxt.text = countrySummary.newDeaths.format(context)
                totalDeathsTxt.text = countrySummary.totalDeaths.format(context)
                newRecoveredTxt.text = countrySummary.newRecovered.format(context)
                totalRecoveredTxt.text = countrySummary.totalRecovered.format(context)
            }
        }
    }

}