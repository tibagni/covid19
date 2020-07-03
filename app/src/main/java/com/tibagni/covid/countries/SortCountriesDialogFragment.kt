package com.tibagni.covid.countries

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.chip.ChipGroup
import com.tibagni.covid.R
import com.tibagni.covid.utils.enableChildren
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sort_countries_dialog.view.*

@AndroidEntryPoint
class SortCountriesDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private val resToOrderByFieldMap = mapOf(
            R.id.by_cases to SortingState.SortField.CASES,
            R.id.by_deaths to SortingState.SortField.DEATHS,
            R.id.by_recovered to SortingState.SortField.RECOVERED,
            View.NO_ID to SortingState.SortField.NAME
        )

        private val orderByFieldToResMap = mapOf(
            SortingState.SortField.CASES to R.id.by_cases,
            SortingState.SortField.DEATHS to R.id.by_deaths,
            SortingState.SortField.RECOVERED to R.id.by_recovered,
            SortingState.SortField.NAME to View.NO_ID
        )

        private val resToPeriodMap = mapOf(
            R.id.total to SortingState.SortPeriod.ALL,
            R.id.last_24 to SortingState.SortPeriod.LAST_24_HOURS,
            View.NO_ID to SortingState.PERIOD_DEFAULT
        )

        private val periodToResMap = mapOf(
            SortingState.SortPeriod.ALL to R.id.total,
            SortingState.SortPeriod.LAST_24_HOURS to R.id.last_24
        )
    }

    private val viewModel: CountriesViewModel by viewModels({ requireActivity() })
    private lateinit var sortByGroup: ChipGroup
    private lateinit var totalNewGroup: ChipGroup
    private lateinit var ascDescGroup: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort_countries_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sortByGroup = view.sort_by_group
        totalNewGroup = view.total_new_group
        ascDescGroup = view.asc_desc_group
        initializeState(viewModel.sortingState)

        sortByGroup.setOnCheckedChangeListener { _, checkedId ->
            totalNewGroup.enableChildren(checkedId != View.NO_ID)
        }

        totalNewGroup.enableChildren(viewModel.sortingState.sortBy != SortingState.SortField.NAME)

        view.clear.setOnClickListener { initializeState(SortingState.EMPTY) }
        view.ok.setOnClickListener {
            val field =
                resToOrderByFieldMap[sortByGroup.checkedChipId] ?: SortingState.SortField.NAME
            val period = resToPeriodMap[totalNewGroup.checkedChipId] ?: SortingState.SortPeriod.ALL
            val asc = view.asc.isChecked
            viewModel.sortingState = SortingState(field, period, asc)
            dismiss()
        }
        view.cancel.setOnClickListener { dismiss() }
    }

    private fun initializeState(state: SortingState) {
        val checkedField = orderByFieldToResMap[state.sortBy] ?: View.NO_ID
        val checkedPeriod = periodToResMap[state.period] ?: View.NO_ID
        val checkedAscDesc = if (state.asc) R.id.asc else R.id.desc

        sortByGroup.check(checkedField)
        totalNewGroup.check(checkedPeriod)
        ascDescGroup.check(checkedAscDesc)
    }
}