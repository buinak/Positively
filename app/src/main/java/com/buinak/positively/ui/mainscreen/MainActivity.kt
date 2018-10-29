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

package com.buinak.positively.ui.mainscreen

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.ui.mainscreen.recyclerview.MonthsAdapter

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel =  ViewModelProviders.of(this).get(MainViewModel::class.java)

        initialiseButtons()
        initialiseRecyclerView()
    }

    private fun initialiseButtons() {
        findViewById<Button>(R.id.button_add).setOnClickListener { viewModel.onAddClicked() }
        findViewById<Button>(R.id.button_reset).setOnClickListener { viewModel.onResetClicked() }
    }

    private fun initialiseRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MonthsAdapter(ArrayList())
        recyclerView.adapter = adapter

        viewModel.getMoodsLiveData().observe(this, Observer { moods ->
            var count = 0
            moods.forEach { it.forEach { entry -> if (entry != null) count++ } }
            findViewById<TextView>(R.id.textView_totalNumber).text = "TOTAL COUNT = $count"
            adapter.moods = moods
            adapter.notifyDataSetChanged()
        })
    }
}
