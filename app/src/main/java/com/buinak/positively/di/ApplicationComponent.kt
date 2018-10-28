package com.buinak.positively.di

import com.buinak.positively.application.ContextModule
import com.buinak.positively.data.DataModule
import com.buinak.positively.ui.mainscreen.MainActivity
import com.buinak.positively.ui.mainscreen.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(ApplicationModule::class, ContextModule::class))
@Singleton
interface ApplicationComponent {
    fun inject(mainViewModel: MainViewModel)
}
