package com.example.socialapp.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.socialapp.common.data.AndroidUserPreferences
import com.example.socialapp.common.data.UserSettingsSerializer
import com.example.socialapp.common.data.local.PREFERENCE_FILE_NAME
import com.example.socialapp.common.data.local.UserPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


actual val platformModule = module {
    single<UserPreferences> { AndroidUserPreferences(get()) }

    // create a single instance of datastore whenever needed
    single {
        DataStoreFactory.create(
            serializer = UserSettingsSerializer,
            produceFile = {
                // in the app context add the datastore file
                androidContext().dataStoreFile(
                    fileName = PREFERENCE_FILE_NAME
                )
            }
        )
    }
}