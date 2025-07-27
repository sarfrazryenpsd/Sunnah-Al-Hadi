package com.ryen.sunnah_alhadi.presentation.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.ExtraContentType
import com.ryen.sunnah_alhadi.presentation.util.extraContentColors

@Composable
fun ECIconBox(
    iconColor: Color,
    iconBackground: Color,
    borderColor: Color,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.5.dp, borderColor),
        contentColor = iconColor,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(iconBackground)
            .size(32.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun ECIconPrev() {
    val colors = MaterialTheme.colorScheme.extraContentColors(ExtraContentType.BENEFIT)
    ECIconBox(
        iconRes = R.drawable.ec_benefit,
        iconColor = colors.iconColor,
        iconBackground = colors.iconBackground,
        borderColor = colors.borderColor
    )
}