package com.tibagni.covid.utils

import android.content.Context
import com.tibagni.covid.repository.ResourceData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidResourceData @Inject constructor(@ApplicationContext val context: Context) :
    ResourceData {
    override fun getStringData(resId: Int, def: String): String {
        return try {
            context.getString(resId)
        } catch (e: Exception) {
            def
        }
    }
}