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
import com.airbnb.epoxy.Typed2EpoxyController
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import io.reactivex.subjects.Subject

class CalendarController : Typed2EpoxyController<List<List<DayEntry>>, Subject<DayEntry>>() {

    @AutoModel
    lateinit var header: CalendarHeaderModel_

    var lastId: Int = 0

    override fun buildModels(data: List<List<DayEntry>>, subject: Subject<DayEntry>) {
        val daysOfTheWeek = ArrayList<String>()
        for (day in DayOfTheWeek.values()) {
            daysOfTheWeek.add(day.toString().substring(0..1).toLowerCase().capitalize())
        }
        header
            .contents(daysOfTheWeek)
            .addTo(this)

        for (list in data) {
            val model = WeekModel_()
                .id(++lastId)
                .primaryMonth(data[3][1].monthOfTheYear)
                .primaryYear(data[3][1].year)
                .contents(list)
                .updateSubject(subject)

            model.secondaryMonth = when (data.indexOf(list)) {
                0 -> if (list[0].monthOfTheYear != model.primaryMonth) list[0].monthOfTheYear else -1
                (data.size - 1) -> if (list[6].monthOfTheYear != model.primaryMonth) list[6].monthOfTheYear else -1
                else -> -1
            }

            model.addTo(this)
        }
    }
}