package com.example.socialapp.di

import com.example.socialapp.common.data.IOSUserPreferences
import com.example.socialapp.common.data.createDataStore
import com.example.socialapp.common.data.local.UserPreferences
import org.koin.dsl.module


// native component for user settings
actual val platformModule = module {
    single<UserPreferences> {IOSUserPreferences(get())}

    single {
        createDataStore()
    }
}