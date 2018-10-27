package com.buinak.positively.di

import com.buinak.positively.application.ContextModule
import com.buinak.positively.ui.mainscreen.MainActivity
import com.buinak.positively.ui.mainscreen.MainViewModel
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class, ContextModule::class))
interface ApplicationComponent {

    fun inject(mainViewModel: MainViewModel)

}
