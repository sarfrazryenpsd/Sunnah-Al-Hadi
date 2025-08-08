package com.ryen.sunnah_alhadi.presentation.components.overlay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.EnglishSubtype
import com.ryen.sunnah_alhadi.domain.model.ExtraContent
import com.ryen.sunnah_alhadi.domain.model.ExtraContentType
import com.ryen.sunnah_alhadi.domain.model.Reference
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.screens.home.HomeEvent
import com.ryen.sunnah_alhadi.presentation.screens.home.HomeViewModel
import com.ryen.sunnah_alhadi.presentation.util.DynamicContentBlockRenderer
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SotdCardContainer(
    isFromNotification: Boolean = false,
    onDismiss: () -> Unit,
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val uiState by homeViewModel.uiState.collectAsState()

    // ✅ Load SOTD based on source
    LaunchedEffect(isFromNotification) {
        if (isFromNotification) {
            // From notification - ensure current SOTD is loaded
            homeViewModel.onEvent(HomeEvent.HandleNotificationLaunch)
        } else {
            // Auto-show - toggle current SOTD
            homeViewModel.onEvent(HomeEvent.ToggleSotd)
        }
    }

    // ✅ Only show if SOTD exists
    uiState.sotd?.let { sunnah ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
        ) {
            Column {

                // SOTD Content
                SotdOverlayContent(
                    sunnah = sunnah,
                    onDismiss = {
                        homeViewModel.onEvent(HomeEvent.MarkSotdAsSeen)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun SotdOverlayContent(
    sunnah: Sunnah,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Prevent dismiss when clicking on card */ },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Enhanced SOTD Card for overlay
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            border = BorderStroke(
                width = 3.dp,
                color = MaterialTheme.colorScheme.tertiary
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Sunnah Title
                Text(
                    text = sunnah.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sunnah Content
                DynamicContentBlockRenderer(
                    contentBlocks = sunnah.body,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Text("Sub'han Allah")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun SotdOverlayPrev() {
    CompositionLocalProvider(
        LocalScreenSize provides ScreenSize.EXPANDED // or whatever default makes sense
    ) {
        SunnahAlHadiTheme(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),

            ) {
            SotdOverlayContent(
                sunnah = Sunnah(
                    id = "",
                    categoryId = 5,
                    title = "Title",
                    body = listOf(
                        ContentBlock(
                            type = ContentType.ENGLISH_TEXT,
                            subtype = EnglishSubtype.NORMAL.name,
                            content = "The beloved Prophet صَلَّى اللهُ عَلَيْهِ وَسَلَّم said: \"By virtue of the righteous Muslim, Allah Almighty removes a calamity from 100 houses in his neighborhood.\""
                        ),
                        ContentBlock(
                            type = ContentType.ENGLISH_TEXT,
                            subtype = EnglishSubtype.NORMAL.name,
                            content = "Then he صَلَّى اللهُ عَلَيْهِ وَسَلَّم recited:"
                        ),
                        ContentBlock(
                            type = ContentType.ARABIC_TEXT,
                            subtype = ArabicSubtype.VERSE.name,
                            content = "وَلَوْلَا دَفْعُ اللَّهِ النَّاسَ بَعْضَهُم بِبَعْضٍۢ لَّفَسَدَتِ الْأَرْضُ"
                        ),
                        ContentBlock(
                            type = ContentType.ENGLISH_TEXT,
                            subtype = EnglishSubtype.TRANSLATION.name,
                            content = "\"And if Allah does not keep away some people by means of others, the earth would have been corrupted.\""
                        )
                    ),
                    references = listOf(
                        Reference("Kanz-ul-Iman, Surah Al-Baqarah, verse 251"),
                        Reference("Majma' Al-Zawa`id, vol. 8, p. 299, Hadith 13533")
                    ),
                    extra = listOf(
                        ExtraContent(
                            type = ExtraContentType.BENEFIT,
                            content = listOf(
                                ContentBlock(
                                    type = ContentType.ENGLISH_TEXT,
                                    subtype = EnglishSubtype.NORMAL.name,
                                    content = "This hadith teaches us the importance of being a righteous neighbor and how our good deeds can benefit the entire community."
                                )
                            )
                        ),
                        ExtraContent(
                            type = ExtraContentType.WARNING,
                            content = listOf(
                                ContentBlock(
                                    type = ContentType.ENGLISH_TEXT,
                                    subtype = EnglishSubtype.NORMAL.name,
                                    content = "Remember that righteousness should be consistent in all aspects of life, not just in public display."
                                )
                            )
                        )
                    ),
                ),
                onDismiss = {}
            )
        }
    }
}