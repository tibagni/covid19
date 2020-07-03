package com.tibagni.covid.countries

data class SortingState(val sortBy: SortField, val period: SortPeriod, val asc: Boolean) {

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
        val EMPTY = SortingState(SortField.NAME, PERIOD_DEFAULT, true)
    }

    fun apply(unsorted: List<CountrySummary>): List<CountrySummary> {
        return unsorted.sortedWith(Comparator { a, b ->
            val multiplier = if (asc) 1 else -1

            val total = period == SortPeriod.ALL
            when {
                a.isPinned -> -1
                b.isPinned -> 1
                else -> when (sortBy) {
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
        })
    }
}