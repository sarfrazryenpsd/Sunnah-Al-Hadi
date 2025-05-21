package com.ryen.sunnah_alhadi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.R


private val amiri = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold),
    Font(R.font.amiri_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.amiri_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

private val cinzel_decorative = FontFamily(
    Font(R.font.cinzel_decorative_regular, FontWeight.Normal),
    Font(R.font.cinzel_decorative_bold, FontWeight.Bold),
    Font(R.font.cinzel_decorative_black, FontWeight.Black)
)

private val cormorant_garamond = FontFamily(
    Font(R.font.cormorant_garamond_bold, FontWeight.Bold),
    Font(R.font.cormorant_garamond_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.cormorant_garamond_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.cormorant_garamond_light, FontWeight.Light),
    Font(R.font.cormorant_garamond_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.cormorant_garamond_medium, FontWeight.Medium),
    Font(R.font.cormorant_garamond_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.cormorant_garamond_regular, FontWeight.Normal),
    Font(R.font.cormorant_garamond_semi_bold, FontWeight.SemiBold),
    Font(R.font.cormorant_garamond_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic)
)

private val figtree = FontFamily(
    Font(R.font.figtree_black, FontWeight.Black),
    Font(R.font.figtree_black_italic, FontWeight.Black, FontStyle.Italic),
    Font(R.font.figtree_bold, FontWeight.Bold),
    Font(R.font.figtree_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.figtree_extra_bold, FontWeight.ExtraBold),
    Font(R.font.figtree_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.figtree_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.figtree_light, FontWeight.Light),
    Font(R.font.figtree_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.figtree_medium, FontWeight.Medium),
    Font(R.font.figtree_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.figtree_regular, FontWeight.Normal),
    Font(R.font.figtree_semi_bold, FontWeight.SemiBold),
    Font(R.font.figtree_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic)
)

private val lateef = FontFamily(
    Font(R.font.lateef_bold, FontWeight.Bold),
    Font(R.font.lateef_extra_bold, FontWeight.ExtraBold),
    Font(R.font.lateef_extra_light, FontWeight.ExtraLight),
    Font(R.font.lateef_light, FontWeight.Light),
    Font(R.font.lateef_medium, FontWeight.Medium),
    Font(R.font.lateef_regular, FontWeight.Normal),
    Font(R.font.lateef_semi_bold, FontWeight.SemiBold),
)

private val mirza = FontFamily(
    Font(R.font.mirza_regular, FontWeight.Normal),
    Font(R.font.mirza_bold, FontWeight.Bold),
    Font(R.font.mirza_semi_bold, FontWeight.SemiBold),
    Font(R.font.mirza_medium, FontWeight.Medium),
)

private val notoNaskhArabic = FontFamily(
    Font(R.font.noto_naskh_arabic_regular, FontWeight.Normal),
    Font(R.font.noto_naskh_arabic_medium, FontWeight.Medium),
    Font(R.font.noto_naskh_arabic_bold, FontWeight.Bold),
    Font(R.font.noto_naskh_arabic_semi_bold, FontWeight.SemiBold),
)

private val notoSerifJP = FontFamily(
    Font(R.font.noto_serif_jp_regular, FontWeight.Normal),
    Font(R.font.noto_serif_jp_bold, FontWeight.Bold),
    Font(R.font.noto_serif_jp_medium, FontWeight.Medium),
    Font(R.font.noto_serif_jp_semi_bold, FontWeight.SemiBold),
    Font(R.font.noto_serif_jp_light, FontWeight.Light),
    Font(R.font.noto_serif_jp_extra_light, FontWeight.ExtraLight),
    Font(R.font.noto_serif_jp_extra_bold, FontWeight.ExtraBold),
    Font(R.font.noto_serif_jp_black, FontWeight.Black),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)