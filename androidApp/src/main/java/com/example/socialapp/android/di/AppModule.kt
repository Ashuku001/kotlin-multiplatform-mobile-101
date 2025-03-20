package com.example.socialapp.android.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.socialapp.android.MainActivityViewModel
import com.example.socialapp.android.auth.login.LoginViewModel
import com.example.socialapp.android.auth.signup.SignupViewModel
import com.example.socialapp.android.common.datastore.UserSettingsSerializer
import com.example.socialapp.android.home.HomeScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// dependency injection using Koin
val appModule = module {
    viewModel {LoginViewModel(get(), get())} // register LoginViewModel on calling koinViewModel() in a composable we receive an instance of LoginViewModel firt get for signup use case second for datastore as required by constructor
    viewModel {SignupViewModel(get(), get())}
    viewModel {MainActivityViewModel(get())}
    viewModel {HomeScreenViewModel()}

    // create a single instance of datastore whenever needed
    single {
        DataStoreFactory.create(
            serializer = UserSettingsSerializer,
            produceFile = {
                // in the app context add the datastore file
                androidContext().dataStoreFile(
                    fileName = "app_user_settings"
                )
            }
        )
    }
}