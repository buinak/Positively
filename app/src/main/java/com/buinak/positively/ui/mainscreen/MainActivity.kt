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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.buinak.positively.R
import com.buinak.positively.entities.plain.DayOfTheWeek

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val allDayTextViewMap: HashMap<DayOfTheWeek, TextView> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this)
            .get(MainViewModel::class.java)

        fillMapWithTextViews()
        initialiseTextViewClickListeners()

        viewModel.getCurrentlySelectedDay().observe(this, Observer { dayOfTheWeek ->
            onTextViewSelected(dayOfTheWeek.first)
            val dayEntry = dayOfTheWeek.second
            val dateString = "${dayEntry.dayOfTheMonth}.${dayEntry.monthOfTheYear}.${dayEntry.year}"
            findViewById<TextView>(R.id.textView_date).text = dateString
            findViewById<TextView>(R.id.textView_id).text = "ID = ${dayEntry.id.substring(0..5)}"
        })
    }

    private fun initialiseTextViewClickListeners() {
        allDayTextViewMap.values.forEach { textView ->
            textView.setOnClickListener { clickedView ->
                val clickedTextView = clickedView as TextView
                val dayOfTheWeek = allDayTextViewMap.toList()
                    .first { it.second == clickedTextView }
                    .first

                viewModel.onDaySelected(dayOfTheWeek)
            }
        }
    }

    private fun onTextViewSelected(dayOfTheWeek: DayOfTheWeek) {
        val selectedTextView: TextView = allDayTextViewMap.toList()
            .first { it.first == dayOfTheWeek }
            .second

        window.statusBarColor = selectedTextView.currentTextColor
        allDayTextViewMap.values.forEach { it.text = it.text.substring(0, 1) }
        selectedTextView.text = allDayTextViewMap.toList()
            .first { it.second == selectedTextView }
            .first
            .toString()
    }

    private fun fillMapWithTextViews() {
        allDayTextViewMap[DayOfTheWeek.MONDAY] = findViewById(R.id.textView_monday)
        allDayTextViewMap[DayOfTheWeek.TUESDAY] = findViewById(R.id.textView_tuesday)
        allDayTextViewMap[DayOfTheWeek.WEDNESDAY] = findViewById(R.id.textView_wednesday)
        allDayTextViewMap[DayOfTheWeek.THURSDAY] = findViewById(R.id.textView_thursday)
        allDayTextViewMap[DayOfTheWeek.FRIDAY] = findViewById(R.id.textView_friday)
        allDayTextViewMap[DayOfTheWeek.SATURDAY] = findViewById(R.id.textView_saturday)
        allDayTextViewMap[DayOfTheWeek.SUNDAY] = findViewById(R.id.textView_sunday)
    }
}
