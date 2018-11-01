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
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CalendarViewModel : ViewModel() {

    @Inject
    lateinit var repository: CalendarRepository

    private val weeksForTheSelectedMonth = MutableLiveData<List<DayEntry>>()


    init {
        PositivelyApplication.inject(this)
        val disposable = repository.getCurrentFiveWeeks()
            .subscribeOn(Schedulers.io())
            .subscribe { it -> weeksForTheSelectedMonth.postValue(it) }
    }

    fun getDaysLiveData(): LiveData<List<DayEntry>> = weeksForTheSelectedMonth
}