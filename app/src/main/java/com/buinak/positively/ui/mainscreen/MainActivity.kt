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

package com.buinak.positively.ui.mainscreen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.buinak.positively.R
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import com.buinak.positively.ui.BaseActivity
import com.buinak.positively.ui.calendarscreen.CalendarActivity
import com.buinak.positively.ui.settingsscreen.SettingsActivity
import com.buinak.positively.utils.Constants
import com.buinak.positively.utils.RxUtils
import com.buinak.positively.utils.ViewUtils
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel
    private val allDayTextViewMap: HashMap<TextView, DayOfTheWeek> = HashMap()

    private lateinit var dateTextView: TextView
    private lateinit var monthTextView: TextView
    private lateinit var howWasYourDayTextView: TextView
    private lateinit var noteEditText: EditText

    private lateinit var arrowRightImageView: ImageView
    private lateinit var arrowLeftImageView: ImageView
    private lateinit var settingsImageButton: ImageButton

    private val mainLayout by lazy { findViewById<ConstraintLayout>(R.id.main_activity_constraint_layout)}

    private lateinit var currentlySelectedDay: DayEntry

    private var currentColour: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeViewsHideKeyboard(mainLayout)
        
        initialiseViewVariables()
        initialiseClickListeners()

        viewModel = ViewModelProviders.of(this)
            .get(MainViewModel::class.java)
        viewModel.setNoteTextObservable(RxUtils.observableFromEditText(noteEditText))

        viewModel.getCurrentlySelectedDay().observe(this, Observer { selectedDay ->
            currentlySelectedDay = selectedDay
            onDayOfTheWeekSelected(DayOfTheWeek.valueOf(selectedDay.dayOfTheWeek))
            dateTextView.text = selectedDay.getDateString()
            noteEditText.setText(selectedDay.note)
        })
        viewModel.getCurrentMonth().observe(this, Observer {
            monthTextView.text = it
        })
    }

    override fun getContentViewId(): Int = R.layout.activity_main

    override fun getNavigationMenuItemId(): Int = R.id.navigation_home

    private fun initialiseViewVariables() {
        dateTextView = findViewById(R.id.textView_date)
        monthTextView = findViewById(R.id.textView_month)
        howWasYourDayTextView = findViewById(R.id.textView_howWasYourDay)

        noteEditText = findViewById(R.id.editText_note)

        arrowRightImageView = findViewById(R.id.imageView_arrowRight)
        arrowLeftImageView = findViewById(R.id.imageView_arrowLeft)

        settingsImageButton = findViewById(R.id.imageButton_settings)
    }

    private fun initialiseClickListeners() {
        fillMapWithTextViews()
        //for all day of the week text views
        allDayTextViewMap.keys.forEach { textView ->
            textView.setOnClickListener { clickedView ->
                val clickedTextView = clickedView as TextView
                val dayOfTheWeek = allDayTextViewMap[clickedTextView]
                //can not be null
                viewModel.onDaySelected(dayOfTheWeek!!)
            }
        }
        //for go-to-the-next-week arrows
        arrowRightImageView.setOnClickListener { viewModel.onGoRightClicked() }
        arrowLeftImageView.setOnClickListener { viewModel.onGoLeftClicked() }
        dateTextView.setOnClickListener { viewModel.onDayResetToToday() }
        monthTextView.setOnClickListener { viewModel.onDayResetToToday() }
        settingsImageButton.setOnClickListener {
            Observable.timer(Constants.ANY_ACTIVITY_START_DELAY, TimeUnit.MILLISECONDS)
                .subscribe { startActivity(Intent(this, SettingsActivity::class.java)) }
        }
    }

    private fun makeViewsHideKeyboard(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                ViewUtils.hideKeyboard(this)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                makeViewsHideKeyboard(innerView)
            }
        }
    }


    private fun onDayOfTheWeekSelected(dayOfTheWeek: DayOfTheWeek) {
        val selectedTextView: TextView = allDayTextViewMap.toList()
            .first { it.second == dayOfTheWeek }.first

        currentColour = selectedTextView.currentTextColor
        animateViews()

        allDayTextViewMap.keys.forEach { it ->
            it.text = it.text.substring(0, 1)
        }
        selectedTextView.text = allDayTextViewMap[selectedTextView].toString()
    }

    private fun animateViews() {
        ViewUtils.animateTextColourChange(
            dateTextView,
            currentColour,
            Constants.ACTIVITY_COLOUR_CHANGES_DELAY
        )
        ViewUtils.animateTextHintColourChange(
            noteEditText,
            currentColour,
            Constants.ACTIVITY_COLOUR_CHANGES_DELAY
        )

        ViewUtils.animateImageViewColourChange(
            settingsImageButton,
            noteEditText.currentTextColor,
            currentColour,
            Constants.ACTIVITY_COLOUR_CHANGES_DELAY
        )
        ViewUtils.animateTextColourChange(
            noteEditText,
            currentColour,
            Constants.ACTIVITY_COLOUR_CHANGES_DELAY
        )
        ViewUtils.animateTextColourChange(
            howWasYourDayTextView,
            currentColour,
            Constants.ACTIVITY_COLOUR_CHANGES_DELAY
        )
        ViewUtils.animateTextColourChange(
            monthTextView,
            currentColour,
            Constants.ACTIVITY_COLOUR_CHANGES_DELAY
        )
        ViewUtils.animateWindowColourChange(
            window,
            currentColour,
            Constants.ACTIVITY_COLOUR_CHANGES_DELAY
        )
    }

    private fun fillMapWithTextViews() {
        allDayTextViewMap[findViewById(R.id.textView_monday)] = DayOfTheWeek.MONDAY
        allDayTextViewMap[findViewById(R.id.textView_tuesday)] = DayOfTheWeek.TUESDAY
        allDayTextViewMap[findViewById(R.id.textView_wednesday)] = DayOfTheWeek.WEDNESDAY
        allDayTextViewMap[findViewById(R.id.textView_thursday)] = DayOfTheWeek.THURSDAY
        allDayTextViewMap[findViewById(R.id.textView_friday)] = DayOfTheWeek.FRIDAY
        allDayTextViewMap[findViewById(R.id.textView_saturday)] = DayOfTheWeek.SATURDAY
        allDayTextViewMap[findViewById(R.id.textView_sunday)] = DayOfTheWeek.SUNDAY
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navigationView.postDelayed({
            val itemId = item.itemId
            when (itemId) {
                R.id.navigation_calendar -> {
                    val intent = Intent(this, CalendarActivity::class.java)
                    intent.putExtra(Constants.CURRENT_DATE_TAG, currentlySelectedDay.dayOfTheMonth)
                    intent.putExtra(
                        Constants.CURRENT_MONTH_TAG,
                        currentlySelectedDay.monthOfTheYear
                    )
                    intent.putExtra(Constants.CURRENT_YEAR_TAG, currentlySelectedDay.year)
                    startActivity(intent)
                }
            }
        }, Constants.ANY_ACTIVITY_START_DELAY)
        return true
    }
}
