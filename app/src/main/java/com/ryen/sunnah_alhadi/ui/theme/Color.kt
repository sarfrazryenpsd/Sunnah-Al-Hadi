package com.ryen.sunnah_alhadi.ui.theme

import androidx.compose.ui.graphics.Color

object SunnahColors {
    // =============================================================================
    // LIGHT THEME COLORS - Warm, peaceful, and inviting
    // =============================================================================

    // Primary Colors - Main text and interactive elements
    val LightPrimary = Color(0xFF282626) // Primary text/buttons - dark charcoal
    val LightOnPrimary = Color(0xFFFFFFFF) // White text on primary buttons
    val LightPrimaryContainer = Color(0xFFFFFFFF) // White containers
    val LightOnPrimaryContainer = Color(0xFF282626) // Dark text on white containers

    // Secondary Colors - Glow effects and accent elements
    val LightSecondary = Color(0xFFF4C1A7) // Warm glow color for Home Sunnah
    val LightOnSecondary = Color(0xFF282626) // Dark text on glow backgrounds
    val LightSecondaryContainer = Color(0xFFFFFFFF) // Very light peachy container
    val LightOnSecondaryContainer = Color(0xFF5D4037) // Brown text on secondary containers

    // Tertiary Colors - Additional accent system
    val LightTertiary = Color(0xFF282626) // Search bar color - muted peach
    val LightOnTertiary = Color(0xFF282626) // Dark text on tertiary backgrounds
    val LightTertiaryContainer = Color(0xFFF4C1A7) // Light tertiary container
    val LightOnTertiaryContainer = Color(0xFF4A3A35) // Text on tertiary containers

    // Background & Surface Colors
    val LightBackground = Color(0xFFF9F7F4) // Main app background - warm cream
    val LightOnBackground = Color(0xFF282626) // Primary text color
    val LightSurface = Color(0xFFFFFEFC) // Sunnah card backgrounds - off-white
    val LightOnSurface = Color(0xFF282626) // Text on cards
    val LightSurfaceVariant = Color(0xFFF9F8F4) // Depth/variant surfaces
    val LightOnSurfaceVariant = Color(0xFFE0E0E0) // Muted text on variants

    // Interactive States
    val LightSurfaceContainer = Color(0xFFE0E0E0) // Unselected tabs
    val LightSurfaceContainerHigh = Color(0xFFF5F5F5) // Elevated surfaces
    val LightSurfaceContainerHighest = Color(0xFFEFEFEF) // Highest elevation

    // Outline & Divider Colors
    val LightOutline = Color(0xFFE5DDD8) // Subtle borders
    val LightOutlineVariant = Color(0xFFF0ECE9) // Very light dividers

    // System Colors
    val LightScrim = Color(0x80000000) // Modal overlays
    val LightInverseSurface = Color(0xFF2F2F2F) // Dark tooltips
    val LightInverseOnSurface = Color(0xFFF5F5F5) // Light text on dark tooltips
    val LightInversePrimary = Color(0xFFF4C1A7) // Glow color for inverse elements
    val LightSurfaceTint = Color(0xFFF4C1A7) // Surface elevation tint

    // Status Colors
    val LightError = Color(0xFFD32F2F) // Clear red for errors
    val LightOnError = Color(0xFFFFFFFF) // White text on error
    val LightErrorContainer = Color(0xFFFFEBEB) // Light error background
    val LightOnErrorContainer = Color(0xFF5F1919) // Dark red text on error containers


    // =============================================================================
    // DARK THEME COLORS - Elegant dark with Islamic green accents
    // =============================================================================

    // Primary Colors
    val DarkPrimary = Color(0xFF282626) // Off-white text - easy on eyes
    val DarkOnPrimary = Color(0xFF1A1A1A) // Dark text for contrast
    val DarkPrimaryContainer = Color(0xFFFFFFFF) // Dark green container
    val DarkOnPrimaryContainer = Color(0xFF2C3D35) // Light text on dark containers

    // Secondary Colors - Bright Islamic green glow
    val DarkSecondary = Color(0xFFCDF0A0) // Bright lime green glow
    val DarkOnSecondary = Color(0xFF1A2E1A) // Very dark green text
    val DarkSecondaryContainer = Color(0xFF2C3D35) // Dark green container
    val DarkOnSecondaryContainer = Color(0xFFE8F5E9) // Light text on dark green

    // Tertiary Colors
    val DarkTertiary = Color(0xFFF2F2F2) // Search bar - very light green
    val DarkOnTertiary = Color(0xFF163125) // Dark green text
    val DarkTertiaryContainer = Color(0xFFDDFFDA) // Dark container
    val DarkOnTertiaryContainer = Color(0xFFDDFFDA) // Light green text

    // Background & Surface Colors
    val DarkBackground = Color(0xFF19241F) // Main dark background - deep forest
    val DarkOnBackground = Color(0xFFF9F7F4) // Off-white text
    val DarkSurface = Color(0xFF27272B) // Sunnah card backgrounds - dark gray
    val DarkOnSurface = Color(0xFFF2F2F2) // Light text on cards
    val DarkSurfaceVariant = Color(0xFF0A1711) // Depth surfaces - very dark
    val DarkOnSurfaceVariant = Color(0xFF0A1711) // Muted green-gray text

    // Interactive States
    val DarkSurfaceContainer = Color(0xFFE0E0E0) // Unselected tabs (keeping light for contrast)
    val DarkSurfaceContainerHigh = Color(0xFF323232) // High elevation
    val DarkSurfaceContainerHighest = Color(0xFF3A3A3A) // Highest elevation

    // Outline & Divider Colors
    val DarkOutline = Color(0xFF4A5A4F) // Subtle green-tinted borders
    val DarkOutlineVariant = Color(0xFF2C3D35) // Very subtle dividers

    // System Colors
    val DarkScrim = Color(0x80000000) // Modal overlays
    val DarkInverseSurface = Color(0xFFF5F5F5) // Light tooltips in dark mode
    val DarkInverseOnSurface = Color(0xFF1A1A1A) // Dark text on light tooltips
    val DarkInversePrimary = Color(0xFF2C3D35) // Dark primary for inverse
    val DarkSurfaceTint = Color(0xFFCDF0A0) // Green tint for elevation

    // Status Colors - Adapted for dark theme
    val DarkError = Color(0xFFFFB4AB) // Softer red for dark backgrounds
    val DarkOnError = Color(0xFF690005) // Dark red text
    val DarkErrorContainer = Color(0xFF93000A) // Dark red container
    val DarkOnErrorContainer = Color(0xFFFFDAD6) // Light text on error

}