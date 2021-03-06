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

import com.buinak.positively.data.DataSource
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import com.buinak.positively.utils.CalendarUtils
import io.reactivex.Single
import java.util.*

class MainRepository(val dataSource: DataSource) {

    var currentYear = CalendarUtils.getCurrentYear()
    var currentMonth = CalendarUtils.getCurrentMonth()
    var currentDay = CalendarUtils.getCurrentDayOfTheMonth()
    var currentDayOfTheWeek: DayOfTheWeek = CalendarUtils.getCurrentDayOfTheWeek()

    lateinit var lastDayEntry: DayEntry

    fun getToday(): Single<DayEntry> {
        currentYear = CalendarUtils.getCurrentYear()
        currentMonth = CalendarUtils.getCurrentMonth()
        currentDay = CalendarUtils.getCurrentDayOfTheMonth()
        currentDayOfTheWeek = CalendarUtils.getCurrentDayOfTheWeek()
        return dataSource.getSpecificDay(currentYear, currentMonth, currentDay)
            .doOnSuccess { lastDayEntry = it }
    }

    fun getSpecificDay(dayOfTheWeek: DayOfTheWeek): Single<DayEntry> {
        val difference = dayOfTheWeek.ordinal - currentDayOfTheWeek.ordinal
        return getDayWithDifference(difference)
    }

    fun getDayWithDifference(
        difference: Int,
        backwards: Boolean = false
    ): Single<DayEntry> {
        val calendar = Calendar.getInstance()
        calendar.set(currentYear, currentMonth, currentDay)
        if (backwards) {
            calendar.add(Calendar.DATE, -difference)
        } else {
            calendar.add(Calendar.DATE, difference)
        }

        currentYear = calendar.get(Calendar.YEAR)
        currentMonth = calendar.get(Calendar.MONTH)
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        currentDayOfTheWeek = CalendarUtils.getSpecificDayOfTheWeek(calendar)

        return dataSource.getSpecificDay(currentYear, currentMonth, currentDay)
            .doOnSuccess { lastDayEntry = it }
    }

    fun getDayById(id: String): Single<DayEntry> {
        return dataSource.getSpecificDay(id)
    }

    fun isNoteChanged(text: String) = lastDayEntry.note != text

    fun changeNoteAndSaveCurrentEntry(text: String) {
        lastDayEntry.note = text
        dataSource.saveDay(lastDayEntry).subscribe()
    }

    fun changeMoodAndSaveCurrentEntry(mood: String) {
        lastDayEntry.mood = mood
        dataSource.saveDay(lastDayEntry).subscribe()
    }
}