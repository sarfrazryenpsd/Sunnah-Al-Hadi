@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.components

// Import for Modifier.blur
// Remove RenderEffect and Shader imports if no longer used elsewhere, or keep if needed
// import androidx.compose.ui.graphics.RenderEffect
// import androidx.compose.ui.graphics.Shader
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.ExtraContent
import com.ryen.sunnah_alhadi.domain.model.ExtraContentType
import com.ryen.sunnah_alhadi.domain.model.Reference
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.cards.ECIconBox
import com.ryen.sunnah_alhadi.presentation.util.buildMetaInfoIconsForSunnah
import com.ryen.sunnah_alhadi.presentation.util.getExtraContentMetaInfo
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography

// Pager extension functions for cinematic effects
fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}

@Composable
fun SunnahPager(
    sunnahs: List<Sunnah>,
    initialPage: Int = 0,
    onDismiss: () -> Unit,
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
                // Memoize cards to prevent unnecessary recomposition
                key(sunnahs[page].id) {
                    SunnahPagerCard(
                        sunnah = sunnahs[page],
                        pagerState = pagerState,
                        page = page,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Conditional indicators for better performance
            if (sunnahs.size > 1) {
                PagerIndicators(
                    pagerState = pagerState,
                    pageCount = sunnahs.size,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
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
    modifier: Modifier = Modifier
) {
    val startOffset = remember(pagerState.currentPage, pagerState.currentPageOffsetFraction) {
        pagerState.startOffsetForPage(page)
    }
    val shouldApplyEffects = startOffset > 0.05f
    val blurRadius = (startOffset * 20f).coerceAtLeast(0.1f)

    val cardModifier = modifier
        .graphicsLayer {
            // Cinematic parallax
            if(shouldApplyEffects){
                translationX = size.width * (startOffset * 0.99f)
                alpha = (2f - startOffset) / 2f

                // Scale effect
                val scale = 1f - (startOffset * 0.1f)
                scaleX = scale
                scaleY = scale
            } else{
                alpha = 1f
                scaleX = 1f
                scaleY = 1f
                renderEffect = null
            }
        }
        .then(
            // Apply blur conditionally based on startOffset
            // Modifier.blur is available on API 31+
            // On older APIs, this will be a no-op, gracefully degrading.
            if (shouldApplyEffects && startOffset > 0.1f && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                Modifier.blur(
                    radius = blurRadius.dp, // Use Dp for blur radius
                    // edgeTreatment can be BlurredEdge or UnblurredEdge (from androidx.compose.ui.draw)
                    // For behavior similar to Shader.TileMode.DECAL, UnblurredEdge might be closer,
                    // or you might not need to specify it if default is fine.
                    // For compose 1.6.0+ edgeTreatment is replaced with BlurredEdgeTreatment
                    edgeTreatment = androidx.compose.ui.draw.BlurredEdgeTreatment.Unbounded // Or .None or .Rectangle
                )
            } else Modifier
        )

    Surface(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        SunnahFullCard(
            sunnah = sunnah,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun SunnahFullCard(
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
        SunnahCardHeader(sunnah = sunnah)

        Spacer(modifier = Modifier.height(16.dp))

        // Main content blocks
        sunnah.body.forEach { contentBlock ->
            ContentBlockRenderer(
                contentBlock = contentBlock,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Extra content section
        if (!sunnah.extra.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            sunnah.extra.forEach { extraContent ->
                ExtraContentRenderer(
                    extraContent = extraContent,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // References section
        if (!sunnah.references.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "References",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            sunnah.references.forEach { reference ->
                ReferenceRenderer(
                    reference = reference,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun SunnahCardHeader(sunnah: Sunnah) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = sunnah.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        // Metadata icons
        val metaIcons = buildMetaInfoIconsForSunnah(sunnah)
        if (metaIcons.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                items(metaIcons.size) { index ->
                    metaIcons[index]()
                }
            }
        }
    }
}

@Composable
private fun ContentBlockRenderer(
    contentBlock: ContentBlock,
    modifier: Modifier = Modifier
) {
    val textStyle = when (contentBlock.type) {
        ContentType.ARABIC_TEXT -> {
            when (contentBlock.subtype.lowercase()) {
                "verse" -> MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    lineHeight = 1.8.em
                )
                "supplication" -> MaterialTheme.typography.headlineSmall.copy(
                    textAlign = TextAlign.Right,
                    lineHeight = 1.6.em
                )
                else -> MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Right,
                    lineHeight = 1.5.em
                )
            }
        }
        ContentType.ENGLISH_TEXT -> {
            when (contentBlock.subtype.lowercase()) {
                "translation" -> MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = MaterialTheme.appTypography.bodyPrimary.fontStyle,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                else -> MaterialTheme.typography.bodyLarge
            }
        }
    }

    Text(
        text = contentBlock.content,
        style = textStyle,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ExtraContentRenderer(
    extraContent: ExtraContent,
    modifier: Modifier = Modifier
) {
    val metaInfo = getExtraContentMetaInfo(extraContent.type)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = metaInfo.colors.iconBackground.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, metaInfo.colors.borderColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                ECIconBox(
                    iconColor = metaInfo.colors.iconColor,
                    iconRes = metaInfo.icon,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = extraContent.type.name.replace("_", " ").titleCase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = metaInfo.colors.iconColor
                )
            }
            // TODO: Render extraContent.content based on its type (e.g., text, list)
            // For now, just displaying the raw content string if it'''s not empty
            /*if (extraContent.content.isNotBlank()) {
                 Text(
                    text = extraContent.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }*/
        }
    }
}

@Composable
private fun ReferenceRenderer(
    reference: Reference,
    modifier: Modifier = Modifier
) {
    Text(
        text = "• ${reference.source}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun PagerIndicators(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            val alpha = if (isSelected) 1f else 0.4f

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(horizontal = 2.dp)
                    .background(
                        color = Color.White.copy(alpha = alpha),
                        shape = CircleShape
                    )
            )
        }
    }
}

// Extension function for title case
private fun String.titleCase(): String {
    return split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}

// --- Previews ---


// Placeholder for ExtraContentType if not defined or to avoid complex dependencies in preview
enum class SampleExtraContentType {
    BENEFIT, STORY, ETIQUETTE, WARNING, VIRTUE, DUAA, HADITH_EXPLANATION
}

// Placeholder for MetaInfoColors
data class SampleMetaInfoColors(
    val iconColor: Color,
    val iconBackground: Color,
    val borderColor: Color
)

// Placeholder for MetaInfo
data class SampleMetaInfo(
    val icon: Int, // Assuming icon is an Int resource
    val colors: SampleMetaInfoColors
)

// Mock/Preview version of getExtraContentMetaInfo
// IMPORTANT: Replace this with a more accurate mock or ensure your actual
// getExtraContentMetaInfo can be called in previews if it has no complex dependencies.
private fun previewGetExtraContentMetaInfo(type: SampleExtraContentType): SampleMetaInfo {
    // Basic placeholder logic
    val colors = when (type) {
        SampleExtraContentType.BENEFIT -> SampleMetaInfoColors(Color.Green, Color.Green.copy(alpha = 0.1f), Color.Green.copy(alpha = 0.3f))
        SampleExtraContentType.WARNING -> SampleMetaInfoColors(Color.Red, Color.Red.copy(alpha = 0.1f), Color.Red.copy(alpha = 0.3f))
        else -> SampleMetaInfoColors(Color.Gray, Color.Gray.copy(alpha = 0.1f), Color.Gray.copy(alpha = 0.3f))
    }
    return SampleMetaInfo(
        icon = android.R.drawable.ic_dialog_info, // Placeholder icon
        colors = colors
    )
}


val sampleContentBlockArabic = ContentBlock(
    type = ContentType.ARABIC_TEXT,
    content = "بسم الله الرحمن الرحيم",
    subtype = "Verse"
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

val sampleExtraContentWarning = ExtraContent(
    type = ExtraContentType.WARNING, // Use your actual ExtraContentType
    content = listOf()
)


val sampleReference = Reference(source = "Sahih Al-Bukhari, Hadith 1")

val sampleSunnah1 = Sunnah(
    id = "1",
    categoryId = 2,
    title = "Greeting Others",
    body = listOf(sampleContentBlockArabic, sampleContentBlockEnglish),
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
        val pagerState = rememberPagerState(initialPage = 0, pageCount = {1})
        // To simulate active, ensure page = 0 and pagerState.currentPage = 0, offset = 0
        // For the preview, we explicitly set the pagerState for this card'''s context


        Box(modifier = Modifier
            .padding(32.dp)
            .fillMaxHeight(0.7f)) {
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
        // So, currentPage = 0, this card'''s page = 1. Offset will be -1 + offsetFraction
        val pagerState = rememberPagerState(initialPage = 0, pageCount = {1})


        Box(modifier = Modifier
            .padding(32.dp)
            .fillMaxHeight(0.7f)) {
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
            SunnahCardHeader(sunnah = sampleSunnah1)
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
            ContentBlockRenderer(contentBlock = sampleContentBlockArabic)
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
            ContentBlockRenderer(contentBlock = sampleContentBlockEnglish)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ExtraContentRendererPreview_Benefit() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        Surface {
            // Temporarily use the previewGetExtraContentMetaInfo for ExtraContentRenderer
            val originalGetMetaInfo = @Composable { type: ExtraContentType -> getExtraContentMetaInfo(type) }
            val previewMetaInfo = @Composable { type: ExtraContentType ->
                // This is a hack for preview. Ideally, your domain ExtraContentType matches SampleExtraContentType
                // or you provide a proper mock for MetaInfo based on your actual ExtraContentType.
                // For this preview, we assume ExtraContentType.BENEFIT can be mapped to SampleExtraContentType.BENEFIT
                previewGetExtraContentMetaInfo(SampleExtraContentType.BENEFIT)
            }
            // This preview will use the mocked getExtraContentMetaInfo
             ExtraContentRenderer(extraContent = sampleExtraContentBenefit)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ExtraContentRendererPreview_Warning() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        Surface {
             ExtraContentRenderer(extraContent = sampleExtraContentWarning)
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
            ReferenceRenderer(reference = sampleReference)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PagerIndicatorsPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        val pagerState = rememberPagerState(initialPage = 1, pageCount= {3})
        Surface(color = Color.DarkGray.copy(alpha = 0.5f)) { // Added background for visibility
            PagerIndicators(pagerState = pagerState, pageCount = 5, modifier = Modifier.padding(16.dp))
        }
    }
}

