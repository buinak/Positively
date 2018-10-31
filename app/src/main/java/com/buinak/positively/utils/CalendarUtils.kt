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

import com.buinak.positively.entities.plain.DayOfTheWeek
import java.text.SimpleDateFormat
import java.util.*

object CalendarUtils {
        fun getAmountOfDaysInAMonth(year: Int, month: Int) =
            GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH)

        fun getCurrentYear() = Calendar.getInstance().get(Calendar.YEAR)
        fun getCurrentMonth() = Calendar.getInstance().get(Calendar.MONTH)
        fun getCurrentDayOfTheMonth() = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        fun getSpecificDayOfTheWeek(calendar: Calendar) = DayOfTheWeek.valueOf(getDayString(calendar).toUpperCase())
        fun getCurrentDayOfTheWeek() = DayOfTheWeek.valueOf(getDayString(Calendar.getInstance()).toUpperCase())

        private fun getDayString(calendar: Calendar) = SimpleDateFormat("EEEE", Locale.ENGLISH)
            .format(calendar.time.time)
}