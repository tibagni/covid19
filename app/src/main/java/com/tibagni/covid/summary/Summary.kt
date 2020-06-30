package com.tibagni.covid.summary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Summary(
    @PrimaryKey val id: Int = 0,
    val newConfirmed: Int,
    val totalConfirmed: Int,
    val newDeaths: Int,
    val totalDeaths: Int,
    val newRecovered: Int,
    val totalRecovered: Int,
    val updatedAt: Long
)