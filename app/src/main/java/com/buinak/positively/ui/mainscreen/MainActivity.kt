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

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.buinak.positively.R
import com.buinak.positively.entities.plain.DayOfTheWeek
import com.buinak.positively.utils.Constants
import com.buinak.positively.utils.ViewUtils

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val allDayTextViewMap: HashMap<TextView, DayOfTheWeek> = HashMap()

    private lateinit var dateTextView: TextView
    private lateinit var monthTextView: TextView
    private lateinit var idTextView: TextView
    private lateinit var howWasYourDayTextView: TextView
    private lateinit var noteEditText: EditText

    private lateinit var arrowRightImageView: ImageView
    private lateinit var arrowLeftImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialiseViewVariables()
        initialiseClickListeners()

        val watcher = object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.onNoteTextChanged(p0.toString())
            }

        }
        noteEditText.addTextChangedListener(watcher)

        viewModel = ViewModelProviders.of(this)
            .get(MainViewModel::class.java)

        viewModel.getCurrentlySelectedDay().observe(this, Observer { dayOfTheWeek ->
            onDayOfTheWeekSelected(dayOfTheWeek.first)
            val dayEntry = dayOfTheWeek.second
            val dateString =
                "${dayEntry.dayOfTheMonth}.${dayEntry.monthOfTheYear + 1}.${dayEntry.year}"
            dateTextView.text = dateString
            noteEditText.setText(dayEntry.note)
            idTextView.text = "ID = ${dayEntry.id.substring(0..5)}"
        })

        viewModel.getCurrentMonth().observe(this, Observer {
            monthTextView.text = it
        })
    }

    private fun initialiseViewVariables() {
        dateTextView = findViewById(R.id.textView_date)
        monthTextView = findViewById(R.id.textView_month)
        idTextView = findViewById(R.id.textView_id)
        howWasYourDayTextView = findViewById(R.id.textView_howWasYourDay)

        noteEditText = findViewById(R.id.editText_note)

        arrowRightImageView = findViewById(R.id.imageView_arrowRight)
        arrowLeftImageView = findViewById(R.id.imageView_arrowLeft)
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
    }


    private fun onDayOfTheWeekSelected(dayOfTheWeek: DayOfTheWeek) {
        val selectedTextView: TextView = allDayTextViewMap.toList()
            .first { it.second == dayOfTheWeek }.first

        ViewUtils.animateTextColourChange(
            dateTextView,
            selectedTextView.currentTextColor,
            Constants.ANIMATION_DURATION_COLOR_CHANGES
        )
        ViewUtils.animateTextColourChange(
            howWasYourDayTextView,
            selectedTextView.currentTextColor,
            Constants.ANIMATION_DURATION_COLOR_CHANGES
        )
        ViewUtils.animateTextColourChange(
            monthTextView,
            selectedTextView.currentTextColor,
            Constants.ANIMATION_DURATION_COLOR_CHANGES
        )
        ViewUtils.animateWindowColourChange(
            window,
            selectedTextView.currentTextColor,
            Constants.ANIMATION_DURATION_COLOR_CHANGES
        )

        allDayTextViewMap.keys.forEach { it.text = it.text.substring(0, 1) }
        selectedTextView.text = allDayTextViewMap[selectedTextView].toString()
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
}
