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
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import com.buinak.positively.entities.Month
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var repository: MainRepository
    private val currentSelectedDay: MutableLiveData<DayEntry> =
        MutableLiveData()
    private val currentMonth: MutableLiveData<String> = MutableLiveData()
    private val disposable: CompositeDisposable = CompositeDisposable()

    private var noteDisposable: Disposable? = null
    private var dayDisposable: Disposable? = null


    init {
        PositivelyApplication.inject(this)
        requestRepositoryDayAndSubscribe()
    }

    fun getCurrentlySelectedDay(): LiveData<DayEntry> = currentSelectedDay
    fun getCurrentMonth(): LiveData<String> = currentMonth

    fun onDayResetToToday() = requestRepositoryDayAndSubscribe()
    fun onDaySelected(dayOfTheWeek: DayOfTheWeek) = requestRepositoryDayAndSubscribe(dayOfTheWeek)
    fun onGoRightClicked() = requestRepositoryDayAndSubscribe(1)
    fun onGoLeftClicked() = requestRepositoryDayAndSubscribe(1, true)


    private fun requestRepositoryDayAndSubscribe(difference: Int, backwards: Boolean = false) {
        dayDisposable?.dispose()
        dayDisposable = repository.getDayWithDifference(difference, backwards)
            .subscribeOn(Schedulers.io())
            .subscribe { it ->
                currentSelectedDay.postValue(it)
                currentMonth.postValue(Month.values()[it.monthOfTheYear].toString())
            }
    }

    private fun requestRepositoryDayAndSubscribe() {
        dayDisposable?.dispose()
        dayDisposable = repository.getToday()
            .subscribeOn(Schedulers.io())
            .subscribe { it ->
                currentSelectedDay.postValue(it)
                currentMonth.postValue(Month.values()[it.monthOfTheYear].toString())
            }
    }

    private fun requestRepositoryDayAndSubscribe(dayOfTheWeek: DayOfTheWeek) {
        dayDisposable?.dispose()
        dayDisposable = repository.getSpecificDay(dayOfTheWeek)
            .subscribeOn(Schedulers.io())
            .subscribe { it ->
                currentSelectedDay.postValue(it)
                currentMonth.postValue(Month.values()[it.monthOfTheYear].toString())
            }
    }

    fun setNoteTextObservable(noteObservable: Observable<String>) {
        noteDisposable?.dispose()
        noteDisposable = noteObservable.debounce(50, TimeUnit.MILLISECONDS)
            .filter { repository.isNoteChanged(it) }
            .subscribe { repository.changeNoteAndSaveCurrentEntry(it) }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}