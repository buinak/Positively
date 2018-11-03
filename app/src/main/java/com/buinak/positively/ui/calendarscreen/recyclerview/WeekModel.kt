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
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.buinak.positively.R
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek

@EpoxyModelClass(layout = R.layout.activity_calendar_recycler_view_row)
abstract class WeekModel : EpoxyModelWithHolder<WeekModel.DateHolder>() {

    @EpoxyAttribute
    lateinit var contents: List<DayEntry>

    override fun bind(holder: DateHolder) {
        for (i in 0 until contents.size) {
            val entry = contents[i]
            val textView = holder.textViews[i]
            textView.text = entry.dayOfTheMonth.toString()
            if (entry.note.isNotEmpty()) {
                holder.textViews[i].setTextColor(
                    when (DayOfTheWeek.valueOf(entry.dayOfTheWeek)) {
                        DayOfTheWeek.MONDAY -> textView.resources.getColor(R.color.mondayColor)
                        DayOfTheWeek.TUESDAY -> textView.resources.getColor(R.color.tuesdayColor)
                        DayOfTheWeek.WEDNESDAY -> textView.resources.getColor(R.color.wednesdayColor)
                        DayOfTheWeek.THURSDAY -> textView.resources.getColor(R.color.thursdayColor)
                        DayOfTheWeek.FRIDAY -> textView.resources.getColor(R.color.fridayColor)
                        DayOfTheWeek.SATURDAY -> textView.resources.getColor(R.color.saturdayColor)
                        DayOfTheWeek.SUNDAY -> textView.resources.getColor(R.color.sundayColor)
                    }
                )
            }
        }
    }

    inner class DateHolder : EpoxyHolder() {
        var textViews: ArrayList<TextView> = ArrayList()

        override fun bindView(itemView: View) {
            textViews.add(itemView.findViewById(R.id.textView_calendar_monday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_tuesday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_wednesday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_thursday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_friday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_saturday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_sunday))
        }

    }
}