package com.example.socialapp.android

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.example.socialapp.android.common.datastore.UserSettings
import com.example.socialapp.android.common.datastore.toAuthResultData
import kotlinx.coroutines.flow.map

class MainActivityViewModel (private val dataStore: DataStore<UserSettings>): ViewModel() {

    val authState = dataStore.data.map {
        it.toAuthResultData().token // map user setting to auth result data get the token
    }

}