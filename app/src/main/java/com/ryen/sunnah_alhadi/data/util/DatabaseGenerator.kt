package com.ryen.sunnah_alhadi.data.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.ryen.sunnah_alhadi.data.local.database.AppDatabase
import com.ryen.sunnah_alhadi.data.local.database.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.database.entity.ExtraContent
import com.ryen.sunnah_alhadi.data.local.database.entity.Reference
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DatabaseGenerator(private val context: Context) {
    // JSON parsing models
    @Serializable
    data class SunnahData(val categories: List<Category>)

    @Serializable
    data class Category(
        val id: Int,
        val topic: String,
        val sunnahs: List<Sunnah>
    )

    @Serializable
    data class Sunnah(
        val id: String,
        val title: String,
        val body: List<ContentBlock>,
        val references: List<Reference>? = null,
        val extra: List<ExtraContent>? = null
    )

    private val TAG = "DatabaseGenerator"

    suspend fun generateDatabaseForExport() = withContext(Dispatchers.IO) {
        try {
            val tempDbName = "temp_sunnah.db"
            val actualDbFile = context.getDatabasePath(tempDbName)

            // Delete old database file if it exists
            if (actualDbFile.exists()) {
                actualDbFile.delete()
                Log.d(TAG, "Deleted existing temp database file")
            }

            Log.d(TAG, "Starting database generation")

            // Create a fresh database instance
            val db = Room.databaseBuilder(context, AppDatabase::class.java, tempDbName)
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE) // No WAL
                .build()

            // Load and parse JSON from assets
            val jsonString = context.assets.open("sunnahs.json").bufferedReader().use { it.readText() }
            Log.d(TAG, "Loaded JSON data, length: ${jsonString.length}")

            val sunnahData = Json.decodeFromString<SunnahData>(jsonString)
            Log.d(TAG, "Parsed JSON data: ${sunnahData.categories.size} categories found")

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

            // Use a non-suspending transaction wrapper
            db.withTransaction {
                // Insert all categories first
                runBlocking {
                    db.categoryDao().insertAll(categories)
                    Log.d(TAG, "Inserted ${categories.size} categories")

                    // Then insert all sunnahs
                    db.sunnahDao().insertAll(allSunnahs)
                    Log.d(TAG, "Inserted ${allSunnahs.size} sunnahs")
                }
            }

            // Verify inserts outside of transaction - this must complete before export
            val categoryCount = db.categoryDao().getCount()
            val sunnahCount = db.sunnahDao().getCount()
            Log.d(TAG, "Database verification - Category count: $categoryCount, Sunnah count: $sunnahCount")

            if (categoryCount == 0 || sunnahCount == 0) {
                Log.e(TAG, "Database population failed - counts are zero!")
                throw IllegalStateException("Database not populated correctly")
            }

            // Ensure all database operations are completed before closing
            db.query("PRAGMA wal_checkpoint(FULL)", null).close()

            // Close the database properly
            db.close()
            Log.d(TAG, "Database closed properly")

            // Get main database file location
            val mainDbFile = actualDbFile

            // List all related database files
            val dbDir = mainDbFile.parentFile
            val dbFiles = dbDir?.listFiles { file ->
                file.name.startsWith(tempDbName)
            } ?: emptyArray()

            Log.d(TAG, "Found ${dbFiles.size} database-related files")
            dbFiles.forEach { file ->
                Log.d(TAG, "Database file: ${file.name}, size: ${file.length()} bytes")
            }

            // Force a sync to ensure all data is written to disk
            try {
                // Use SQLite directly to ensure database is properly flushed to disk
                val sqliteDb = SQLiteDatabase.openDatabase(
                    mainDbFile.absolutePath,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                )

                // Verify data with direct SQL queries
                val categoryCursor = sqliteDb.rawQuery("SELECT COUNT(*) FROM categories", null)
                var categoryCount1 = 0
                if (categoryCursor.moveToFirst()) {
                    categoryCount1 = categoryCursor.getInt(0)
                }
                categoryCursor.close()

                val sunnahCursor = sqliteDb.rawQuery("SELECT COUNT(*) FROM sunnahs", null)
                var sunnahCount1 = 0
                if (sunnahCursor.moveToFirst()) {
                    sunnahCount1 = sunnahCursor.getInt(0)
                }
                sunnahCursor.close()

                Log.d(TAG, "SQLite direct verification - Categories: $categoryCount1, Sunnahs: $sunnahCount1")

                // Close SQLite connection
                sqliteDb.close()
                Log.d(TAG, "SQLite verification complete")
            } catch (e: Exception) {
                Log.e(TAG, "Error during SQLite verification", e)
            }

            // Wait to ensure all file operations complete
            delay(1000)

            // Copy to export location
            val exportDir = context.getExternalFilesDir(null)
            val exportFile = File(exportDir, "sunnah_database.db")

            try {
                // Use FileInputStream/FileOutputStream for more reliable file copying
                FileInputStream(mainDbFile).use { input ->
                    FileOutputStream(exportFile).use { output ->
                        val buffer = ByteArray(4 * 1024) // 4KB buffer
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                        }
                        output.flush()
                    }
                }
                Log.d(TAG, "Database copied successfully using streams")

                // Verify the copied file exists and has content
                if (exportFile.exists() && exportFile.length() > 0) {
                    Log.i(TAG, "Export file created successfully, size: ${exportFile.length()} bytes")
                } else {
                    Log.e(TAG, "Export file creation failed or file is empty")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error copying database file", e)
                throw e
            }

            Log.i(TAG, "DATABASE GENERATED AND COPIED TO: ${exportFile.absolutePath}")
            Log.i(TAG, "Copy this file to your project's assets/database directory if needed")

            return@withContext exportFile
        } catch (e: Exception) {
            Log.e(TAG, "Error generating database", e)
            e.printStackTrace()
            throw e
        }
    }
}