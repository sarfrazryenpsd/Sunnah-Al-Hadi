package com.ryen.sunnah_alhadi.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ryen.sunnah_alhadi.data.local.database.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.database.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.local.database.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.util.Converters
import com.ryen.sunnah_alhadi.data.util.DatabaseGenerator
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors

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
        private const val DATABASE_NAME = "sunnah_database.db"
        private val TAG = "AppDatabase"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    // First, try loading from assets
                    val assetExists = try {
                        context.assets.open("database/$DATABASE_NAME").use { true }
                    } catch (e: Exception) {
                        Log.w(TAG, "Database not found in assets, falling back to runtime creation")
                        false
                    }

                    val builder = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )

                    // If the pre-populated database exists in assets, use it
                    if (assetExists) {
                        Log.d(TAG, "Using pre-populated database from assets")
                        builder.createFromAsset("database/$DATABASE_NAME")
                    } else {
                        // Otherwise, set up a fallback for development
                        Log.d(TAG, "Setting up fallback database initialization")
                        builder.addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                Log.d(TAG, "Database created, will populate from JSON")

                                // Populate database from JSON in assets
                                // Use a single-thread executor to ensure sequential operations
                                val executor = Executors.newSingleThreadExecutor()
                                executor.execute {
                                    runBlocking {
                                        val database = getDatabase(context)
                                        populateDatabaseFromJson(context, database)

                                        // Verify population was successful
                                        val categoryCount = database.categoryDao().getCount()
                                        val sunnahCount = database.sunnahDao().getCount()
                                        Log.d(TAG, "Database population complete - Categories: $categoryCount, Sunnahs: $sunnahCount")
                                    }
                                }
                            }
                        })
                    }

                    val instance = builder.build()
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating database", e)
                    throw e
                }
            }
        }

        /**
         * Fallback method to populate database from JSON during development
         */
        private suspend fun populateDatabaseFromJson(context: Context, database: AppDatabase) {
            try {
                Log.d(TAG, "Populating database from JSON")
                val jsonString = context.assets.open("sunnahs.json").bufferedReader().use { it.readText() }
                val sunnahData = Json.decodeFromString<DatabaseGenerator.SunnahData>(jsonString)

                // Batch process categories and sunnahs
                // Create entity lists first
                val categories = sunnahData.categories.map { category ->
                    CategoryEntity(
                        id = category.id,
                        topic = category.topic
                    )
                }

                val allSunnahs = mutableListOf<SunnahEntity>()
                sunnahData.categories.forEach { category ->
                    val sunnahs = category.sunnahs.map { sunnah ->
                        SunnahEntity(
                            id = sunnah.id,
                            categoryId = category.id,
                            title = sunnah.title,
                            body = sunnah.body,
                            references = sunnah.references,
                            extra = sunnah.extra
                        )
                    }
                    allSunnahs.addAll(sunnahs)
                }

                // Use withTransaction which works with suspend functions
                database.withTransaction {
                    // Insert all categories first
                    database.categoryDao().insertAll(categories)
                    Log.d(TAG, "Inserted ${categories.size} categories")

                    // Then insert all sunnahs
                    database.sunnahDao().insertAll(allSunnahs)
                    Log.d(TAG, "Inserted ${allSunnahs.size} sunnahs")
                }

                Log.d(TAG, "Database successfully populated from JSON")
            } catch (e: Exception) {
                Log.e(TAG, "Error populating database from JSON", e)
                e.printStackTrace()
            }
        }
    }
}