package com.ryen.sunnah_alhadi.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = SunnahEntity::class,
            parentColumns = ["id"],
            childColumns = ["sunnahId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sunnahId"], unique = true)]
)
data class BookmarkEntity(
    @PrimaryKey val sunnahId: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)