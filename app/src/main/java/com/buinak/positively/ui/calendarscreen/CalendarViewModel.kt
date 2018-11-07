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
    private val currentCalendarYear = MutableLiveData<String>()

    private val currentSelectedDay = MutableLiveData<DayEntry>()


    init {
        PositivelyApplication.inject(this)
    }

    private fun getCurrentFiveWeeks() {
        monthDisposable?.dispose()
        monthDisposable = repository.getCurrentFiveWeeks()
            .subscribeOn(Schedulers.io())
            .map { list -> splitTheListIntoWeeks(list) }
            .subscribe { it ->
                weeksForTheSelectedMonth.postValue(it)
                val dateString = "${Month.values()[repository.getCurrentMonth()]}"
                currentCalendarMonth.postValue(dateString)
                currentCalendarYear.postValue(repository.getCurrentYear().toString())
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
    fun getCurrentCalendarYearLiveData(): LiveData<String> = currentCalendarYear

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
        getCurrentFiveWeeks()
    }

    fun onDaySelected(dayEntry: DayEntry) {
        dayDisposable?.dispose()
        dayDisposable = repository.getDayEntry(dayEntry)
            .subscribeOn(Schedulers.io())
            .subscribe { it -> currentSelectedDay.postValue(it) }
    }

    override fun onCleared() {
        super.onCleared()
        monthDisposable?.dispose()
        dayDisposable?.dispose()

        repository.resetToCurrent()
        getCurrentFiveWeeks()
    }
}