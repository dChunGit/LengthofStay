package com.simplex.whatsup

import android.app.Application
import android.util.Log
import com.facebook.stetho.Stetho
import com.simplex.whatsup.di.appComponent
import com.simplex.whatsup.di.dataModule
import com.simplex.whatsup.di.extrasModule
import com.simplex.whatsup.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("WHATSUP", "Found application")
        Stetho.initializeWithDefaults(this)

        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(appComponent)
        }
    }
}