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
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.ui.BaseActivity
import com.buinak.positively.ui.calendarscreen.recyclerview.CalendarController
import com.buinak.positively.utils.Constants
import io.reactivex.subjects.PublishSubject

class CalendarActivity : BaseActivity() {
    private lateinit var viewModel: CalendarViewModel

    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView_calendar) }
    private val dateTextView: TextView by lazy { findViewById<TextView>(R.id.textView_calendar_date) }

    private val controller by lazy { CalendarController() }

    private val pressedDateSubject = PublishSubject.create<DayEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val colour = intent.getIntExtra(Constants.COLOUR_TAG, 0)
        window.statusBarColor = colour

        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        viewModel.getDaysLiveData().observe(this, Observer {
            if (recyclerView.adapter == null) {
                recyclerView.adapter = controller.adapter
            }
            controller.setData(it, pressedDateSubject)
        })
        viewModel.getCurrentCalendarDateLiveData()
            .observe(this, Observer { dateTextView.text = it })


    }

    override fun getContentViewId(): Int = R.layout.activity_calendar

    override fun getNavigationMenuItemId(): Int = R.id.navigation_calendar

}
