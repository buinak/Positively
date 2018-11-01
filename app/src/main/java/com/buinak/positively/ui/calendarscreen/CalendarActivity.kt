/*
 * Copyright 2018 Konstantin Buinak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.buinak.positively.ui.calendarscreen

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.Month
import com.buinak.positively.ui.BaseActivity
import com.buinak.positively.ui.calendarscreen.recyclerview.CalendarRecyclerViewAdapter

class CalendarActivity : BaseActivity() {
    private lateinit var viewModel: CalendarViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView = findViewById(R.id.recyclerView_calendar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CalendarRecyclerViewAdapter(ArrayList())
        recyclerView.adapter = adapter

        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        viewModel.getDaysLiveData().observe(this, Observer {
            findViewById<TextView>(R.id.textView_calendar_month).text =
                    Month.values()[it[3][4].monthOfTheYear].toString()
            findViewById<TextView>(R.id.textView_calendar_year).text = "${it[3][4].year}"
            updateRecyclerView(it)
        })
        findViewById<Button>(R.id.button_oneMonthAhead).setOnClickListener { viewModel.goOneMonthAhead() }
        findViewById<Button>(R.id.button_calendar_oneYearAhead).setOnClickListener { for (i in 1..12) viewModel.goOneMonthAhead() }
        findViewById<Button>(R.id.button_add10Years).setOnClickListener { for (i in 1..12 * 10) viewModel.goOneMonthAhead() }
        findViewById<Button>(R.id.button_add100Years).setOnClickListener { for (i in 1..12 * 100) viewModel.goOneMonthAhead() }
    }

    private fun updateRecyclerView(data: List<List<DayEntry>>) {
        adapter.monthList = data
        adapter.notifyDataSetChanged()
    }

    override fun getContentViewId(): Int = R.layout.activity_calendar

    override fun getNavigationMenuItemId(): Int = R.id.navigation_calendar

}
