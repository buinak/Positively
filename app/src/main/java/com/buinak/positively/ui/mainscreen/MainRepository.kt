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
import com.buinak.positively.entities.plain.DayEntry
import com.buinak.positively.entities.plain.DayOfTheWeek
import com.buinak.positively.entities.plain.Mood
import com.buinak.positively.utils.CalendarUtils
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

fun IntRange.random() =
    Random().nextInt((endInclusive + 1) - start) + start

class MainRepository(val dataSource: DataSource) {

    var currentYear = CalendarUtils.getCurrentYear()
    var currentMonth = CalendarUtils.getCurrentMonth()
    var currentDay = CalendarUtils.getCurrentDayOfTheMonth()
    var currentDayOfTheWeek: DayOfTheWeek = CalendarUtils.getCurrentDayOfTheWeek()

    fun getObservableSavedDays(): Observable<List<DayEntry>> = dataSource.getAllDays(2018, true)

    fun getToday(): Single<Pair<DayOfTheWeek, DayEntry>> {
        currentDayOfTheWeek = CalendarUtils.getCurrentDayOfTheWeek()
        return dataSource.getSpecificDay(currentYear, currentMonth, currentDay)
            .map { dayEntry ->
                Pair(currentDayOfTheWeek, dayEntry)
            }
    }

    fun getSpecificDay(dayOfTheWeek: DayOfTheWeek): Single<Pair<DayOfTheWeek, DayEntry>> {
        val calendar = Calendar.getInstance()
        calendar.set(currentYear, currentMonth, currentDay)
        val difference = dayOfTheWeek.ordinal - currentDayOfTheWeek.ordinal
        calendar.add(Calendar.DATE, difference)

        currentYear = calendar.get(Calendar.YEAR)
        currentMonth = calendar.get(Calendar.MONTH)
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        currentDayOfTheWeek = CalendarUtils.getSpecificDayOfTheWeek(calendar)

        return dataSource.getSpecificDay(currentYear, currentMonth, currentDay)
            .map { dayEntry ->
                Pair(currentDayOfTheWeek, dayEntry)
            }
    }

    fun addRandomDay() {
        val year = CalendarUtils.getCurrentYear()
        val month = (1..12).random()
        val day = (1..CalendarUtils.getAmountOfDaysInAMonth(year, month)).random()

        val dayEntry = DayEntry(day, month, year)
        val moodString = Mood.values()
            .asList()
            .shuffled()
            .first()
            .toString()
        dayEntry.mood = moodString

        dataSource.saveDay(dayEntry)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun resetAll() {
        dataSource.removeAllDays()
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}