package com.ryen.sunnah_alhadi.domain.useCase

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.SunnahFullCard
import com.ryen.sunnah_alhadi.util.ImageExportUtil
import com.ryen.sunnah_alhadi.util.Result

class ExportSunnahAsImageUseCase(
    private val context: Context
) : UseCase<ExportParams, ExportResult>() {

    override suspend fun execute(parameters: ExportParams): ExportResult {
        return try {
            // Create the composable content with 9:16 aspect ratio
            val width = 1080
            val height = 1920 // 9:16 aspect ratio

            val bitmapResult = ImageExportUtil.captureComposableAsBitmap(
                context = context,
                composable = {
                    SunnahCardWithContainer(
                        sunnah = parameters.sunnah,
                        backgroundColor = Color.Black
                    )
                }
            )

            when (bitmapResult) {
                is Result.Error -> {
                    return ExportResult.Error(bitmapResult.message ?: "Failed to generate image")
                }

                is Result.Success -> {
                    // Remove spaces from the title for filename
                    val filename = parameters.sunnah.title.replace(" ", "")

                    when (parameters.action) {
                        ExportAction.SAVE -> {
                            val uriResult = ImageExportUtil.saveBitmapLocally(
                                context,
                                bitmapResult.data,
                                filename
                            )
                            when (uriResult) {
                                is Result.Success -> ExportResult.Success(uriResult.data)
                                is Result.Error -> ExportResult.Error(
                                    uriResult.message ?: "Failed to save image"
                                )
                            }
                        }

                        ExportAction.SHARE -> {
                            val uriResult = ImageExportUtil.saveBitmapLocally(
                                context,
                                bitmapResult.data,
                                filename
                            )
                            when (uriResult) {
                                is Result.Success -> {
                                    ImageExportUtil.shareBitmap(context, uriResult.data)
                                    ExportResult.Success(null) // No URI needed for share action
                                }

                                is Result.Error -> ExportResult.Error(
                                    uriResult.message ?: "Failed to create shareable image"
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            ExportResult.Error(e.message ?: "Unknown error occurred")
        }
    }
}

@Composable
fun SunnahCardWithContainer(
    sunnah: Sunnah,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        SunnahFullCard(
            sunnah = sunnah,
            modifier = Modifier.fillMaxSize()
        )
    }
}

data class ExportParams(
    val sunnah: Sunnah,
    val action: ExportAction
)

enum class ExportAction {
    SAVE,
    SHARE
}

sealed class ExportResult {
    data class Success(val uri: android.net.Uri?) : ExportResult()
    data class Error(val message: String) : ExportResult()
}