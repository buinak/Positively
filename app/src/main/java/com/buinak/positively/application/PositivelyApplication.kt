package com.buinak.positively.application

import android.app.Application
import com.buinak.positively.di.ApplicationComponent
import com.buinak.positively.di.ApplicationModule
import com.buinak.positively.di.DaggerApplicationComponent
import com.buinak.positively.ui.mainscreen.MainActivity
import com.buinak.positively.ui.mainscreen.MainViewModel
import io.realm.Realm

class PositivelyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder()
            .contextModule(ContextModule(this))
            .build()
        Realm.init(this)
    }

    companion object Injector {
        private lateinit var applicationComponent: ApplicationComponent

        fun inject(mainViewModel: MainViewModel) = applicationComponent.inject(mainViewModel)
    }

}