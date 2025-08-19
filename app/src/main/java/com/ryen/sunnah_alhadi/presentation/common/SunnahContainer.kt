@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.cards.SunnahCompactCard
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme


@Composable
fun SunnahCompactCardContainer(
    sunnahs: List<Sunnah>,
    onSunnahClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .height(600.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(36.dp)
            )
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                items = sunnahs,
                key = { it.id }
            ) { sunnah ->
                SunnahCompactCard(
                    title = sunnah.title,
                    extraIcons = buildList {
                        if (sunnah.extra != null) {
                            add {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Has extra content",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (sunnah.body.any {
                                it.subtype.equals(
                                    ArabicSubtype.VERSE.name,
                                    ignoreCase = true
                                )
                            }) {
                            add {
                                Icon(
                                    painter = painterResource(R.drawable.ec_verse),
                                    contentDescription = "Contains verses",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        if (sunnah.body.any {
                                it.subtype.equals(
                                    ArabicSubtype.SUPPLICATION.name,
                                    ignoreCase = true
                                )
                            }) {
                            add {
                                Icon(
                                    painter = painterResource(R.drawable.ec_supplication),
                                    contentDescription = "Contains supplications",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        if (sunnah.references != null) {
                            add {
                                Icon(
                                    painter = painterResource(R.drawable.ec_reference),
                                    contentDescription = "Has reference",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    },
                    borderColor = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSunnahClick(sunnah.id) }
                )
            }
        }
    }
}

/*@Preview
@Composable
fun SunnahContainerPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ){
        val sunnahs = listOf(
            Sunnah(
                id = "1",
                categoryId = 1,
                title = "Sunnah 1",
                body = listOf(
                    ContentBlock(ContentType.ARABIC_TEXT, ArabicSubtype.VERSE.name, "Arabic verse"),
                    ContentBlock(ContentType.ENGLISH_TEXT, "NORMAL", "English translation")
                ),
                extra = listOf()
            ),
            Sunnah(
                id = "2",
                categoryId = 1,
                title = "Sunnah 2",
                body = listOf(
                    ContentBlock(
                        ContentType.ARABIC_TEXT,
                        ArabicSubtype.SUPPLICATION.name,
                        "Arabic supplication"
                    )
                ),
                references = listOf()
            )
        )
        SunnahContainer(
            sunnahs = sunnahs,
            onSunnahClick = {}
        )
    }
}*/

@Preview
@Composable
fun SunnahCompactCardContainerPreview() {
    SunnahAlHadiTheme(windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))) {
        val sunnahs = listOf(
            Sunnah(
                id = "1",
                categoryId = 1,
                title = "Accountability For Rich Neighbours",
                body = listOf(
                    ContentBlock(ContentType.ARABIC_TEXT, ArabicSubtype.VERSE.name, "Verse"),
                    ContentBlock(ContentType.ENGLISH_TEXT, "NORMAL", "Translation")
                ),
                extra = listOf()
            ),
            Sunnah(
                id = "2",
                categoryId = 2,
                title = "A Believer Feeds His Neighbors",
                body = listOf(
                    ContentBlock(ContentType.ARABIC_TEXT, "PARABLE", "Parable")
                ),
                references = listOf()
            ),
            Sunnah(
                id = "3",
                categoryId = 3,
                title = "Additional Veiling While Eating",
                body = listOf(),
                references = listOf()
            )
        )
        SunnahCompactCardContainer(
            sunnahs = sunnahs,
            onSunnahClick = {}
        )
    }
}
