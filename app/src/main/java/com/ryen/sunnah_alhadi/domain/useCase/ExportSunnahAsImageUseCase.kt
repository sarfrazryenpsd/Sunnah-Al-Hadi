package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah

class ExportSunnahAsImageUseCase(
    // Add image generation dependencies
) : UseCase<ExportParams, ExportResult>() {

    override suspend fun execute(parameters: ExportParams): ExportResult {
        // Implementation for generating JPG from Sunnah content
        // This would involve creating a bitmap/canvas with the content
        return ExportResult.Success("path/to/exported/image.jpg")
    }
}

data class ExportParams(
    val sunnah: Sunnah,
    val style: ExportStyle,
    val includeWatermark: Boolean = true
)

enum class ExportStyle { LIGHT, DARK }

sealed class ExportResult {
    data class Success(val filePath: String) : ExportResult()
    data class Error(val message: String) : ExportResult()
}