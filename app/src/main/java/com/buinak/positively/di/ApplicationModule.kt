package com.buinak.positively.di

import android.content.Context
import com.buinak.positively.data.DataModule
import com.buinak.positively.data.DataSource
import com.buinak.positively.ui.mainscreen.MainRepository

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module (includes = arrayOf(DataModule::class))
class ApplicationModule {

    @Provides
    @Singleton
    fun provideMainRepository(dataSource: DataSource) = MainRepository(dataSource)
}
