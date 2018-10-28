package com.buinak.positively.data.local

import com.buinak.positively.entities.plain.DayEntry
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

class LocalDataRepository(val databaseInteractor: LocalDatabaseInteractor) : LocalDataSource {
    override fun getAllDays(): Observable<List<DayEntry>> = databaseInteractor.getAllDays()

    override fun saveDay(dayEntry: DayEntry): Completable {
        return databaseInteractor.saveDay(dayEntry)
    }
    override fun removeAllDays(): Completable = databaseInteractor.removeAllDays()
}