package com.ryen.sunnah_alhadi.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ryen.sunnah_alhadi.data.local.database.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.database.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.database.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.local.database.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.database.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.util.Converters

@Database(
    entities = [
        CategoryEntity::class,
        SunnahEntity::class,
        BookmarkEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun sunnahDao(): SunnahDao
    abstract fun bookmarkDao(): BookmarkDao
}
