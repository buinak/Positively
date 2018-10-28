package com.buinak.positively.ui.mainscreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buinak.positively.application.PositivelyApplication
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var repository: MainRepository

    private val messages: MutableLiveData<String> = MutableLiveData()


    init {
        PositivelyApplication.inject(this)

    }

    fun getMessagesLiveData(): LiveData<String> = messages
}