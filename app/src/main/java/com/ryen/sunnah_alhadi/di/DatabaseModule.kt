package com.ryen.sunnah_alhadi.di

import androidx.room.Room
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "sunnah_database.db"
        )
            .createFromAsset("database/sunnah_database.db")
            .build()
    }

    single<CategoryDao> { get<AppDatabase>().categoryDao() }
    single<SunnahDao> { get<AppDatabase>().sunnahDao() }
    single<BookmarkDao> { get<AppDatabase>().bookmarkDao() }
}

