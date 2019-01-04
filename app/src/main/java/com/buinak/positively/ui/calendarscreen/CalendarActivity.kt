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
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import com.buinak.positively.entities.Mood
import com.buinak.positively.ui.BaseActivity
import com.buinak.positively.ui.calendarscreen.recyclerview.CalendarController
import com.buinak.positively.utils.Constants
import com.buinak.positively.utils.ViewUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.time.Month

class CalendarActivity : BaseActivity() {
    private val viewModel: CalendarViewModel by lazy {
        ViewModelProviders.of(this).get(CalendarViewModel::class.java)
    }

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView_calendar) }
    private val dateTextView by lazy { findViewById<TextView>(R.id.textView_calendar_date) }
    private val monthTextView by lazy { findViewById<TextView>(R.id.textView_calendar_month) }
    private val noteTextView by lazy { findViewById<TextView>(R.id.textView_note) }
    private val dayOfTheWeekView by lazy { findViewById<TextView>(R.id.textView_calendar_day_of_the_week) }
    private val moodImageView by lazy { findViewById<ImageView>(R.id.imageView_calendar_mood) }
    private val arrowRight by lazy { findViewById<ImageView>(R.id.imageView_arrowRight) }
    private val arrowLeft by lazy { findViewById<ImageView>(R.id.imageView_arrowLeft) }


    private val controller by lazy { CalendarController() }

    private var wasColourChanged: Boolean = false

    //this emits DayEntry whenever a textview inside the calendar is clicked
    private var pressedDateSubject: BehaviorSubject<DayEntry> = BehaviorSubject.create<DayEntry>()
    //emits DayEntry whenever a textview inside the calendar is long-pressed.
    private var longPressedDateSubject: PublishSubject<DayEntry> = PublishSubject.create<DayEntry>()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isFreshlyStarted = savedInstanceState?.getBoolean("isFreshlyStarted") ?: true
        //if this is a fresh start (activity launched from the main activity directly), we need to
        //set the date in the viewmodel. Otherwise, if the activity got restarted (configuration changes)
        //we just subscribe to the data in the viewmodel
        if (isFreshlyStarted) {
            val selectedDay = intent.getIntExtra(Constants.CURRENT_DATE_TAG, -1)
            val selectedMonth = intent.getIntExtra(Constants.CURRENT_MONTH_TAG, -1)
            val selectedYear = intent.getIntExtra(Constants.CURRENT_YEAR_TAG, -1)
            val dayEntry = DayEntry(selectedDay, selectedMonth, selectedYear)
            pressedDateSubject.onNext(dayEntry)
            viewModel.onBind(dayEntry)
        }

        recyclerView.adapter = controller.adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.getDaysLiveData().observe(this, Observer {
            controller.setData(it, pressedDateSubject, longPressedDateSubject)
        })
        viewModel.getCurrentCalendarDateLiveData()
            .observe(this, Observer { dateTextView.text = it })
        viewModel.getCurrentCalendarMonthLiveData()
            .observe(this, Observer { monthTextView.text = it })
        viewModel.getCurrentSelectedDay()
            .observe(this, Observer { dayEntry ->
                changeColours(dayEntry)
                noteTextView.text = dayEntry.note
                var dateString = dayEntry.dayOfTheWeek.toLowerCase().capitalize()
                dateString += ", ${dayEntry.dayOfTheMonth} ${Month.values()[dayEntry.monthOfTheYear].toString().toLowerCase().capitalize()} ${dayEntry.year}"
                dayOfTheWeekView.text = dateString
                moodImageView.visibility = View.VISIBLE
                when (Mood.valueOf(dayEntry.mood)) {
                    Mood.GOOD -> moodImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_good))
                    Mood.NEUTRAL -> moodImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral))
                    Mood.SAD -> moodImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_bad))
                    Mood.UNKNOWN -> moodImageView.visibility = View.GONE
                }
                pressedDateSubject.onNext(dayEntry)
            })
        viewModel.getIdForFinishingLiveData().observe(this, Observer { finishActivity(it) })

        //it is unusable because there is no way for the observable to emit once the activity is gone
        compositeDisposable.add(pressedDateSubject.subscribe { viewModel.onDaySelected(it) })
        compositeDisposable.add(longPressedDateSubject.subscribe {
            viewModel.onDayForModificationSelected(it)
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

        if (wasColourChanged) animateColour(colour) else animateColour(colour, 0)
            .also { wasColourChanged = true }
    }

    private fun animateColour(
        colourTo: Int,
        duration: Int = Constants.ACTIVITY_COLOUR_CHANGES_DELAY
    ) {
        ViewUtils.animateWindowColourChange(window, colourTo, duration)
        ViewUtils.animateTextColourChange(dateTextView, colourTo, duration)
        ViewUtils.animateTextColourChange(monthTextView, colourTo, duration)
        ViewUtils.animateTextColourChange(noteTextView, colourTo, duration)
        ViewUtils.animateTextColourChange(dayOfTheWeekView, colourTo, duration)
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
