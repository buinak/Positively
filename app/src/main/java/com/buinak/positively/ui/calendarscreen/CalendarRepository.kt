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

package com.buinak.positively.ui.calendarscreen

import com.buinak.positively.data.DataSource
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import com.buinak.positively.utils.CalendarUtils
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.*
import kotlin.collections.ArrayList


class CalendarRepository(val dataSource: DataSource) {
    //total amount of days = N * 7, where N = the amount of weeks
    private val TOTAL = 5 * 7

    fun getCurrentFiveWeeks(): Single<List<DayEntry>> {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val acceptableMonths = when (currentMonth) {
            11 -> listOf(10, 11, 0)
            0 -> listOf(11, 0, 1)
            else -> listOf(currentMonth - 1, currentMonth, currentMonth + 1)
        }
        return Single.fromObservable(
            dataSource.getAllDays()
                .map { list -> list.filter { entry -> acceptableMonths.contains(entry.monthOfTheYear) } }
                .zipWith(getListOfEmptyDayEntries().toObservable(),
                    BiFunction<List<DayEntry>, ArrayList<DayEntry>, ArrayList<DayEntry>> { savedEntriesList, emptyEntriesList ->
                        for (savedEntry in savedEntriesList) {
                            val index = emptyEntriesList.indexOfFirst { it.equals(savedEntry) }
                            emptyEntriesList[index] = savedEntry
                        }
                        return@BiFunction emptyEntriesList
                    })
        )
    }

    private fun getListOfEmptyDayEntries(): Single<ArrayList<DayEntry>> {
        val resultList = ArrayList<DayEntry>()
        val currentYear = CalendarUtils.getCurrentYear()
        val currentMonth = CalendarUtils.getCurrentMonth()
        var calendar = Calendar.getInstance()

        //FILL WITH MISSING FROM THE BEGINNING OF THE WEEK
        calendar.set(currentYear, currentMonth, 1)
        while (CalendarUtils.getSpecificDayOfTheWeek(calendar) != DayOfTheWeek.MONDAY) {
            calendar.add(Calendar.DATE, -1)
        }

        for (i in 1..TOTAL) {
            val dayEntry = DayEntry(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
            )
            dayEntry.dayOfTheWeek = DayOfTheWeek.values()[(i - 1) % 7].toString()
            resultList.add(dayEntry)
            calendar.add(Calendar.DATE, 1)
        }

        return Single.just(resultList)
    }
}