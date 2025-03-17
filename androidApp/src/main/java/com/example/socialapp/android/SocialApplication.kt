package com.example.socialapp.android

import android.app.Application
import com.example.socialapp.android.di.appModule
import org.koin.core.context.startKoin

// dependency injection before on launch just before rendering
class SocialApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin{
            modules(appModule) // initialize dependency injection make viewModels available
        }
    }
}