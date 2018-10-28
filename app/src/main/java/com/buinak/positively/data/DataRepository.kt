package com.buinak.positively.data

import com.buinak.positively.data.local.LocalDataSource

class DataRepository(val localDataSource: LocalDataSource) : DataSource {
}