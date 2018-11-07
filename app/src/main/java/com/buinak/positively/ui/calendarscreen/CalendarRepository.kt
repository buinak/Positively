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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.collections.ArrayList


class CalendarRepository(val dataSource: DataSource) {
    //total amount of days = N * 7, where N = the amount of weeks
    private val TOTAL = 6 * 7
    private var calendar = Calendar.getInstance()

    private var dayOfTheWeekCalendar = Calendar.getInstance()
    private var disposable: Disposable? = null

    private var lastData: List<DayEntry>? = null


    //TODO: make it 7 weeks
    fun getCurrentFiveWeeks(): Observable<List<DayEntry>> {
        val subject: PublishSubject<List<DayEntry>> = PublishSubject.create()

        val currentMonth = calendar.get(Calendar.MONTH)
        val acceptableMonths = when (currentMonth) {
            11 -> listOf(10, 11, 0)
            0 -> listOf(11, 0, 1)
            else -> listOf(currentMonth - 1, currentMonth, currentMonth + 1)
        }
        disposable?.dispose()
        disposable = dataSource.getAllDays()
            .take(1)
            .map { list -> list.filter { entry -> acceptableMonths.contains(entry.monthOfTheYear) } }
            .subscribe { savedEntriesList ->
                getListOfEmptyDayEntries().subscribe { emptyEntriesList ->
                    val data = filterLists(savedEntriesList, emptyEntriesList)
                    lastData = data

                    subject.onNext(lastData!!)
                }
            }


        return subject
    }

    fun resetToCurrent() {
        calendar = Calendar.getInstance()
    }

    private fun filterLists(
        savedEntriesList: List<DayEntry>,
        emptyEntriesList: ArrayList<DayEntry>
    ): ArrayList<DayEntry> {
        for (savedEntry in savedEntriesList) {
            val index = emptyEntriesList.indexOfFirst { it == savedEntry }
            if (index != -1) {
                emptyEntriesList[index] = savedEntry
            }
        }
        return emptyEntriesList
    }

    private fun getListOfEmptyDayEntries(): Single<ArrayList<DayEntry>> {
        val resultList = ArrayList<DayEntry>()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        var newCalendar = Calendar.getInstance()

        //FILL WITH MISSING FROM THE BEGINNING OF THE WEEK
        newCalendar.set(currentYear, currentMonth, 1)
        while (CalendarUtils.getSpecificDayOfTheWeek(newCalendar) != DayOfTheWeek.MONDAY) {
            newCalendar.add(Calendar.DATE, -1)
        }

        for (i in 1..TOTAL) {
            val dayEntry = DayEntry(
                newCalendar.get(Calendar.DAY_OF_MONTH),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.YEAR)
            )
            dayEntry.dayOfTheWeek = DayOfTheWeek.values()[(i - 1) % 7].toString()
            resultList.add(dayEntry)
            newCalendar.add(Calendar.DATE, 1)
        }

        return Single.just(resultList)
    }

    fun goOneMonthAhead() = calendar.add(Calendar.MONTH, 1)
    fun goOneMonthBehind() = calendar.add(Calendar.MONTH, -1)
    fun getCurrentMonth() = calendar.get(Calendar.MONTH)
    fun getCurrentYear() = calendar.get(Calendar.YEAR)

    fun getDayEntry(dayEntry: DayEntry): Single<DayEntry> {
        if (lastData != null) {
            val entryFromLastData = lastData!!.firstOrNull { it == dayEntry }
            if (entryFromLastData != null) {
                return Single.just(entryFromLastData)
            }
        }

        return dataSource.getSpecificDay(
            dayEntry.year,
            dayEntry.monthOfTheYear,
            dayEntry.dayOfTheMonth
        )
    }

    fun setDate(month: Int, year: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
    }
}