package com.buinak.positively.data.local

import dagger.Module
import dagger.Provides

@Module
class LocalDataModule {
    @Provides
    fun provideLocalDatabaseInteractor() = LocalDatabaseInteractor()

    @Provides
    fun provideLocalDataSource(localDatabaseInteractor: LocalDatabaseInteractor): LocalDataSource =
        LocalDataRepository(localDatabaseInteractor)
}