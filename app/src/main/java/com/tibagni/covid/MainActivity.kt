package com.tibagni.covid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tibagni.covid.summary.SummaryFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SummaryFragment.newInstance())
                    .commitNow()
        }
    }
}