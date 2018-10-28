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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var repository: MainRepository

    private val moods: MutableLiveData<List<DayEntry>> = MutableLiveData()
    val disposable: CompositeDisposable = CompositeDisposable()


    init {
        PositivelyApplication.inject(this)
        disposable.add(repository.getObservableSavedDays()
            .subscribeOn(Schedulers.io())
            .subscribe { moods.postValue(it) })
    }

    fun getMoodsLiveData(): LiveData<List<DayEntry>> = moods
    fun onAddClicked() = repository.addRandomDay()
    fun onResetClicked() = repository.resetAll()

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}