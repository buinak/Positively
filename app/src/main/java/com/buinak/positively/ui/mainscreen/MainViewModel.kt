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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buinak.positively.application.PositivelyApplication
import com.buinak.positively.entities.plain.DayEntry
import com.buinak.positively.entities.plain.DayOfTheWeek
import com.buinak.positively.entities.plain.Month
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var repository: MainRepository
    private val currentSelectedDay: MutableLiveData<Pair<DayOfTheWeek, DayEntry>> =
        MutableLiveData()
    private val currentMonth: MutableLiveData<String> = MutableLiveData()
    private val disposable: CompositeDisposable = CompositeDisposable()


    init {
        PositivelyApplication.inject(this)
        requestRepositoryDayAndSubscribe()
    }

    fun getCurrentlySelectedDay(): LiveData<Pair<DayOfTheWeek, DayEntry>> = currentSelectedDay
    fun getCurrentMonth(): LiveData<String> = currentMonth

    fun onDayResetToToday() = requestRepositoryDayAndSubscribe()
    fun onDaySelected(dayOfTheWeek: DayOfTheWeek) = requestRepositoryDayAndSubscribe(dayOfTheWeek)
    fun onGoRightClicked() = requestRepositoryDayAndSubscribe(1)
    fun onGoLeftClicked() = requestRepositoryDayAndSubscribe(1, true)


    private fun requestRepositoryDayAndSubscribe(difference: Int, backwards: Boolean = false) {
        disposable.add(repository.getDayWithDifference(difference, backwards)
            .subscribeOn(Schedulers.io())
            .subscribe { it ->
                currentSelectedDay.postValue(it)
                currentMonth.postValue(Month.values()[it.second.monthOfTheYear].toString())
            })
    }

    private fun requestRepositoryDayAndSubscribe() {
        disposable.add(repository.getToday()
            .subscribeOn(Schedulers.io())
            .subscribe { it ->
                currentSelectedDay.postValue(it)
                currentMonth.postValue(Month.values()[it.second.monthOfTheYear].toString())
            })
    }

    private fun requestRepositoryDayAndSubscribe(dayOfTheWeek: DayOfTheWeek) {
        disposable.add(repository.getSpecificDay(dayOfTheWeek)
            .subscribeOn(Schedulers.io())
            .subscribe { it ->
                currentSelectedDay.postValue(it)
                currentMonth.postValue(Month.values()[it.second.monthOfTheYear].toString())
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}