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

package com.buinak.positively.entities

import io.realm.RealmObject
import java.util.*

open class DayEntry (var dayOfTheMonth: Int = 1,
                     var monthOfTheYear: Int = 1,
                     var year: Int = 1990,
                     var mood: String = "UNKNOWN",
                     var note: String = "",
                     var dayOfTheWeek: String = "",
                     var id: String = UUID.randomUUID().toString()
) : RealmObject(), Comparable<DayEntry> {


    override fun compareTo(other: DayEntry): Int = when {
        year != other.year -> year.compareTo(other.year)
        monthOfTheYear != other.monthOfTheYear -> monthOfTheYear.compareTo(other.monthOfTheYear)
        dayOfTheMonth != other.dayOfTheMonth -> dayOfTheMonth.compareTo(other.dayOfTheMonth)
        else -> 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DayEntry) return false

        if (id == other.id) return true

        if (dayOfTheMonth != other.dayOfTheMonth) return false
        if (monthOfTheYear != other.monthOfTheYear) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dayOfTheMonth
        result = 31 * result + monthOfTheYear
        result = 31 * result + year
        return result
    }

    fun getDateString(): String {

        val yearString = year.toString()
        val monthString: String = when (monthOfTheYear.toString().length) {
            1 -> if (monthOfTheYear < 9) "0${monthOfTheYear + 1}" else "${monthOfTheYear + 1}"
            else -> "${monthOfTheYear + 1}"
        }
        val dayString: String = when (dayOfTheMonth.toString().length) {
            1 -> "0$dayOfTheMonth"
            else -> dayOfTheMonth.toString()
        }
        return "$dayString.$monthString.$yearString"
    }


}