package com.buinak.positively.ui.mainscreen

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.ui.mainscreen.recyclerview.MoodsRecyclerViewAdapter

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
        val adapter = MoodsRecyclerViewAdapter(ArrayList())
        recyclerView.adapter = adapter

        viewModel.getMoodsLiveData().observe(this, Observer { moods ->
            adapter.moods = moods
            adapter.notifyDataSetChanged()
        })
    }
}
