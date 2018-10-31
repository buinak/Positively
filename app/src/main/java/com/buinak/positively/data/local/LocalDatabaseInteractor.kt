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

package com.buinak.positively.data.local

import com.buinak.positively.entities.DayEntry
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.realm.Realm

class LocalDatabaseInteractor {

    fun saveDay(dayEntry: DayEntry): Completable {
        return Completable.fromAction {
            val realm = Realm.getDefaultInstance()
            //if the dayentry matches the ID
            if (realm.where(DayEntry::class.java).equalTo("id", dayEntry.id).findFirst() != null) {
                realm.use {
                    realm.executeTransaction { r ->
                        r.where(DayEntry::class.java).equalTo("id", dayEntry.id).findAll()
                            .deleteAllFromRealm()
                        r.copyToRealm(dayEntry)
                    }
                    return@fromAction
                }
                //otherwise if the day entry matches but is not the same (ID doesn't match), return
            } else {
                if (realm.where(DayEntry::class.java)
                        .findAll()
                        .contains(dayEntry)
                ) return@fromAction
            }
            //otherwise save the entry
            realm.use { realm.executeTransaction { r -> r.copyToRealm(dayEntry) } }
        }
    }

    fun getSpecificDay(year: Int, month: Int, day: Int): Single<DayEntry> {
        val realm = Realm.getDefaultInstance()
        realm.use {
            val result = realm.where(DayEntry::class.java)
                .findAll()
                .firstOrNull { dayEntry ->
                    dayEntry.year == year &&
                            dayEntry.monthOfTheYear == month &&
                            dayEntry.dayOfTheMonth == day
                }
            return when (result) {
                null -> {
                    val newDay = DayEntry(day, month, year)
                    realm.executeTransaction { r -> r.copyToRealm(newDay) }
                    Single.just(newDay)
                }
                else -> Single.just(realm.copyFromRealm(result))
            }
        }
    }

    fun getAllDays(year: Int, sorted: Boolean): Observable<List<DayEntry>> {
        val realm = Realm.getDefaultInstance()
        val subject: PublishSubject<List<DayEntry>> = PublishSubject.create()
        realm.use {
            realm.where(DayEntry::class.java)
                .findAllAsync()
                .asFlowable()
                .map { results ->
                    val list = ArrayList<DayEntry>(results.size)
                    //ADD UNMANAGED REALM OBJECTS TO A NEW LIST
                    for (dayEntry in results) {
                        //IF YEAR FILTERING IS ON, FILTER BY YEAR
                        when (year) {
                            0 -> list.add(realm.copyFromRealm(dayEntry))
                            dayEntry.year -> list.add(realm.copyFromRealm(dayEntry))
                        }
                    }
                    if (sorted) list.sort()
                    return@map list.toList()
                }
                .subscribe { resultList -> subject.onNext(resultList) }
        }
        return subject
    }

    fun removeAllDays(): Completable {
        return Completable.fromAction {
            val realm = Realm.getDefaultInstance()
            realm.use {
                realm.executeTransaction { r ->
                    r.where(DayEntry::class.java)
                        .findAll()
                        .deleteAllFromRealm()
                }
            }
        }
    }
}