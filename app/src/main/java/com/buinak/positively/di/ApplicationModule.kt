package com.buinak.positively.di

import android.content.Context
import com.buinak.positively.ui.mainscreen.MainRepository

import dagger.Module
import dagger.Provides

@Module
class ApplicationModule {

    @Provides
    fun provideMainRepository() = MainRepository()
}
