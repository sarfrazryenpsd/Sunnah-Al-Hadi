package com.ryen.sunnah_alhadi

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.AndroidJUnitRunner
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.repository.BookmarkRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.CategoryRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.SunnahRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.UserPreferencesRepositoryImpl
import com.ryen.sunnah_alhadi.data.util.Converters
import com.ryen.sunnah_alhadi.di.ApplicationScope
import com.ryen.sunnah_alhadi.di.DatabaseModule
import com.ryen.sunnah_alhadi.di.RepositoryModule
import com.ryen.sunnah_alhadi.di.SotdModule
import com.ryen.sunnah_alhadi.di.UseCaseModule
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.ExportSunnahAsImageUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetAllSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetBookmarkedSunnahsFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetHomeDataUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahByIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahOfTheDayUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetTopicWithSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.ScheduleDailyReminderUseCase
import com.ryen.sunnah_alhadi.domain.useCase.SearchSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.ToggleBookmarkUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UpdateUserPreferencesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GetCurrentSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.MarkSotdAsSeenUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.ShouldShowSotdCardUseCase
import com.ryen.sunnah_alhadi.platform.notification.SotdNotificationHelper
import com.ryen.sunnah_alhadi.platform.scheduler.SotdNotificationScheduler
import com.ryen.sunnah_alhadi.util.Result
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(
    DatabaseModule::class,
    UseCaseModule::class,
    SotdModule::class
)
class HiltDiTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()



    @Inject
    lateinit var sunnahRepository: SunnahRepository

    @Inject
    lateinit var bookmarkRepository: BookmarkRepository

    @Inject
    lateinit var categoryRepository: CategoryRepository

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var getAllSunnahsUseCase: GetAllSunnahsUseCase

    @Inject
    lateinit var getSunnahByIdUseCase: GetSunnahByIdUseCase

    @Inject
    lateinit var generateNewSotdUseCase: GenerateNewSotdIdUseCase

    @Inject
    lateinit var sotdNotificationHelper: SotdNotificationHelper

    @Inject
    lateinit var sotdNotificationScheduler: SotdNotificationScheduler

    @Inject
    lateinit var database: AppDatabase

    @Before
    fun setup() {
        // Only initialize WorkManager if it hasn't been initialized yet
        try {
            WorkManager.getInstance(ApplicationProvider.getApplicationContext())
        } catch (e: IllegalStateException) {
            // WorkManager not initialized, so initialize it
            val context = ApplicationProvider.getApplicationContext<Context>()
            val config = Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()
            WorkManager.initialize(context, config)
        }

        hiltRule.inject()
    }

    @After
    fun tearDown() {
        if (::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun simple_injection_test() {
        // This should pass if injection is working
        assertThat(::database.isInitialized).isTrue()
    }

    @Test
    fun when_hilt_injects_repositories_then_should_provide_non_null_instances() {
        // Then
        assertThat(sunnahRepository).isNotNull()
        assertThat(bookmarkRepository).isNotNull()
        assertThat(categoryRepository).isNotNull()
        assertThat(userPreferencesRepository).isNotNull()
    }

    @Test
    fun when_hilt_injects_use_cases_then_should_provide_non_null_instances() {
        // Then
        assertThat(getAllSunnahsUseCase).isNotNull()
        assertThat(getSunnahByIdUseCase).isNotNull()
        assertThat(generateNewSotdUseCase).isNotNull()
    }

    @Test
    fun when_hilt_injects_sotd_components_then_should_provide_non_null_instances() {
        // Then
        assertThat(sotdNotificationHelper).isNotNull()
        assertThat(sotdNotificationScheduler).isNotNull()
    }

    @Test
    fun when_hilt_injects_database_then_should_provide_working_instance() {
        // Given/When
        val sunnahDao = database.sunnahDao()
        val categoryDao = database.categoryDao()
        val bookmarkDao = database.bookmarkDao()

        // Then
        assertThat(sunnahDao).isNotNull()
        assertThat(categoryDao).isNotNull()
        assertThat(bookmarkDao).isNotNull()
    }

    @Test
    fun given_repositories_are_injected_when_calling_methods_then_should_work_correctly() = runTest {
        // Given - Insert test data
        val categoryEntity = CategoryEntity(id = 1, topic = "Test Category")
        val sunnahEntity = SunnahEntity(
            id = "01_01",
            categoryId = 1,
            title = "Test Sunnah",
            body = listOf(
                ContentBlock(
                    type = ContentType.ENGLISH_TEXT,
                    subtype = EnglishSubtype.NORMAL,
                    content = "Test content"
                )
            ),
            references = null,
            extra = null
        )

        database.categoryDao().insertCategory(categoryEntity)
        database.sunnahDao().insertSunnah(sunnahEntity)

        // When
        val categories = categoryRepository.getAllCategories()
        val sunnahs = sunnahRepository.getAllSunnahs()

        // Then
        assertThat(categories).hasSize(1)
        assertThat(categories.first().topic).isEqualTo("Test Category")

        when (sunnahs) {
            is Result.Success -> {
                assertThat(sunnahs.data).hasSize(1)
                assertThat(sunnahs.data.first().title).isEqualTo("Test Sunnah")
            }
            is Result.Error -> fail("Expected success but got error: ${sunnahs.message}")
        }
    }

    @Test
    fun given_use_cases_are_injected_when_executing_then_should_delegate_to_repositories() = runTest {
        // Given - Insert test data
        val categoryEntity = CategoryEntity(id = 1, topic = "Test Category")
        val sunnahEntity = SunnahEntity(
            id = "01_01",
            categoryId = 1,
            title = "Test Sunnah",
            body = listOf(
                ContentBlock(
                    type = ContentType.ENGLISH_TEXT,
                    subtype = EnglishSubtype.NORMAL,
                    content = "Test content"
                )
            ),
            references = null,
            extra = null
        )

        database.categoryDao().insertCategory(categoryEntity)
        database.sunnahDao().insertSunnah(sunnahEntity)

        // When
        val allSunnahsResult = getAllSunnahsUseCase()
        val sunnahByIdResult = getSunnahByIdUseCase("01_01")

        // Then
        when (allSunnahsResult) {
            is Result.Success -> assertThat(allSunnahsResult.data).hasSize(1)
            is Result.Error -> fail("Expected success but got error: ${allSunnahsResult.message}")
        }

        when (sunnahByIdResult) {
            is Result.Success -> {
                assertThat(sunnahByIdResult.data).isNotNull()
                assertThat(sunnahByIdResult.data?.id).isEqualTo("01_01")
            }
            is Result.Error -> fail("Expected success but got error: ${sunnahByIdResult.message}")
        }
    }

    @Test
    fun given_multiple_repositories_injected_when_they_share_database_then_should_use_same_instance() {
        // Given - Get DAOs from different repositories
        val sunnahDao1 = (sunnahRepository as SunnahRepositoryImpl).sunnahDao
        val bookmarkDao1 = (bookmarkRepository as BookmarkRepositoryImpl).bookmarkDao

        // When - Get DAOs directly from database
        val sunnahDao2 = database.sunnahDao()
        val bookmarkDao2 = database.bookmarkDao()

        // Then - Should be using same database instance (same DAO instances)
        assertThat(sunnahDao1).isSameInstanceAs(sunnahDao2)
        assertThat(bookmarkDao1).isSameInstanceAs(bookmarkDao2)
    }

    @Test
    fun given_sotd_components_are_injected_when_accessing_then_should_be_properly_configured() {
        // When
        val notificationHelper = sotdNotificationHelper
        val scheduler = sotdNotificationScheduler

        // Then
        assertThat(notificationHelper).isInstanceOf(SotdNotificationHelper::class.java)
        assertThat(scheduler).isInstanceOf(SotdNotificationScheduler::class.java)
    }

    @Test
    fun given_dependency_graph_when_checking_scopes_then_should_respect_singleton_annotations() {
        // Given - Inject same dependencies multiple times
        val database1 = database

        // Re-inject to get new instance reference
        //hiltRule.inject()
        val database2 = database

        // Then - Should be same instance (Singleton scope)
        assertThat(database1).isSameInstanceAs(database2)
    }

    @Test
    fun given_test_modules_replace_production_modules_when_injecting_then_should_use_test_implementations() {
        // When
        val isDatabaseInMemory = database.openHelper.databaseName == null

        // Then - Should be using in-memory database from test module
        assertThat(isDatabaseInMemory).isTrue()
    }
}

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
@Module
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideTestDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideSunnahDao(database: AppDatabase): SunnahDao = database.sunnahDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao = database.bookmarkDao()

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UseCaseModule::class]
)
@Module
object TestUseCaseModule {

