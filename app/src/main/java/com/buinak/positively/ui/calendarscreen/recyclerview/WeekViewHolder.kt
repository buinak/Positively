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

package com.buinak.positively.ui.calendarscreen.recyclerview

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek.*

class WeekViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val textViewMonday = view.findViewById<TextView>(R.id.textView_calendar_monday)
    val textViewTuesday = view.findViewById<TextView>(R.id.textView_calendar_tuesday)
    val textViewWednesday = view.findViewById<TextView>(R.id.textView_calendar_wednesday)
    val textViewThursday = view.findViewById<TextView>(R.id.textView_calendar_thursday)
    val textViewFriday = view.findViewById<TextView>(R.id.textView_calendar_friday)
    val textViewSaturday = view.findViewById<TextView>(R.id.textView_calendar_saturday)
    val textViewSunday = view.findViewById<TextView>(R.id.textView_calendar_sunday)

    fun bindDay(weekList: List<DayEntry>) {
        for (day in weekList) {
            val date = when (day.dayOfTheMonth.toString().length) {
                1 -> "0${day.dayOfTheMonth}"
                else -> "${day.dayOfTheMonth}"
            }
            val textView: TextView
            val colour: Int
            when (valueOf(day.dayOfTheWeek)) {
                MONDAY -> {
                    textView = textViewMonday
                    colour = textView.resources.getColor(R.color.mondayColor)
                }
                TUESDAY -> {
                    textView = textViewTuesday
                    colour = textView.resources.getColor(R.color.tuesdayColor)
                }
                WEDNESDAY -> {
                    textView = textViewWednesday
                    colour = textView.resources.getColor(R.color.wednesdayColor)
                }
                THURSDAY -> {
                    textView = textViewThursday
                    colour = textView.resources.getColor(R.color.thursdayColor)
                }
                FRIDAY -> {
                    textView = textViewFriday
                    colour = textView.resources.getColor(R.color.fridayColor)
                }
                SATURDAY -> {
                    textView = textViewSaturday
                    colour = textView.resources.getColor(R.color.saturdayColor)
                }
                SUNDAY -> {
                    textView = textViewSunday
                    colour = textView.resources.getColor(R.color.sundayColor)
                }
            }
            if (day.note.isNotEmpty()) {
                textView.setTextColor(colour)
                textView.setTypeface(null, Typeface.BOLD)
            }
            textView.text = date
        }
    }
}