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

import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.Typed3EpoxyController
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import io.reactivex.subjects.Subject

class CalendarController :
    Typed3EpoxyController<List<List<DayEntry>>, Subject<DayEntry>, Subject<DayEntry>>() {

    @AutoModel
    lateinit var header: CalendarHeaderModel_

    var lastId: Int = 0

    override fun buildModels(
        data: List<List<DayEntry>>,
        onClickSubject: Subject<DayEntry>,
        onLongClickSubject: Subject<DayEntry>
    ) {
        val daysOfTheWeek = ArrayList<String>()
        for (day in DayOfTheWeek.values()) {
            daysOfTheWeek.add(day.toString()[0].toUpperCase().toString())
        }
        header
            .contents(daysOfTheWeek)
            .addTo(this)

        val primaryMonth = data[3][1].monthOfTheYear
        val primaryYear = data[3][1].year
        for (list in data) {
            //if the entire last week consists of days belonging to the wrong month, skip it
            if (list.count { it.monthOfTheYear == primaryMonth } == 0) continue

            val model = WeekModel_()
                .id(++lastId)
                .primaryMonth(primaryMonth)
                .primaryYear(primaryYear)
                .contents(list)
                .updateSubject(onClickSubject)
                .longClickSubject(onLongClickSubject)

            if (data.indexOf(list) == (data.size - 1)) {
                println()
            }
            model.secondaryMonth = when (data.indexOf(list)) {
                0 -> if (list[0].monthOfTheYear != model.primaryMonth) list[0].monthOfTheYear else -1
                else -> if (list[6].monthOfTheYear != model.primaryMonth) list[6].monthOfTheYear else -1
            }

            model.addTo(this)
        }
    }
}