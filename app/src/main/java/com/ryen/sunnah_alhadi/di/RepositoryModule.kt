package com.ryen.sunnah_alhadi.di

import com.ryen.sunnah_alhadi.data.repository.BookmarkRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.CategoryRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.SunnahRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.UserPreferencesRepositoryImpl
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindBookmarkRepository(
        bookmarkRepositoryImpl: BookmarkRepositoryImpl
    ): BookmarkRepository

    @Binds
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    abstract fun bindSunnahRepository(
        sunnahRepositoryImpl: SunnahRepositoryImpl
    ): SunnahRepository

    @Binds
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}