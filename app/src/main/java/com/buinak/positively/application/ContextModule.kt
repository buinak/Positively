package com.buinak.positively.application

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule (val context: Context) {

    @Provides
    fun provideContext() = context
}