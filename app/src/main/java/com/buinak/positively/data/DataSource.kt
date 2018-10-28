package com.buinak.positively.data

import com.buinak.positively.entities.plain.DayEntry
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

interface DataSource {
    fun saveDay(dayEntry: DayEntry): Completable
    fun removeAllDays(): Completable
    fun getAllDays(): Observable<List<DayEntry>>
}