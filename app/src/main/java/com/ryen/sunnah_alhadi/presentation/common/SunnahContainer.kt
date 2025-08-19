@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.cards.SunnahCompactCard
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

@Composable
fun SunnahContainer(
    modifier: Modifier = Modifier,
    sunnahs: List<Sunnah>,
    onSunnahClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Recently Viewed",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(
                items = sunnahs,
                key = { it.id }
            ) { sunnah ->
                SunnahCompactCard(
                    title = sunnah.title,
                    extraIcons = buildList {
                        // Add extra content icon if available
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

                        // Add verses icon if contains verses
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
                                    contentDescription = "Contains verses",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        // Add reference icon if has reference
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
                        .width(200.dp)
                        .clickable {
                            onSunnahClick(sunnah.id)
                        }
                )
            }
        }
    }
}

@Composable
fun SunnahCompactCardContainer(
    sunnahs: List<Sunnah>,
    onSunnahClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .then(Modifier
                .height(600.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50)
                )),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
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
