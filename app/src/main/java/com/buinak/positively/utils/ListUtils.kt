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

package com.buinak.positively.utils

import com.buinak.positively.entities.DayEntry
import java.util.*

object ListUtils {
        //takes a list of day entries and outputs a list of months with those entries and null entries
        // for the entire year
        fun createListOfMonths(dayEntries: List<DayEntry>): List<List<DayEntry?>> {

            //ASSUMING ALL OF THE ENTRIES HAVE THE SAME YEAR!!
            val year: Int = when (dayEntries.size) {
                0 -> Calendar.getInstance().get(Calendar.YEAR)
                else -> dayEntries[0].year
            }

            val resultList = ArrayList<ArrayList<DayEntry?>>()
            for (i in 1..12) {
                val monthList = ArrayList<DayEntry?>()
                for (dayIndex in 1..CalendarUtils.getAmountOfDaysInAMonth(year, i)) {
                    monthList.add(null)
                }
                resultList.add(monthList)

                //filter the list by months, add entries to the resulting list
                dayEntries.filter { dayEntry -> dayEntry.monthOfTheYear == i }
                    .forEach { (resultList[i - 1])[it.dayOfTheMonth - 1] = it }
            }
            return resultList
        }
}