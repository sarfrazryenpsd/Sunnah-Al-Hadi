package com.ryen.sunnah_alhadi.domain.model

enum class NotificationTime(val hour: Int, val displayName: String) {
    MORNING(8, "Morning (8:00 AM)"),
    EVENING(18, "Evening (6:00 PM)"),
    NIGHT(21, "Night (9:00 PM)")
}