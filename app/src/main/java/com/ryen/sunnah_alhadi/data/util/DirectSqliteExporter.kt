package com.ryen.sunnah_alhadi.data.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import androidx.core.database.sqlite.transaction
import kotlinx.serialization.encodeToString

class DirectSqliteExporter(private val context: Context) {
    private val TAG = "DirectSqliteExporter"

    /**
     * Generate database directly with SQLite APIs rather than Room
     */
    suspend fun generateDatabase(): File = withContext(Dispatchers.IO) {
        val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sunnah_database_direct.db")


        // Delete if exists
        if (outputFile.exists()) {
            outputFile.delete()
            Log.d(TAG, "Deleted existing output file")
        }

        var sqliteDatabase: SQLiteDatabase? = null

        try {
            // Create the database directly with SQLite
            sqliteDatabase = SQLiteDatabase.openOrCreateDatabase(outputFile, null)
            Log.d(TAG, "Created SQLite database directly at: ${outputFile.absolutePath}")

            // Create the tables
            createTables(sqliteDatabase)

            // Load and parse JSON from assets
            val jsonString = context.assets.open("sunnahs.json").bufferedReader().use { it.readText() }
            Log.d(TAG, "Loaded JSON data, length: ${jsonString.length}")

            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            }

            val sunnahData = json.decodeFromString<DatabaseGenerator.SunnahData>(jsonString)
            Log.d(TAG, "Parsed JSON data: ${sunnahData.categories.size} categories found")

            // Begin transaction
            sqliteDatabase.transaction {

                try {
                    // Insert categories
                    val categoryCount = insertCategories(this, sunnahData.categories)
                    Log.d(TAG, "Inserted $categoryCount categories")

                    // Insert sunnahs
                    val sunnahCount = insertSunnahs(this, sunnahData)
                    Log.d(TAG, "Inserted $sunnahCount sunnahs")

                    // Mark transaction successful
                } finally {
                    // End transaction
                }
            }

            // Verify data was inserted
            val verification = verifyDatabase(sqliteDatabase)
            Log.d(TAG, "Direct verification - Categories: ${verification.first}, Sunnahs: ${verification.second}")

            // Force checkpoint to ensure everything is written
            sqliteDatabase.rawQuery("PRAGMA wal_checkpoint(FULL)", null).use {
                // Optional: check results here if you want
            }

            // Close database to ensure all changes are flushed
            sqliteDatabase.close()
            Log.d(TAG, "Database closed properly")

            // Check file size
            Log.i(TAG, "Final database file size: ${outputFile.length()} bytes")

            outputFile
        } catch (e: Exception) {
            Log.e(TAG, "Error generating database directly", e)
            sqliteDatabase?.close()
            throw e
        }
    }

    private fun createTables(db: SQLiteDatabase) {
        // Create categories table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY NOT NULL,
                topic TEXT NOT NULL
            )
        """)

        // Create sunnahs table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS sunnahs (
                id TEXT PRIMARY KEY NOT NULL,
                categoryId INTEGER NOT NULL,
                title TEXT NOT NULL,
                body TEXT NOT NULL,
                referenceData TEXT,
                extra TEXT,
                FOREIGN KEY (categoryId) REFERENCES categories(id) ON DELETE CASCADE
            )
        """)

        // Create index for foreign key
        db.execSQL("CREATE INDEX IF NOT EXISTS index_sunnahs_categoryId ON sunnahs(categoryId)")

        Log.d(TAG, "Database tables created successfully")
    }

    private fun insertCategories(db: SQLiteDatabase, categories: List<DatabaseGenerator.Category>): Int {
        var count = 0

        categories.forEach { category ->
            val values = ContentValues().apply {
                put("id", category.id)
                put("topic", category.topic)
            }

            db.insert("categories", null, values)
            count++
        }

        return count
    }

    private fun insertSunnahs(db: SQLiteDatabase, data: DatabaseGenerator.SunnahData): Int {
        var count = 0
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

        data.categories.forEach { category ->
            category.sunnahs.forEach { sunnah ->
                val values = ContentValues().apply {
                    put("id", sunnah.id)
                    put("categoryId", category.id)
                    put("title", sunnah.title)
                    put("body", json.encodeToString(sunnah.body))
                    put("referenceData", sunnah.references?.let { json.encodeToString(it) })
                    put("extra", sunnah.extra?.let { json.encodeToString(it) })
                }

                db.insert("sunnahs", null, values)
                count++
            }
        }

        return count
    }

    private fun verifyDatabase(db: SQLiteDatabase): Pair<Int, Int> {
        val categoryCount = db.rawQuery("SELECT COUNT(*) FROM categories", null).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

        val sunnahCount = db.rawQuery("SELECT COUNT(*) FROM sunnahs", null).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

        return Pair(categoryCount, sunnahCount)
    }

    /**
     * Utility method to list all database-related files in the application's
     * databases directory
     */
    fun listDatabaseFiles(dbName: String): List<Pair<String, Long>> {
        val databaseDir = context.getDatabasePath(dbName).parentFile
        return databaseDir?.listFiles()?.map { file ->
            Pair(file.name, file.length())
        } ?: emptyList()
    }

    /**
     * Utility method to query any SQLite database file directly
     */
    fun verifyDatabaseFile(dbFile: File): Pair<Int, Int> {
        if (!dbFile.exists()) {
            return Pair(-1, -1)
        }

        var db: SQLiteDatabase? = null

        return try {
            db = SQLiteDatabase.openDatabase(dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)

            val categoryCount = db.rawQuery("SELECT COUNT(*) FROM categories", null).use { cursor ->
                if (cursor.moveToFirst()) cursor.getInt(0) else 0
            }

            val sunnahCount = db.rawQuery("SELECT COUNT(*) FROM sunnahs", null).use { cursor ->
                if (cursor.moveToFirst()) cursor.getInt(0) else 0
            }

            Pair(categoryCount, sunnahCount)
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying database file: ${dbFile.absolutePath}", e)
            Pair(-1, -1)
        } finally {
            db?.close()
        }
    }
}