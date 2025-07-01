package com.ryen.sunnah_alhadi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences

val Context.dataStore: DataStore<ProtoUserPreferences> by dataStore(
    fileName = "user_preferences.pb",
    serializer = ProtoUserPreferencesSerializer
)