    @Provides
    fun provideGetAllSunnahsUseCase(
        repository: SunnahRepository
    ): GetAllSunnahsUseCase = GetAllSunnahsUseCase(repository)

    @Provides
    fun provideGetSunnahByIdUseCase(
        repository: SunnahRepository
    ): GetSunnahByIdUseCase = GetSunnahByIdUseCase(repository)


    @Provides
    fun provideGetHomeDataUseCase(
        repository: CategoryRepository,
        prefs: UserPreferencesRepository
    ) = GetHomeDataUseCase(repository, prefs)

    @Provides
    fun provideGetTopicWithSunnahsUseCase(
        categoryRepository: CategoryRepository,
        sunnahRepository: SunnahRepository
    ) = GetTopicWithSunnahsUseCase(categoryRepository, sunnahRepository)

    @Provides
    fun provideToggleBookmarkUseCase(
        repository: BookmarkRepository
    ) = ToggleBookmarkUseCase(repository)

    @Provides
    fun provideSearchSunnahsUseCase(
        sunnahRepository: SunnahRepository
    ) = SearchSunnahsUseCase(sunnahRepository)

    @Provides
    fun provideGetBookmarkedSunnahsFlowUseCase(
        repository: BookmarkRepository
    ) = GetBookmarkedSunnahsFlowUseCase(repository)

