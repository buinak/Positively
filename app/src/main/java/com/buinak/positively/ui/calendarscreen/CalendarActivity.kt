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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.ui.BaseActivity
import com.buinak.positively.ui.calendarscreen.recyclerview.CalendarController

class CalendarActivity : BaseActivity() {
    private lateinit var viewModel: CalendarViewModel

    private lateinit var recyclerView: RecyclerView

    val controller = CalendarController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerView = findViewById(R.id.recyclerView_calendar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = controller.adapter

        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        viewModel.getDaysLiveData().observe(this, Observer {
            controller.setData(it)
        })
        findViewById<Button>(R.id.button_oneMonthAhead).setOnClickListener { viewModel.goOneMonthAhead() }
    }

    override fun getContentViewId(): Int = R.layout.activity_calendar

    override fun getNavigationMenuItemId(): Int = R.id.navigation_calendar

}
