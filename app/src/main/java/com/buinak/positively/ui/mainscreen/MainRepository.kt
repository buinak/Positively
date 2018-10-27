package com.buinak.positively.ui.mainscreen

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class MainRepository {
    val observableTimer: Observable<Long> = Observable.interval(1, TimeUnit.SECONDS)


}