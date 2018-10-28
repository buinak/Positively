package com.buinak.positively.ui.mainscreen

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.buinak.positively.R

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel =  ViewModelProviders.of(this).get(MainViewModel::class.java)

        findViewById<Button>(R.id.button_add).setOnClickListener { viewModel.onAddClicked() }
        findViewById<Button>(R.id.button_reset).setOnClickListener { viewModel.onResetClicked() }

        val textView: TextView = findViewById(R.id.textView)
        viewModel.getMessagesLiveData().observe(this, Observer { message -> textView.text = message })
    }
}
