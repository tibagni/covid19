package com.tibagni.covid.countries

data class CountrySummarySortingState(
    val sortBy: SortField,
    val period: SortPeriod,
    val asc: Boolean
) {

    enum class SortPeriod {
        ALL,
        LAST_24_HOURS
    }

    enum class SortField {
        CASES,
        DEATHS,
        RECOVERED,
        NAME
    }

    companion object {
        val PERIOD_DEFAULT = SortPeriod.ALL
        val EMPTY = CountrySummarySortingState(SortField.NAME, PERIOD_DEFAULT, true)
    }

    /**
     * Apply the sorting state to the given countries summary list keeping the pinned
     * countries first
     */
    fun apply(unsorted: List<CountrySummary>): List<CountrySummary> {
        return unsorted.sortedWith(Comparator { a, b ->
            when {
                a.isPinned && !b.isPinned -> -1
                b.isPinned && !a.isPinned -> 1
                else -> sortByField(a, b)
            }
        })
    }

    /**
     * Apply the sorting state to the given countries summary list but without placing the pinned
     * countries first
     */
    fun applyUnpinned(unsorted: List<CountrySummary>): List<CountrySummary> {
        return unsorted.sortedWith(Comparator { a, b -> sortByField(a, b) })
    }

    private fun sortByField(a: CountrySummary, b: CountrySummary): Int {
        val multiplier = if (asc) 1 else -1
        val total = period == SortPeriod.ALL
        return when (sortBy) {
            SortField.CASES ->
                if (total) a.totalConfirmed.compareTo(b.totalConfirmed) * multiplier
                else a.newConfirmed.compareTo(b.newConfirmed) * multiplier

            SortField.DEATHS ->
                if (total) a.totalDeaths.compareTo(b.totalDeaths) * multiplier
                else a.newDeaths.compareTo(b.newDeaths) * multiplier

            SortField.RECOVERED ->
                if (total) a.totalRecovered.compareTo(b.totalRecovered) * multiplier
                else a.newRecovered.compareTo(b.newRecovered) * multiplier

            SortField.NAME -> a.countryName.compareTo(b.countryName) * multiplier
        }
    }
}