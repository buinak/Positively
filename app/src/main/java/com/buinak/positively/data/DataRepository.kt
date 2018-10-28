package com.buinak.positively.data

import com.buinak.positively.data.local.LocalDataSource
import com.buinak.positively.entities.plain.DayEntry
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

class DataRepository(val localDataSource: LocalDataSource) : DataSource {
    override fun removeAllDays(): Completable = localDataSource.removeAllDays()
    override fun getAllDays(): Observable<List<DayEntry>> = localDataSource.getAllDays()
    override fun saveDay(dayEntry: DayEntry): Completable = localDataSource.saveDay(dayEntry)
}