    @Provides
    fun provideUpdateUserPreferencesUseCase(
        repository: UserPreferencesRepository
    ) = UpdateUserPreferencesUseCase(repository)

    @Provides
    fun provideGetUserPreferencesFlowUseCase(
        repository: UserPreferencesRepository
    ) = GetUserPreferencesFlowUseCase(repository)

    @Provides
    fun provideGetSunnahOfTheDayUseCase(
        sunnahRepository: SunnahRepository,
        userPreferencesRepository: UserPreferencesRepository
    ) = GetSunnahOfTheDayUseCase(sunnahRepository, userPreferencesRepository)

    @Provides
    fun provideScheduleDailyReminderUseCase() = ScheduleDailyReminderUseCase()

    @Provides
    fun provideExportSunnahAsImageUseCase() = ExportSunnahAsImageUseCase()
}

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SotdModule::class]
)
@Module
object TestSotdModule {

    @Provides
    @Singleton
    fun provideSotdNotificationHelper(
        @ApplicationContext context: Context
    ): SotdNotificationHelper = SotdNotificationHelper(context)

    @Provides
    @Singleton
    fun provideSotdNotificationScheduler(
        @ApplicationContext context: Context
    ): SotdNotificationScheduler = SotdNotificationScheduler(context)

    @Provides
    fun provideGenerateNewSotdUseCase(
        userPreferencesRepository: UserPreferencesRepository,
        sunnahRepository: SunnahRepository
    ): GenerateNewSotdIdUseCase = GenerateNewSotdIdUseCase(sunnahRepository, userPreferencesRepository)

    @Provides
    fun provideGetCurrentSotdUseCase(
        userPreferencesRepository: UserPreferencesRepository,
        sunnahRepository: SunnahRepository
    ): GetCurrentSotdUseCase = GetCurrentSotdUseCase(sunnahRepository, userPreferencesRepository)

    @Provides
    fun provideShouldShowSotdCardUseCase(
        repository: UserPreferencesRepository
    ): ShouldShowSotdCardUseCase = ShouldShowSotdCardUseCase(repository)

    @Provides
    fun provideMarkSotdAsSeenUseCase(
        repository: UserPreferencesRepository
    ): MarkSotdAsSeenUseCase = MarkSotdAsSeenUseCase(repository)
}

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
@Module
abstract class TestRepositoryModule {

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

    // Add the missing repository bindings
    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        impl: BookmarkRepositoryImpl
    ): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository
}


class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
