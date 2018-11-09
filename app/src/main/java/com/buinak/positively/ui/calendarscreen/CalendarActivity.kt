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

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import com.buinak.positively.ui.BaseActivity
import com.buinak.positively.ui.calendarscreen.recyclerview.CalendarController
import com.buinak.positively.utils.Constants
import com.buinak.positively.utils.ViewUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class CalendarActivity : BaseActivity() {
    private val viewModel: CalendarViewModel by lazy {
        ViewModelProviders.of(this).get(CalendarViewModel::class.java)
    }

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView_calendar) }
    private val dateTextView by lazy { findViewById<TextView>(R.id.textView_calendar_date) }
    private val monthTextView by lazy { findViewById<TextView>(R.id.textView_calendar_month) }
    private val noteTextView by lazy { findViewById<TextView>(R.id.textView_note) }

    private val arrowRight by lazy { findViewById<ImageView>(R.id.imageView_arrowRight) }
    private val arrowLeft by lazy { findViewById<ImageView>(R.id.imageView_arrowLeft) }


    private val controller by lazy { CalendarController() }

    private var currentColour: Int = -1

    private var pressedDateSubject: BehaviorSubject<DayEntry> = BehaviorSubject.create<DayEntry>()
    private var longPressedDateSubject: PublishSubject<DayEntry> = PublishSubject.create<DayEntry>()

    private val compositeDisposable = CompositeDisposable()
    private var hasBeenInitialised = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isFreshlyStarted = savedInstanceState?.getBoolean("isFreshlyStarted") ?: true

        if (isFreshlyStarted) {
            val selectedDay = intent.getIntExtra(Constants.CURRENT_DATE_TAG, -1)
            val selectedMonth = intent.getIntExtra(Constants.CURRENT_MONTH_TAG, -1)
            val selectedYear = intent.getIntExtra(Constants.CURRENT_YEAR_TAG, -1)
            if (selectedDay != -1 && selectedMonth != -1) {
                val dayEntry = DayEntry(selectedDay, selectedMonth, selectedYear)
                pressedDateSubject.onNext(dayEntry)
                viewModel.onDaySelected(dayEntry)
            }

            viewModel.setDate(selectedMonth, selectedYear)
        }

        recyclerView.adapter = controller.adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.getDaysLiveData().observe(this, Observer {
            if (!hasBeenInitialised) {
                controller.setData(it, pressedDateSubject, longPressedDateSubject)
            } else {
                pressedDateSubject = BehaviorSubject.create()
                longPressedDateSubject = PublishSubject.create()
                controller.setData(it, pressedDateSubject, longPressedDateSubject)
            }
        })
        viewModel.getCurrentCalendarDateLiveData()
            .observe(this, Observer { dateTextView.text = it })
        viewModel.getCurrentCalendarMonthLiveData()
            .observe(this, Observer { monthTextView.text = it })

        viewModel.getCurrentSelectedDay()
            .observe(this, Observer { dayEntry ->
                changeColours(dayEntry)
                noteTextView.text = dayEntry.note
                pressedDateSubject.onNext(dayEntry)
            })

        //it is unusable because there is no way for the observable to emit once the activity is gone
        compositeDisposable.add(pressedDateSubject.subscribe { viewModel.onDaySelected(it) })
        compositeDisposable.add(longPressedDateSubject.subscribe { dayEntry ->
            viewModel.getDayEntryIdForModification(dayEntry).observe(this, Observer { id ->
                finishActivity(id)
            })
        })

        arrowLeft.setOnClickListener { viewModel.goOneMonthBehind() }
        arrowRight.setOnClickListener { viewModel.goOneMonthAhead() }
        
        dateTextView.setOnClickListener { viewModel.resetDate() }
        monthTextView.setOnClickListener { viewModel.resetDate() }
    }

    private fun finishActivity(id: String?) {
        val resultIntent = Intent()
        resultIntent.putExtra(Constants.RESULT_ID_TAG, id)
        setResult(Constants.ACTIVITY_REQUEST_CODE, resultIntent)
        finish()
    }

    private fun changeColours(dayEntry: DayEntry) {
        val colour = when (DayOfTheWeek.valueOf(dayEntry.dayOfTheWeek)) {
            DayOfTheWeek.MONDAY -> resources.getColor(R.color.mondayColor)
            DayOfTheWeek.TUESDAY -> resources.getColor(R.color.tuesdayColor)
            DayOfTheWeek.WEDNESDAY -> resources.getColor(R.color.wednesdayColor)
            DayOfTheWeek.THURSDAY -> resources.getColor(R.color.thursdayColor)
            DayOfTheWeek.FRIDAY -> resources.getColor(R.color.fridayColor)
            DayOfTheWeek.SATURDAY -> resources.getColor(R.color.saturdayColor)
            DayOfTheWeek.SUNDAY -> resources.getColor(R.color.sundayColor)
        }
        when (currentColour) {
            -1 -> animateColour(colour, 0)
            else -> animateColour(colour)
        }
        currentColour = colour
    }

    private fun animateColour(
        colourTo: Int,
        duration: Int = Constants.ACTIVITY_COLOUR_CHANGES_DELAY
    ) {
        ViewUtils.animateWindowColourChange(window, colourTo, duration)
        ViewUtils.animateTextColourChange(dateTextView, colourTo, duration)
        ViewUtils.animateTextColourChange(monthTextView, colourTo, duration)
        ViewUtils.animateTextColourChange(noteTextView, colourTo, duration)
    }

    override fun getContentViewId(): Int = R.layout.activity_calendar
    override fun getNavigationMenuItemId(): Int = R.id.navigation_calendar

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("isRestarted", false)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
