package com.buinak.positively.data.local

import com.buinak.positively.entities.plain.DayEntry
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.Action
import io.reactivex.subjects.PublishSubject
import io.realm.Realm
import io.realm.kotlin.where

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