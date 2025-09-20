@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.components


import android.graphics.RenderEffect
import android.graphics.Shader
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.EnglishSubtype
import com.ryen.sunnah_alhadi.domain.model.ExtraContent
import com.ryen.sunnah_alhadi.domain.model.ExtraContentType
import com.ryen.sunnah_alhadi.domain.model.Reference
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.util.DynamicContentBlockRendererV2
import com.ryen.sunnah_alhadi.presentation.util.DynamicExtraContentRenderer
import com.ryen.sunnah_alhadi.presentation.util.DynamicReferenceRenderer
import com.ryen.sunnah_alhadi.presentation.viewmodel.ExportAction
import com.ryen.sunnah_alhadi.presentation.viewmodel.ExportState
import com.ryen.sunnah_alhadi.presentation.viewmodel.ImageExportViewModel
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography
import kotlinx.coroutines.launch

// Pager extension functions for cinematic effects
fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

@Composable
fun SunnahPager(
    sunnahs: List<Sunnah>,
    initialPage: Int = 0,
    onDismiss: () -> Unit,
    onBookmarkClick: (String) -> Unit,
    onPageChanged: (Int) -> Unit
) {
    // Prevent crashes with empty lists or invalid indices
    if (sunnahs.isEmpty()) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    val safeInitialPage = initialPage.coerceIn(0, sunnahs.size - 1)
    val pagerState = rememberPagerState(
        initialPage = safeInitialPage,
        pageCount = { sunnahs.size }
    )

    // Handle case where current page becomes invalid due to list changes
    LaunchedEffect(sunnahs.size) {
        when {
            sunnahs.isEmpty() -> {
                // No items left, dismiss immediately to prevent flash
                onDismiss()
            }

            pagerState.currentPage >= sunnahs.size -> {
                // Current page is out of bounds, navigate to last valid page
                val newPage = (sunnahs.size - 1).coerceAtLeast(0)
                pagerState.scrollToPage(newPage)
                onPageChanged(newPage)
            }
        }
    }

    // Optimize page change handling
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != initialPage) {
            onPageChanged(pagerState.currentPage)
        }
    }

    // Handle system back gesture
    BackHandler(onBack = onDismiss)

    // Animated background dimming
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (pagerState.isScrollInProgress) 0.8f else 0.7f,
        animationSpec = tween(150),
        label = "background_alpha"
    )

    // Image export functionality
    val imageExportViewModel: ImageExportViewModel = hiltViewModel()
    val exportState by imageExportViewModel.exportState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Handle export state changes
    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportState.Success -> {
                when (state.action) {
                    ExportAction.SAVE -> {
                        if (state.uri != null) {
                            Toast.makeText(
                                context,
                                "Sunnah card saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    ExportAction.SHARE -> {
                        // Share action is handled by the system share sheet
                    }
                }
                imageExportViewModel.resetState()
            }
            is ExportState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                Log.e("ImageExport", state.message)
                imageExportViewModel.resetState()
            }
            is ExportState.Loading -> {
                when (state.action) {
                    ExportAction.SAVE -> {
                        Toast.makeText(context, "Saving sunnah card...", Toast.LENGTH_SHORT).show()
                    }

                    ExportAction.SHARE -> {
                        Toast.makeText(
                            context,
                            "Preparing sunnah card for sharing...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backgroundAlpha))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        // 70% screen height container with proper bounds
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.Center)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { /* Prevent dismissal when tapping content */ }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 16.dp,
                contentPadding = PaddingValues(horizontal = 32.dp),
                // Optimize page loading
                beyondViewportPageCount = 5
            ) { page ->
                // FIXED: Safe access with bounds checking in the pager itself
                if (page < sunnahs.size && sunnahs.isNotEmpty()) {
                    // Memoize cards to prevent unnecessary recomposition
                    key(sunnahs[page].id) {
                        SunnahPagerCard(
                            sunnah = sunnahs[page],
                            pagerState = pagerState,
                            page = page,
                            modifier = Modifier.fillMaxSize(),
                            onExportImage = { sunnah, action ->
                                imageExportViewModel.exportSunnahAsImage(sunnah, action)
                            }
                        )
                    }
                }
            }
        }

        // FIXED: Safe index access with bounds checking
        val currentPage by remember {
            derivedStateOf { pagerState.currentPage }
        }

        // Only show bookmark button if we have a valid current page
        if (currentPage < sunnahs.size) {
            val currentSunnah = sunnahs[currentPage]

            // Export buttons container
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 64.dp, end = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Share button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            imageExportViewModel.exportSunnahAsImage(
                                currentSunnah,
                                ExportAction.SHARE
                            )
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.interface_share),
                        contentDescription = "Share Sunnah Card",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Save button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            imageExportViewModel.exportSunnahAsImage(
                                currentSunnah,
                                ExportAction.SAVE
                            )
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.interface_download),
                        contentDescription = "Save Sunnah Card",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Bookmark button (moved to be below export buttons)
            val bookmarkIcon = if (currentSunnah.isBookmarked) {
                R.drawable.interface_bookmarked
            } else {
                R.drawable.interface_bookmark
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 64.dp, start = 40.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        onBookmarkClick(currentSunnah.id)
                    }
            ) {
                Icon(
                    painter = painterResource(bookmarkIcon),
                    contentDescription = if (currentSunnah.isBookmarked) "Remove Bookmark" else "Add Bookmark",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SunnahPagerCard(
    sunnah: Sunnah,
    pagerState: PagerState,
    page: Int,
    modifier: Modifier = Modifier,
    onExportImage: (Sunnah, ExportAction) -> Unit = { _, _ -> }
) {
    val startOffset by remember(pagerState) {
        derivedStateOf { pagerState.startOffsetForPage(page) }
    }

    val shouldApplyEffects = startOffset > 0.05f
    val blurRadiusPx = (startOffset * 20f).coerceAtLeast(0.1f)
    val blurDp = with(LocalDensity.current) { blurRadiusPx.toDp() }

    // Give space for blur to expand + disable clipping
    Box(
        modifier = modifier
            .padding(if (shouldApplyEffects) blurDp else 0.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (shouldApplyEffects) {
                            translationX = size.width * (startOffset * 0.99f)
                            alpha = (2f - startOffset) / 2f

                            val scale = 1f - (startOffset * 0.1f)
                            scaleX = scale
                            scaleY = scale

                            // Apply blur directly via RenderEffect
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                renderEffect = RenderEffect.createBlurEffect(
                                    blurRadiusPx,
                                    blurRadiusPx,
                                    Shader.TileMode.DECAL
                                ).asComposeRenderEffect()
                            }
                        } else {
                            alpha = 1f
                            scaleX = 1f
                            scaleY = 1f
                            renderEffect = null
                        }
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                SunnahFullCard(
                    sunnah = sunnah,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SunnahFullCard(
    sunnah: Sunnah,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        // Header with title and metadata
        Text(
            text = sunnah.title,
            style = MaterialTheme.appTypography.displayName.copy(
                fontWeight = FontWeight.SemiBold,
                lineHeight = 32.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main content blocks
        DynamicContentBlockRendererV2(
            modifier = Modifier.fillMaxWidth(),
            contentBlocks = sunnah.body
        )

        // Extra content section
        if (!sunnah.extra.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            DynamicExtraContentRenderer(
                extraContent = sunnah.extra,
            )
        }

        // References section
        if (!sunnah.references.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            DynamicReferenceRenderer(
                references = sunnah.references,
            )
        }
    }
}

val sampleContentBlockArabic = ContentBlock(
    type = ContentType.ARABIC_TEXT,
    content = "بسم الله الرحمن الرحيم",
    subtype = "Verse"
)

val mixedContentBlocks = listOf(
    ContentBlock(
        type = ContentType.ENGLISH_TEXT,
        subtype = EnglishSubtype.NORMAL.name,
        content = "The beloved Prophet "
    ),
    ContentBlock(
        type = ContentType.ARABIC_TEXT,
        subtype = ArabicSubtype.HONORIFICS.name,
        content = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم"
    ),
    ContentBlock(
        type = ContentType.ENGLISH_TEXT,
        subtype = EnglishSubtype.NORMAL.name,
        content = " has said, \"On Judgement Day, there will be no other shade except for the shade of Arsh of Allah Almighty.\n\n" + "Three people will be under the shade of Arsh of Allah Almighty.\""
    ),
    ContentBlock(
        type = ContentType.ENGLISH_TEXT,
        subtype = EnglishSubtype.NORMAL.name,
        content = "It was humbly asked, ‘Ya Rasoolallah "
    ),
    ContentBlock(
        type = ContentType.ARABIC_TEXT,
        subtype = ArabicSubtype.HONORIFICS.name,
        content = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم"
    ),
    ContentBlock(
        type = ContentType.ENGLISH_TEXT,
        subtype = EnglishSubtype.NORMAL.name,
        content = " ! Who will be those people?’ He "
    ),
    ContentBlock(
        type = ContentType.ARABIC_TEXT,
        subtype = ArabicSubtype.HONORIFICS.name,
        content = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم"
    ),
    ContentBlock(
        type = ContentType.ENGLISH_TEXT,
        subtype = EnglishSubtype.NORMAL.name,
        content = " replied:\n\n" +
                "1. The person who removes the worry of my Ummah.\n" +
                "2. The one who revives my Sunnah.\n" +
                "3. The one who recites salat upon me abundantly."
    ),
)

val sampleContentBlockEnglish = ContentBlock(
    type = ContentType.ENGLISH_TEXT,
    content = "In the name of Allah, the Most Gracious, the Most Merciful.",
    subtype = "Translation"
)

val sampleExtraContentBenefit = ExtraContent(
    type = ExtraContentType.BENEFIT, // Use your actual ExtraContentType
    content = listOf()
)

val sampleReference = Reference(source = "Sahih Al-Bukhari, Hadith 1")

val sampleSunnah1 = Sunnah(
    id = "1",
    categoryId = 2,
    title = "Greeting Others Appropriately",
    body = mixedContentBlocks,
    extra = listOf(sampleExtraContentBenefit),
    references = listOf(sampleReference),
)

val sampleSunnah2 = Sunnah(
    id = "2",
    categoryId = 3,
    title = "Dua for entering home",
    body = listOf(sampleContentBlockArabic, sampleContentBlockEnglish),
    extra = listOf(sampleExtraContentBenefit),
    references = listOf(sampleReference),
)

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
fun SunnahPagerPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        SunnahPager(
            sunnahs = listOf(sampleSunnah1, sampleSunnah2),
            initialPage = 0,
            onDismiss = {},
            onBookmarkClick = {},
            onPageChanged = {}
        )
    }
}

@Preview(showBackground = true, name = "Pager Card - Active")
@Composable
fun SunnahPagerCard_ActivePreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 1 })
        // To simulate active, ensure page = 0 and pagerState.currentPage = 0, offset = 0
        // For the preview, we explicitly set the pagerState for this card's context

        Box(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxHeight(0.7f)
        ) {
            SunnahPagerCard(
                sunnah = sampleSunnah1,
                pagerState = pagerState,
                page = 0, // This card is page 0
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, name = "Pager Card - Blurred (Offset)")
@Composable
fun SunnahPagerCard_BlurredPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        // Simulate this card (page 1) being to the right of the current page (page 0)
        // So, currentPage = 0, this card's page = 1. Offset will be -1 + offsetFraction
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 1 })

        Box(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxHeight(0.7f)
        ) {
            SunnahPagerCard(
                sunnah = sampleSunnah2,
                pagerState = pagerState, // Pager is on page 0, fractionally moving to 1
                page = 1, // This card is page 1, so it should be offset and blurred
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun SunnahFullCardPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        Surface {
            SunnahFullCard(sunnah = sampleSunnah1)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun SunnahCardHeaderPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        Surface {
            Text(
                text = sampleSunnah1.title,
                style = MaterialTheme.appTypography.displayName.copy(
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 32.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ContentBlockRenderer_ArabicPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        Surface {
            DynamicContentBlockRendererV2(contentBlocks = listOf(sampleContentBlockArabic))
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ContentBlockRenderer_MixedPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        Surface {
            DynamicContentBlockRendererV2(contentBlocks = mixedContentBlocks)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ContentBlockRenderer_EnglishPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        Surface {
            DynamicContentBlockRendererV2(contentBlocks = listOf(sampleContentBlockEnglish))
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ReferenceRendererPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        Surface {
            DynamicReferenceRenderer(references = listOf(sampleReference))
        }
    }
}
