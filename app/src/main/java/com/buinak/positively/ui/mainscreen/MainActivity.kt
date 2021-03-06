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
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.buinak.positively.R
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import com.buinak.positively.entities.Mood
import com.buinak.positively.ui.BaseActivity
import com.buinak.positively.ui.calendarscreen.CalendarActivity
import com.buinak.positively.ui.settingsscreen.SettingsActivity
import com.buinak.positively.utils.Constants
import com.buinak.positively.utils.RxUtils
import com.buinak.positively.utils.ViewUtils
import com.devs.vectorchildfinder.VectorChildFinder
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel
    private val allDayTextViewMap: HashMap<TextView, DayOfTheWeek> = HashMap()
    private val moodsImageViewMap: HashMap<Mood, ImageView> = HashMap()

    private val dateTextView by lazy { findViewById<TextView>(R.id.textView_date) }
    private val monthTextView by lazy { findViewById<TextView>(R.id.textView_month) }
    private val howWasYourDayTextView by lazy { findViewById<TextView>(R.id.textView_howWasYourDay) }
    private val noteEditText by lazy { findViewById<EditText>(R.id.editText_note) }

    private val sadMoodImageView by lazy { findViewById<ImageView>(R.id.imageView_sad) }
    private val neutralMoodImageView by lazy { findViewById<ImageView>(R.id.imageView_neutral) }
    private val goodMoodImageView by lazy { findViewById<ImageView>(R.id.imageView_good) }


    private val arrowRightImageView by lazy { findViewById<ImageView>(R.id.imageView_arrowRight) }
    private val arrowLeftImageView by lazy { findViewById<ImageView>(R.id.imageView_arrowLeft) }
    private val settingsImageButton by lazy { findViewById<ImageView>(R.id.imageButton_settings) }

    private val mainLayout by lazy { findViewById<ConstraintLayout>(R.id.main_activity_constraint_layout)}

    private lateinit var currentlySelectedDay: DayEntry

    private var currentColour: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeViewsHideKeyboard(mainLayout)

        fillMapWithTextViews()
        fillMoodMapWithViews()
        initialiseClickListeners()

        viewModel = ViewModelProviders.of(this)
            .get(MainViewModel::class.java)
        viewModel.setNoteTextObservable(RxUtils.observableFromEditText(noteEditText))

        viewModel.getCurrentlySelectedDay().observe(this, Observer { selectedDay ->
            currentlySelectedDay = selectedDay
            onDayOfTheWeekSelected(DayOfTheWeek.valueOf(selectedDay.dayOfTheWeek))
            dateTextView.text = selectedDay.getDateString()
            noteEditText.setText(selectedDay.note)

            setMoodDrawableColor(Mood.valueOf(selectedDay.mood))
        })
        viewModel.getCurrentMonth().observe(this, Observer {
            monthTextView.text = it
        })
    }

    override fun getContentViewId(): Int = R.layout.activity_main

    override fun getNavigationMenuItemId(): Int = R.id.navigation_home

    private fun initialiseClickListeners() {
        //for all day of the week text views
        allDayTextViewMap.keys.forEach { textView ->
            textView.setOnClickListener { clickedView ->
                val clickedTextView = clickedView as TextView
                //can not be null
                val dayOfTheWeek = allDayTextViewMap[clickedTextView] as DayOfTheWeek
                viewModel.onDaySelected(dayOfTheWeek)
            }
        }
        moodsImageViewMap.entries.forEach { entry ->
            entry.value.setOnClickListener {
                setMoodDrawableColor(entry.key)
                viewModel.onMoodSelected(entry.key)
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

    private fun fillMoodMapWithViews() {
        moodsImageViewMap[Mood.GOOD] = findViewById(R.id.imageView_good)
        moodsImageViewMap[Mood.NEUTRAL] = findViewById(R.id.imageView_neutral)
        moodsImageViewMap[Mood.SAD] = findViewById(R.id.imageView_sad)
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
                    startActivityForResult(intent, Constants.ACTIVITY_REQUEST_CODE)
                }
            }
        }, Constants.ANY_ACTIVITY_START_DELAY)
        return true
    }


    private fun resetMoodDrawables() {
        moodsImageViewMap[Mood.GOOD]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_good))
        moodsImageViewMap[Mood.NEUTRAL]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral))
        moodsImageViewMap[Mood.SAD]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_bad))
    }

    private fun setMoodDrawableColor(mood: Mood) {
        resetMoodDrawables()
        when (mood) {
            Mood.GOOD -> {
                val vector =
                    VectorChildFinder(this, R.drawable.ic_good, moodsImageViewMap[Mood.GOOD])
                val path1 = vector.findPathByName("inside_path")
                path1.fillColor = resources.getColor(R.color.goodMoodColor)
            }
            Mood.NEUTRAL -> {
                val vector =
                    VectorChildFinder(this, R.drawable.ic_neutral, moodsImageViewMap[Mood.NEUTRAL])
                val path1 = vector.findPathByName("inside_path")
                path1.fillColor = resources.getColor(R.color.neutralMoodColor)
            }
            Mood.SAD -> {
                val vector = VectorChildFinder(this, R.drawable.ic_bad, moodsImageViewMap[Mood.SAD])
                val path1 = vector.findPathByName("inside_path")
                path1.fillColor = resources.getColor(R.color.sadMoodColor)
            }
            Mood.UNKNOWN -> return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != Constants.ACTIVITY_REQUEST_CODE) return
        if (resultCode != 1) return

        val id = data!!.getStringExtra(Constants.RESULT_ID_TAG)
        viewModel.onDayByIdSelected(id)
    }
}
