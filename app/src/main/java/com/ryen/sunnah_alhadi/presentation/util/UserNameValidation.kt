package com.ryen.sunnah_alhadi.presentation.util

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

fun validateUsername(username: String): ValidationResult {
    return when {
        username.isBlank() -> ValidationResult(
            isValid = false,
            errorMessage = "Name cannot be empty"
        )
        username.length < 2 -> ValidationResult(
            isValid = false,
            errorMessage = "Name must be at least 2 characters"
        )
        username.length > 20 -> ValidationResult(
            isValid = false,
            errorMessage = "Name must be 20 characters or less"
        )
        username.contains(" ") -> ValidationResult(
            isValid = false,
            errorMessage = "Spaces are not allowed"
        )
        !username.matches(Regex("^[a-zA-Z]+$")) -> ValidationResult(
            isValid = false,
            errorMessage = "Only letters are allowed"
        )
        else -> ValidationResult(isValid = true)
    }
}

fun getUsernameCharacterCount(username: String): String {
    val remaining = 20 - username.length
    return "${username.length}/20"
}

fun getUsernameHelperText(username: String, validationResult: ValidationResult): String? {
    return if (validationResult.isValid && username.isNotEmpty()) {
        null // No helper text when valid
    } else if (username.isEmpty()) {
        "Enter your preferred name for the app"
    } else {
        validationResult.errorMessage
    }
}