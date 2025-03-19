package com.example.socialapp.android.common.datastore

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import androidx.datastore.core.Serializer

// How to read and write the datastore
object UserSettingsSerializer: Serializer<UserSettings>{
    override val defaultValue: UserSettings
        get() = UserSettings()

    override suspend fun readFrom(input: InputStream): UserSettings {
        return try {
            Json.decodeFromString(
                deserializer =  UserSettings.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (serializationExc: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = UserSettings.serializer(),
                value = t,
            ).encodeToByteArray()
        )
    }
}