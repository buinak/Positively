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

import com.buinak.positively.entities.plain.DayEntry
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.realm.Realm

class LocalDatabaseInteractor {

    fun saveDay(dayEntry: DayEntry): Completable {
        return Completable.fromAction {
            val realm = Realm.getDefaultInstance()
            realm.use { realm.executeTransaction { r -> r.copyToRealm(dayEntry) } }
        }
    }

    fun getAllDays(): Observable<List<DayEntry>> {
        val realm = Realm.getDefaultInstance()
        val publishSubject = PublishSubject.create<List<DayEntry>>()
        realm.use {
            realm.where(DayEntry::class.java)
                .findAllAsync()
                .asFlowable()
                .subscribe { results ->
                    val list = ArrayList<DayEntry>(results.size)
                    for (dayEntry in results) {
                        list.add(realm.copyFromRealm(dayEntry))
                    }
                    publishSubject.onNext(list)
                }
        }
        return publishSubject
    }

    fun removeAllDays(): Completable {
        return Completable.fromAction {
            val realm = Realm.getDefaultInstance()
            realm.use {
                realm.executeTransaction { r -> r.where(DayEntry::class.java)
                    .findAll()
                    .deleteAllFromRealm() }
            }
        }
    }
}