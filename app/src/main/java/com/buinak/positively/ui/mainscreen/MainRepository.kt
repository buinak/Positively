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
import com.buinak.positively.entities.plain.Mood
import com.buinak.positively.utils.CalendarUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

fun IntRange.random() =
        Random().nextInt((endInclusive + 1) - start) + start

class MainRepository(val dataSource: DataSource) {
    fun getObservableSavedDays(): Observable<List<DayEntry>> = dataSource.getAllDays(2018, true)

    fun addRandomDay() {
        val year = 2018
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