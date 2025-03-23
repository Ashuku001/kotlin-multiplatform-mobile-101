package com.example.socialapp.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.example.socialapp.common.data.local.PREFERENCE_FILE_NAME
import com.example.socialapp.common.data.local.UserPreferences
import com.example.socialapp.common.data.local.UserSettings


class IOSUserPreferences(
    // no kotlinx serializer for userSettings like android so pass Preferences (it will be a manual setup)
    private val dataStore: DataStore<Preferences>
): UserPreferences{

    override suspend fun getUserData(): UserSettings {
        TODO("Not yet implemented")
    }

    override suspend fun setUserData(userSettings: UserSettings) {
        TODO("Not yet implemented")
    }

}

// there is a difference on how to instantiate a datastore so a different implementation for IOS
// creating a datastore in IOS
internal fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        produceFile = { // create a file on IOS disk
            val documentaryDirectory: NSURL = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                isDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            (requireNotNull(documentaryDirectory).path + "/$PREFERENCE_FILE_NAME").toPath
        }
    )
}