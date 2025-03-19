package com.example.socialapp.android

import android.app.Application
import com.example.socialapp.android.di.appModule
import com.example.socialapp.di.getSharedModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

// dependency injection before on launch just before rendering
class SocialApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin{
            // add shared module to coin has dao
            modules(appModule + getSharedModules()) // initialize dependency injection make viewModels available
            androidContext(this@SocialApplication) // set the app context instance to reach koin app configuration
        }
    }
}