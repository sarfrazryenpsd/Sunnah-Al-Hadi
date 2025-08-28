package com.ryen.sunnah_alhadi.presentation.components.cards

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.screens.home.homeSunnahConst
import com.ryen.sunnah_alhadi.presentation.util.DynamicContentBlockRendererV2
import com.ryen.sunnah_alhadi.presentation.util.DynamicReferenceRenderer
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography

@Composable
fun HomeSunnahCard(
    sunnah: Sunnah,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary)
            .innerShadow(
                shape = RectangleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                blur = 12.dp,
                offsetX = 0.dp,
                offsetY = 0.dp,
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(20.dp)
        ) {

            // Sunnah Title
            Text(
                text = sunnah.title,
                style = MaterialTheme.appTypography.homeSunnahTitle.copy(
                    lineHeight = 32.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))
            // Sunnah Content Preview (first few lines)
            DynamicContentBlockRendererV2(
                contentBlocks = sunnah.body, // Show only first 2 blocks
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))


            DynamicReferenceRenderer(
                references = sunnah.references!!,
            )

        }
    }
}

fun Modifier.innerShadow(
    shape: Shape,
    color: Color = Color.Black,
    blur: Dp = 4.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 2.dp,
    spread: Dp = 0.dp
) = this.drawWithContent {

    drawContent()

    drawIntoCanvas { canvas ->

        val shadowSize = Size(size.width + spread.toPx(), size.height + spread.toPx())
        val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

        val paint = Paint()
        paint.color = color

        canvas.saveLayer(size.toRect(), paint)
        canvas.drawOutline(shadowOutline, paint)

        paint.asFrameworkPaint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            if (blur.toPx() > 0) {
                maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
        }

        paint.color = Color.Black

        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)
        canvas.restore()
    }
}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun HomeSunnahPrev() {
    CompositionLocalProvider(
        LocalScreenSize provides ScreenSize.MEDIUM
    ) {
        SunnahAlHadiTheme(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
        ) {
            HomeSunnahCard(sunnah = homeSunnahConst)
        }
    }
}