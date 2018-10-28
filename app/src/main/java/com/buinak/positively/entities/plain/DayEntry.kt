package com.buinak.positively.entities.plain

import java.time.Year
import java.util.*

class DayEntry (val dayOfTheMonth: Int,
                val monthOfTheYear: Month,
                val year: Int,
                var mood: Mood = Mood.UNKNOWN) {

    val id: String = UUID.randomUUID().toString()

}