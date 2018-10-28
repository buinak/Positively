package com.buinak.positively.data

import com.buinak.positively.data.local.LocalDataModule
import com.buinak.positively.data.local.LocalDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module (includes = arrayOf(LocalDataModule::class))
class DataModule {
    @Provides
    @Singleton
    fun provideDataSource(localDataSource: LocalDataSource): DataSource = DataRepository(localDataSource)
}