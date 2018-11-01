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
            when (valueOf(day.dayOfTheWeek)) {
                MONDAY -> textViewMonday.text = date
                TUESDAY -> textViewTuesday.text = date
                WEDNESDAY -> textViewWednesday.text = date
                THURSDAY -> textViewThursday.text = date
                FRIDAY -> textViewFriday.text = date
                SATURDAY -> textViewSaturday.text = date
                SUNDAY -> textViewSunday.text = date
            }
        }
    }
}