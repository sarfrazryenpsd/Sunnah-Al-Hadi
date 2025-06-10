
package com.ryen.sunnah_alhadi.data.local.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences
import java.io.InputStream
import java.io.OutputStream


// Serializer for Proto DataStore
object ProtoUserPreferencesSerializer : Serializer<ProtoUserPreferences> {
    override val defaultValue: ProtoUserPreferences = ProtoUserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ProtoUserPreferences {
        return try {
            ProtoUserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ProtoUserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}


