package com.buinak.positively.ui.mainscreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buinak.positively.application.PositivelyApplication
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var repository: MainRepository

    private val messages: MutableLiveData<String> = MutableLiveData()
    val disposable: CompositeDisposable = CompositeDisposable()


    init {
        PositivelyApplication.inject(this)
        disposable.add(repository.getObservableSavedDays()
            .subscribeOn(Schedulers.io())
            .subscribe { it ->
                var resultString = "DATABASE SIZE = ${it.size} ELEMENTS \n"
                for (dayEntry in it){
                    resultString += "NEW ID(FIRST 5 SYMBOLS): ${dayEntry.id.slice(0..4)}, MOOD = ${dayEntry.mood} \n"
                }
                messages.postValue(resultString)
            })
    }

    fun getMessagesLiveData(): LiveData<String> = messages
    fun onAddClicked() = repository.addRandomDay()
    fun onResetClicked() = repository.resetAll()

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}