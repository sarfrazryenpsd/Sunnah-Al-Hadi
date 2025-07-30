package com.ryen.sunnah_alhadi.presentation.screens.home

import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.EnglishSubtype
import com.ryen.sunnah_alhadi.domain.model.Reference
import com.ryen.sunnah_alhadi.domain.model.Sunnah

data class HomeUiState(
    val userName: String = "",
    val featuredCategories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSotd: Boolean = false,
    val showDisclaimer: Boolean = false,
    val sunnahCount: Map<Int, Int> = emptyMap(),
    val sotd: Sunnah? = null,
    val recentSotd: List<Sunnah> = emptyList(),
    val homeSunnah: Sunnah = com.ryen.sunnah_alhadi.presentation.screens.home.homeSunnah,
)

val homeSunnah: Sunnah = Sunnah(
    id = "HOME_SUNNAH",
    categoryId = 99,
    title = "The excellence of Salat upon the Holy Prophet ﷺ",
    body = listOf(
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL.name,
            content = "The beloved Prophet صَلَّى اللهُ عَلَيْهِ وَسَلَّم has said, \"On Judgement Day, there will be no other shade except for the shade of Arsh of Allah Almighty. Three people will be under the shade of Arsh of Allah Almighty.\""
        ),
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL.name,
            content = "It was humbly asked, ‘Ya Rasoolallah صَلَّى اللهُ عَلَيْهِ وَسَلَّم ! Who will be those people?’ He صَلَّى اللهُ عَلَيْهِ وَسَلَّم replied:  \n" +
                    "1. The person who removes the worry of my Ummati.  \n" +
                    "2. The one who revives my Sunnah.  \n" +
                    "3. The one who recites salat upon me abundantly."
        )
    ),
    references = listOf(
        Reference("Al-Budu-rus-safirah, p. 131, Hadith 366")
    )
)