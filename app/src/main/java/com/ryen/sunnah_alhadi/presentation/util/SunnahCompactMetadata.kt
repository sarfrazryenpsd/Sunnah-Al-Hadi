package com.ryen.sunnah_alhadi.presentation.util

import androidx.annotation.DrawableRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.ExtraContentType
import com.ryen.sunnah_alhadi.domain.model.Sunnah

data class ExtraContentMetaInfo(
    @param:DrawableRes val icon: Int,
    val colors: ExtraContentColor
)

@Composable
fun getExtraContentMetaInfo(type: ExtraContentType): ExtraContentMetaInfo {
    val colors = MaterialTheme.colorScheme.extraContentColors(type)
    val iconRes = when (type) {
        ExtraContentType.PARABLE -> R.drawable.ec_parable
        ExtraContentType.SCHOLARLY_EXPLANATION -> R.drawable.ec_scholar
        ExtraContentType.EXPLANATION -> R.drawable.ec_explanation
        ExtraContentType.TRANSLATION -> R.drawable.ec_translate
        ExtraContentType.HADITH -> R.drawable.ec_hadith
        ExtraContentType.NOTES -> R.drawable.ec_note
        ExtraContentType.WARNING -> R.drawable.ec_warning
        ExtraContentType.BENEFIT -> R.drawable.ec_benefit
    }
    return ExtraContentMetaInfo(iconRes, colors)
}

@Composable
fun buildMetaInfoIconsForSunnah(sunnah: Sunnah): List<@Composable () -> Unit> {
    return buildList {
        // ExtraContent icons
        sunnah.extra
            ?.map { it.type }
            ?.distinct()
            ?.forEach { type ->
                val meta = getExtraContentMetaInfo(type)
                add {
                    ECIconBox(
                        iconColor = meta.colors.iconColor,
                        iconRes = meta.icon,
                    )
                }
            }

        // Verse
        if (sunnah.body.any { it.subtype.equals(ArabicSubtype.VERSE.name, ignoreCase = true) }) {
            val style = getSunnahMetaInfoStyle(SunnahMetaInfoType.VERSE)
            add {
                ECIconBox(
                    iconColor = style.iconColor,
                    iconRes = style.icon,
                )
            }
        }

        // Supplication
        if (sunnah.body.any { it.subtype.equals(ArabicSubtype.SUPPLICATION.name, ignoreCase = true) }) {
            val style = getSunnahMetaInfoStyle(SunnahMetaInfoType.SUPPLICATION)
            add {
                ECIconBox(
                    iconColor = style.iconColor,
                    iconRes = style.icon,
                )
            }
        }

        // Reference
        if (!sunnah.references.isNullOrEmpty()) {
            val style = getSunnahMetaInfoStyle(SunnahMetaInfoType.REFERENCE)
            add {
                ECIconBox(
                    iconColor = style.iconColor,
                    iconRes = style.icon,
                )
            }
        }
    }
}

