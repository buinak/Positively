package com.buinak.positively.ui.mainscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buinak.positively.application.PositivelyApplication
import com.buinak.positively.entities.plain.DayEntry
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var repository: MainRepository

    private val moods: MutableLiveData<List<DayEntry>> = MutableLiveData()
    val disposable: CompositeDisposable = CompositeDisposable()


    init {
        PositivelyApplication.inject(this)
        disposable.add(repository.getObservableSavedDays()
            .subscribeOn(Schedulers.io())
            .subscribe { moods.postValue(it) })
    }

    fun getMoodsLiveData(): LiveData<List<DayEntry>> = moods
    fun onAddClicked() = repository.addRandomDay()
    fun onResetClicked() = repository.resetAll()

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}