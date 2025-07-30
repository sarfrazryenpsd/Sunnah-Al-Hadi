package com.ryen.sunnah_alhadi.di

import com.ryen.sunnah_alhadi.data.repository.CategoryRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.SunnahRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.UserPreferencesRepositoryImpl
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindSunnahRepository(
        impl: SunnahRepositoryImpl
    ): SunnahRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository
}
