package com.ryen.sunnah_alhadi.ui.theme

import androidx.compose.ui.graphics.Color

object SunnahColors {
    // Light Theme Colors - Inspired by the UI mockup's soft pastels and neutrals for a calm, inviting feel.
    // Overall: Beige background for warmth, green primary for spiritual growth (Islamic association), gold secondary for enlightenment.
    // Pastel accents (blues, yellows) for bubbles and categories to match the mockup's playful emotion circles.

    val LightPrimary = Color(0xFF000000) // Primary text/icons — solid black    val LightOnPrimary = Color(0xFFFFFFFF) // Use for: Text or icons on primary color backgrounds.
    val LightOnPrimary = Color(0xFFFFFFFF) // For inverted situations (e.g., black button with white text)
    val LightPrimaryContainer = Color(0xFFFFFFFF) // For inverted situations (e.g., black button with white text)
    val LightOnPrimaryContainer = Color(0xFF1B5E20) // Use for: Text on primary containers for contrast.
    val LightSecondary = Color(0xFFF9A825) // Use for: Accents like highlights, gold outlines on logo, or secondary buttons (e.g., enlightenment-themed elements).
    val LightOnSecondary = Color(0xFF000000) // Use for: Text or icons on secondary color backgrounds.
    val LightSecondaryContainer = Color(0xFFFFF8E1) // Use for: Subtle backgrounds for secondary elements like stats or illustrations.
    val LightOnSecondaryContainer = Color(0xFF5D4037) // Use for: Text on secondary containers.
    val LightBackground = Color(0xFFFAF7F4) // Off-white / warm cream — main app background
    val LightOnBackground = Color(0xFF1C1C1C) // Dark charcoal text for comfortable reading (not 100% black)
    val LightSurface = Color(0xFFFFFFFF) // Card backgrounds
    val LightOnSurface = Color(0xFF1C1C1C) // Text/icons on cards
    val LightSurfaceVariant = Color(0xFFF0ECE9) // Slightly tinted surface for variant sections
    val LightOnSurfaceVariant = Color(0xFF4F4F4F) // Use for: Text on surface variants.
    val LightOutline = Color(0xFFDAD4CF) // Borders/dividers
    val LightOutlineVariant = Color(0xFFEDE8E4) // Use for: Lighter outlines in low-contrast areas.
    val LightScrim = Color(0x99000000) // Use for: Overlays like modals or loading screens.
    val LightInverseSurface = Color(0xFF121212) // Use for: Inverted surfaces in special modes (e.g., dark accents in light theme).
    val LightInverseOnSurface = Color(0xFFE0E0E0) // Use for: Text on inverse surfaces.
    val LightInversePrimary = Color(0xFF81C784) // Use for: Inverted primary accents.
    val LightSurfaceTint = Color(0xFF4CAF50) // Use for: Tints on surfaces for elevation effects.
    val LightTertiary = Color(0xFF81D4FA) // Added: Use for: Calm/reflective bubbles (e.g., Sadness or peace-related Sunnah like Patience).
    val LightError = Color(0xFFE2A5A5)
    val LightWarning = Color(0xFFF5D6A3) // Soft muted yellow-orange for caution

    // Dark Theme Colors - High-contrast version for low-light use, emphasizing the white-on-black logo for a gorgeous, reverent vibe.
    // Overall: Deep black background for focus, muted greens/golds to maintain spirituality without harshness.

    val DarkPrimary = Color(0xFFEAE7E4) // Off-white text/icons — not pure white
    val DarkOnPrimary = Color(0xFF1C1C1C) // For dark-on-light usage in reversed UI elements
    val DarkPrimaryContainer = Color(0xFF1B5E20) // Use for: Darker containers for primary sections.
    val DarkOnPrimaryContainer = Color(0xFFE8F5E9) // Use for: Text on primary containers.
    val DarkSecondary = Color(0xFFFFECB3) // Use for: Muted gold accents, logo highlights in dark mode.
    val DarkOnSecondary = Color(0xFF000000) // Use for: Text on secondary backgrounds.
    val DarkSecondaryContainer = Color(0xFF5D4037) // Use for: Subtle dark backgrounds for secondary elements.
    val DarkOnSecondaryContainer = Color(0xFFFFF8E1) // Use for: Text on secondary containers.
    val DarkBackground = Color(0xFF1F1F1F) // Soft warm charcoal — avoids harsh black
    val DarkOnBackground = Color(0xFFEAE7E4) // Off-white for main text
    val DarkSurface = Color(0xFF2A2A2A) // Card backgrounds in dark mode
    val DarkOnSurface = Color(0xFFEAE7E4) // Use for: Text on surfaces.
    val DarkSurfaceVariant = Color(0xFF3A3A3A) // Slightly lighter variant
    val DarkOnSurfaceVariant = Color(0xFFCFCAC6) // Use for: Text on surface variants.
    val DarkOutline = Color(0xFF5E5E5E)
    val DarkOutlineVariant = Color(0xFF3A3A3A) // Use for: Lighter dark outlines.
    val DarkScrim = Color(0x99000000) // Use for: Overlays.
    val DarkInverseSurface = Color(0xFFE0E0E0) // Use for: Inverted surfaces.
    val DarkInverseOnSurface = Color(0xFF121212) // Use for: Text on inverse surfaces.
    val DarkInversePrimary = Color(0xFF4CAF50) // Use for: Inverted primary.
    val DarkSurfaceTint = Color(0xFF81C784) // Use for: Tints in dark mode.
    val DarkTertiary = Color(0xFF4FC3F7) // Added: Use for: Muted blue accents in dark mode (e.g., calm bubbles).
    val DarkError = Color(0xFFB37B7B) // Added: Use for: Muted red for errors or negative elements.
    val DarkWarning = Color(0xFFC8A770) // Added: Use for: Muted yellow for warnings or neutral accents.
}