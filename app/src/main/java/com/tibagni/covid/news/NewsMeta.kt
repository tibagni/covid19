package com.tibagni.covid.news

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class NewsMeta(@PrimaryKey val key: String, val value: String)