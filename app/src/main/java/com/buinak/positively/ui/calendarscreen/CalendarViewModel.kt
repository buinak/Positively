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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buinak.positively.application.PositivelyApplication
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.Month
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CalendarViewModel : ViewModel() {

    @Inject
    lateinit var repository: CalendarRepository

    private var monthDisposable: Disposable? = null
    private var dayDisposable: Disposable? = null

    private val weeksForTheSelectedMonth = MutableLiveData<List<List<DayEntry>>>()

    private val currentCalendarMonth = MutableLiveData<String>()
    private val currentCalendarDate = MutableLiveData<String>()

    private val currentSelectedDay = MutableLiveData<DayEntry>()
    private var lastSelected: DayEntry? = null


    init {
        PositivelyApplication.inject(this)
    }

    private fun getCurrentFiveWeeks() {
        monthDisposable?.dispose()
        monthDisposable = getCurrentFiveWeeksWithAction()
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun getCurrentFiveWeeksWithAction(): Observable<ArrayList<List<DayEntry>>> {
        return repository.getCurrentFiveWeeks()
            .map { list -> splitTheListIntoWeeks(list) }
            .doOnNext { it ->
                val existing = weeksForTheSelectedMonth.value
                val sameList = existing?.get(3)?.get(3) == it[3][3]

                if (!sameList) weeksForTheSelectedMonth.postValue(it)

                val currentYear = repository.getCurrentYear()
                val currentMonth = (repository.getCurrentMonth() + 1)
                val currentDay = repository.getCurrentDayOfTheMonth()
                val currentDayString = when (currentDay < 10) {
                    true -> "0$currentDay"
                    false -> "$currentDay"
                }
                val currentMonthString = when (currentMonth < 10) {
                    true -> "0$currentMonth"
                    false -> "$currentMonth"
                }

                val dateString = "$currentDayString.$currentMonthString.$currentYear"
                currentCalendarDate.postValue(dateString)

                val monthString = "${Month.values()[repository.getCurrentMonth()]}"
                currentCalendarMonth.postValue(monthString)
            }
    }

    private fun splitTheListIntoWeeks(list: List<DayEntry>): ArrayList<List<DayEntry>> {
        val resultList = ArrayList<List<DayEntry>>()
        var currentList = ArrayList<DayEntry>()
        for (i in 0 until list.size) {
            currentList.add(list[i])
            if (i != 0) {
                if ((i + 1) % 7 == 0 || i == list.size - 1) {
                    resultList.add(currentList)
                    currentList = ArrayList()
                }
            }
        }
        return resultList
    }

    fun getDaysLiveData(): LiveData<List<List<DayEntry>>> = weeksForTheSelectedMonth

    fun getCurrentCalendarMonthLiveData(): LiveData<String> = currentCalendarMonth
    fun getCurrentCalendarDateLiveData(): LiveData<String> = currentCalendarDate

    fun getCurrentSelectedDay(): LiveData<DayEntry> = currentSelectedDay

    fun goOneMonthAhead() {
        repository.goOneMonthAhead()
        getCurrentFiveWeeks()
    }

    fun goOneMonthBehind() {
        repository.goOneMonthBehind()
        getCurrentFiveWeeks()
    }

    fun setDate(month: Int, year: Int) {
        repository.setDate(month, year)
        getCurrentFiveWeeks()
    }

    fun resetDate() {
        repository.resetToCurrent()

        monthDisposable?.dispose()
        monthDisposable = getCurrentFiveWeeksWithAction()
            .subscribeOn(Schedulers.io())
            .map { list ->
                val currentDayEntry = DayEntry(
                    repository.getCurrentDayOfTheMonth(),
                    repository.getCurrentMonth(),
                    repository.getCurrentYear()
                )
                return@map list.flatten().first { it == currentDayEntry }
            }
            .subscribe { currentSelectedDay.postValue(it) }
    }

    fun onDaySelected(dayEntry: DayEntry) {
        //posts the date strings from the day entry passed
        fun postDate(dayEntry: DayEntry) {
            val currentYear = dayEntry.year
            val currentMonth = (dayEntry.monthOfTheYear + 1)
            val currentDay = dayEntry.dayOfTheMonth
            val currentDayString = when (currentDay < 10) {
                true -> "0$currentDay"
                false -> "$currentDay"
            }
            val currentMonthString = when (currentMonth < 10) {
                true -> "0$currentMonth"
                false -> "$currentMonth"
            }

            val dateString = "$currentDayString.$currentMonthString.$currentYear"
            currentCalendarDate.postValue(dateString)

            val monthString = "${Month.values()[dayEntry.monthOfTheYear]}"
            currentCalendarMonth.postValue(monthString)
        }

        if (lastSelected == dayEntry) return
        dayDisposable?.dispose()
        lastSelected = dayEntry

        dayDisposable = repository.getDayEntry(dayEntry)
            .subscribeOn(Schedulers.io())
            .subscribe { it ->
                postDate(it)
                currentSelectedDay.postValue(it)
            }
    }

    fun getDayEntryIdForModification(dayEntry: DayEntry): LiveData<String> {
        val liveData = MutableLiveData<String>()
        val disposable = repository.getDayEntry(dayEntry)
            .subscribeOn(Schedulers.io())
            .map { it -> it.id }
            .take(1)
            .subscribe { liveData.postValue(it) }

        return liveData
    }

    override fun onCleared() {
        super.onCleared()
        monthDisposable?.dispose()
        dayDisposable?.dispose()

        repository.resetToCurrent()
        getCurrentFiveWeeks()
    }
}