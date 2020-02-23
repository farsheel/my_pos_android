package com.farsheel.mypos

import androidx.multidex.MultiDexApplication
import com.farsheel.mypos.di.repositoryModule
import com.farsheel.mypos.di.storageModule
import com.farsheel.mypos.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class BaseApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(listOf(storageModule, viewModelModule, repositoryModule))
        }
    }
}