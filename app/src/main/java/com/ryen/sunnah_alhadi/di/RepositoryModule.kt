package com.ryen.sunnah_alhadi.di

import com.ryen.sunnah_alhadi.data.repository.BookmarkRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.CategoryRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.SunnahRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.UserPreferencesRepositoryImpl
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<BookmarkRepository> { BookmarkRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<SunnahRepository> { SunnahRepositoryImpl(get(), get()) }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(androidContext()) }
}