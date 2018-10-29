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

package com.buinak.positively

import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.buinak.positively.ui.mainscreen.MainActivity
import com.buinak.positively.utils.CalendarUtils
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ActivityTest {

    @Rule
    lateinit var testRule: ActivityTestRule<MainActivity>

    init {
        testRule = ActivityTestRule(MainActivity::class.java)
    }


    @Test
    fun clickAdd1000Times_totalCountShouldNotBeMoreThan365() {
        val year = CalendarUtils.getCurrentYear()
        var totalCount = 0
        (1..12).forEach { totalCount += CalendarUtils.getAmountOfDaysInAMonth(year, it) }

        for (i in 1..1000) {
            onView(withId(R.id.button_add)).perform(click())
        }

        val totalNumberText = testRule.activity.findViewById<TextView>(R.id.textView_totalNumber).text.toString()
        val splitParts = totalNumberText.split(" ")
        val count = splitParts[splitParts.lastIndex].toInt()
        assertTrue(count <= totalCount)
    }
}
