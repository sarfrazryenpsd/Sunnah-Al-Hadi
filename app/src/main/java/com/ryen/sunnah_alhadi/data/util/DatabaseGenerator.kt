package com.ryen.sunnah_alhadi.data.util

import android.content.Context
import androidx.room.Room
import com.ryen.sunnah_alhadi.data.local.database.AppDatabase
import com.ryen.sunnah_alhadi.data.local.database.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.database.entity.ExtraContent
import com.ryen.sunnah_alhadi.data.local.database.entity.Reference
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
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

    // Create the database file from JSON
    suspend fun generateDatabaseFromJson() = withContext(Dispatchers.IO) {
        // Create a temporary database file
        val dbFile = context.getDatabasePath("sunnah_database.db")
        if (dbFile.exists()) {
            dbFile.delete()
        }

        // Create the database with Room
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "sunnah_database.db"
        ).build()

        try {
            // Read and parse the JSON
            val jsonString = context.assets.open("sunnahs.json").bufferedReader().use { it.readText() }
            val sunnahData = Json.decodeFromString<SunnahData>(jsonString)

            // Convert and insert data
            val categoryEntities = sunnahData.categories.map { category ->
                CategoryEntity(id = category.id, topic = category.topic)
            }

            val sunnahEntities = sunnahData.categories.flatMap { category ->
                category.sunnahs.map { sunnah ->
                    SunnahEntity(
                        id = sunnah.id,
                        categoryId = category.id,
                        title = sunnah.title,
                        body = sunnah.body,
                        references = sunnah.references,
                        extra = sunnah.extra
                    )
                }
            }

            // Insert data
            db.categoryDao().insertAll(categoryEntities)
            db.sunnahDao().insertAll(sunnahEntities)
        } finally {
            db.close()
        }

        // Copy the database file to the assets directory (during development)
        // This step would be done manually in a real project
        val outputDir = File(context.filesDir.parent, "assets/database")
        outputDir.mkdirs()

        val outputFile = File(outputDir, "sunnah_database.db")
        dbFile.inputStream().use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }
    }
}