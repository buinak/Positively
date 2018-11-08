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

package com.buinak.positively.data

import com.buinak.positively.data.local.LocalDataSource
import com.buinak.positively.entities.DayEntry
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class DataRepository(val localDataSource: LocalDataSource) : DataSource {
    override fun getSpecificDayObservable(year: Int, month: Int, day: Int): Observable<DayEntry> =
        localDataSource.getSpecificDayObservable(year, month, day)


    override fun getSpecificDay(year: Int, month: Int, day: Int): Single<DayEntry> =
        localDataSource.getSpecificDay(year, month, day)


    override fun removeAllDays(): Completable = localDataSource.removeAllDays()
    override fun getAllDays(year: Int, sorted: Boolean): Observable<List<DayEntry>> =
            localDataSource.getAllDays(year, sorted)

    override fun saveDay(dayEntry: DayEntry): Completable = localDataSource.saveDay(dayEntry)
}