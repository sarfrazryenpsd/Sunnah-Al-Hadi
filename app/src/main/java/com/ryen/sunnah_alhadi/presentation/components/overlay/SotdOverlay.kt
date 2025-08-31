package com.ryen.sunnah_alhadi.presentation.components.overlay

import android.graphics.Paint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.EnglishSubtype
import com.ryen.sunnah_alhadi.domain.model.ExtraContent
import com.ryen.sunnah_alhadi.domain.model.ExtraContentType
import com.ryen.sunnah_alhadi.domain.model.Reference
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.SunnahFullCard
import com.ryen.sunnah_alhadi.presentation.screens.home.HomeEvent
import com.ryen.sunnah_alhadi.presentation.screens.home.HomeViewModel
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography

@Composable
fun SotdCardContainer(
    isFromNotification: Boolean = false,
    onDismiss: () -> Unit,
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val uiState by homeViewModel.uiState.collectAsState()

    val containerColor: Color = Color.White
    val cornersRadius: Dp = 32.dp
    val glowingRadius: Dp = 32.dp
    val xShifting: Dp = 0.dp
    val yShifting: Dp = 0.dp
    val glowingColor: Color = MaterialTheme.colorScheme.secondary

    val glow = Modifier.drawBehind {
        val canvasSize = size
        drawContext.canvas.nativeCanvas.apply {
            drawRoundRect(
                0f, // Left
                0f, // Top
                canvasSize.width, // Right
                canvasSize.height, // Bottom
                cornersRadius.toPx(), // Radius X
                cornersRadius.toPx(), // Radius Y
                Paint().apply {
                    color = containerColor.toArgb()
                    isAntiAlias = true
                    setShadowLayer(
                        glowingRadius.toPx(),
                        xShifting.toPx(), yShifting.toPx(),
                        glowingColor.copy(alpha = 0.7f).toArgb()
                    )
                }
            )
        }
    }

    // ✅ Load SOTD based on source
    LaunchedEffect(isFromNotification) {
        if (isFromNotification) {
            // From notification - ensure current SOTD is loaded
            homeViewModel.onEvent(HomeEvent.HandleNotificationLaunch)
        }
    }

    // ✅ Only show if SOTD exists
    uiState.sotd?.let { sunnah ->
        BackHandler(onBack = {
            homeViewModel.onEvent(HomeEvent.MarkSotdAsSeen)
            onDismiss()
        })


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    homeViewModel.onEvent(HomeEvent.MarkSotdAsSeen)
                    onDismiss()
                }
        ) {
            // 70% screen height container with proper bounds
            Box(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxHeight(0.7f)
                    .padding(horizontal = 32.dp)
                    .align(Alignment.Center)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* Prevent dismissal when tapping content */ }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "SUNNAH OF THE DAY",
                        style = MaterialTheme.appTypography.displayName,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        SunnahFullCard(
                            sunnah = sunnah,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f)
                        )
                    }
                    Button(
                        onClick = {
                            homeViewModel.onEvent(HomeEvent.MarkSotdAsSeen)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .padding(top = 8.dp)
                            .then(glow)
                    ) {
                        Text(
                            text = "Sub'Haan Allah",
                            style = MaterialTheme.appTypography.notificationType.copy(
                                fontSize = 18.sp,
                            ),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
private fun SotdOverlayPrev() {
    CompositionLocalProvider(
        LocalScreenSize provides ScreenSize.EXPANDED // or whatever default makes sense
    ) {
        SunnahAlHadiTheme(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),

            ) {
            SunnahFullCard(
                sunnah = Sunnah(
                    id = "",
                    categoryId = 5,
                    title = "Visiting grave of parents every friday",
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
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}