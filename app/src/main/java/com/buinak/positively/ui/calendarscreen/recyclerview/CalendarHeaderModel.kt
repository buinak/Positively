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
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.buinak.positively.R

@EpoxyModelClass(layout = R.layout.activity_calendar_recycler_view_row)
abstract class CalendarHeaderModel : EpoxyModelWithHolder<CalendarHeaderModel.DateHolder>() {

    @EpoxyAttribute
    lateinit var contents: List<String>

    override fun bind(holder: DateHolder) {
        for (i in 0 until contents.size) {
            holder.textViews[i].text = contents[i]
            holder.textViews[i].setTypeface(null, Typeface.BOLD)
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