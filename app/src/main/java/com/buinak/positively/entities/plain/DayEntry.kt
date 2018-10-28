package com.buinak.positively.entities.plain

import io.realm.RealmObject
import java.time.Year
import java.util.*

open class DayEntry (var dayOfTheMonth: Int = 1,
                var monthOfTheYear: Int = 1,
                var year: Int = 1990,
                var mood: String = "UNKNOWN"): RealmObject() {

    var id: String = UUID.randomUUID().toString()

}