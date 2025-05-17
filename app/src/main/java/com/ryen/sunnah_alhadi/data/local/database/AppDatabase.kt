package com.ryen.sunnah_alhadi.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ryen.sunnah_alhadi.data.local.database.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.database.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.local.database.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.util.Converters

@Database(
    entities = [CategoryEntity::class, SunnahEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun sunnahDao(): SunnahDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sunnah_database.db"
                )
                    .createFromAsset("database/sunnah_database.db") // Pre-populated DB
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}