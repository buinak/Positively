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

package com.buinak.positively.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.buinak.positively.R
import com.buinak.positively.ui.calendarscreen.CalendarActivity
import com.buinak.positively.utils.Constants.DELAY
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    protected lateinit var navigationView: BottomNavigationView
    protected var finish: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())

        navigationView = findViewById(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()
        updateNavigationBarState()
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navigationView.postDelayed({
            val itemId = item.itemId
            when (itemId) {
                R.id.navigation_calendar -> startActivity(
                    Intent(
                        this,
                        CalendarActivity::class.java
                    )
                )
            }
            if (finish) finish()
        }, DELAY)
        return true
    }

    private fun updateNavigationBarState() {
        val actionId = getNavigationMenuItemId()
        selectBottomNavigationBarItem(actionId)
    }

    internal fun selectBottomNavigationBarItem(itemId: Int) {
        val item = navigationView.menu.findItem(itemId)
        item.isChecked = true
    }

    internal abstract fun getContentViewId(): Int

    internal abstract fun getNavigationMenuItemId(): Int

}