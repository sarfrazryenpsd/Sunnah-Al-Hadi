package com.ryen.sunnah_alhadi

/*import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ArabicSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ExtraContent
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ExtraContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.Reference
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DbSeederViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {

    fun seedDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categoryDao = db.categoryDao()
                val sunnahDao = db.sunnahDao()
                val bookmarkDao = db.bookmarkDao()

                // Insert Categories
                val categories = listOf(
                    CategoryEntity(1, "Prayer"),
                    CategoryEntity(2, "Charity"),
                    CategoryEntity(3, "Fasting")
                )
                categoryDao.insertAll(categories)

                // Insert Sunnahs
                val sunnahs = listOf(
                    SunnahEntity(
                        id = "01_01",
                        categoryId = 1,
                        title = "Pray with Focus",
                        body = listOf(
                            ContentBlock(
                                ContentType.ARABIC_TEXT,
                                ArabicSubtype.VERSE,
                                "صَلِّ كَأَنَّكَ تُوَدِّع"
                            ),
                            ContentBlock(
                                ContentType.ENGLISH_TEXT,
                                EnglishSubtype.NORMAL,
                                "Pray as if it's your last prayer"
                            )
                        ),
                        references = listOf(Reference("Bukhari 1")),
                        extra = listOf(
                            ExtraContent(
                                ExtraContentType.EXPLANATION, listOf(
                                    ContentBlock(
                                        ContentType.ENGLISH_TEXT,
                                        EnglishSubtype.TRANSLATION,
                                        "This means to be mindful during prayer."
                                    )
                                )
                            )
                        )
                    ),
                    SunnahEntity(
                        id = "01_02",
                        categoryId = 2,
                        title = "Give Charity Quietly",
                        body = emptyList(),
                        references = null,
                        extra = null
                    )
                )
                sunnahDao.insertAll(sunnahs)

                // Insert Bookmarks
                val bookmarks = listOf(
                    BookmarkEntity(sunnahId = "01_01")
                )
                bookmarkDao.insertAll(bookmarks)

                Log.d("DbSeeder", "Database seeded successfully.")

            } catch (e: Exception) {
                Log.e("DbSeeder", "Error seeding database: ${e.message}", e)
            }
        }
    }
}*/
