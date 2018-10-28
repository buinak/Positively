/*
 * Copyright 2018 Konstantin Buinak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.buinak.positively.application

import android.app.Application
import com.buinak.positively.di.ApplicationComponent
import com.buinak.positively.di.DaggerApplicationComponent
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