package com.example.socialapp.common.data

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import androidx.datastore.core.Serializer
import com.example.socialapp.common.data.local.UserSettings

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