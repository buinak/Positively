package com.buinak.positively.ui.mainscreen

import com.buinak.positively.data.DataSource
import com.buinak.positively.entities.plain.DayEntry
import com.buinak.positively.entities.plain.Month
import com.buinak.positively.entities.plain.Mood
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainRepository (val dataSource: DataSource) {
    fun getObservableSavedDays(): Observable<List<DayEntry>>
            = dataSource.getAllDays()

    fun addRandomDay() {
        val dayEntry = DayEntry(21, Month.DECEMBER.ordinal, 1997)
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