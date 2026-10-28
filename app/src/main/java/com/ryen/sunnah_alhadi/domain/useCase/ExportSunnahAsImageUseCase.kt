package com.ryen.sunnah_alhadi.domain.useCase

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.SunnahFullCard
import com.ryen.sunnah_alhadi.util.ImageExportUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val EXPORT_WIDTH_PX = 1080
private const val EXPORT_MIN_HEIGHT_PX = 1920 // 9:16; long cards grow taller instead of being cut off

class ExportSunnahAsImageUseCase : UseCase<ExportParams, ExportResult>() {

    override suspend fun execute(parameters: ExportParams): ExportResult = try {
        val context = parameters.context
        val sunnah = parameters.sunnah
        val bitmap = ImageExportUtil.captureComposableAsBitmap(
            context, parameters.parentComposition, EXPORT_WIDTH_PX
        ) {
            SunnahExportCard(sunnah)
        }
        // Keeps letters and digits (any script); drops spaces, quotes and path separators.
        val filename = sunnah.title.filter { it.isLetterOrDigit() }.ifEmpty { sunnah.id }
        when (parameters.action) {
            ExportAction.SAVE -> {
                val uri = withContext(Dispatchers.IO) {
                    ImageExportUtil.saveBitmapToGallery(context, bitmap, filename)
                }
                ExportResult.Success(uri)
            }

            ExportAction.SHARE -> {
                val uri = withContext(Dispatchers.IO) {
                    ImageExportUtil.saveBitmapLocally(context, bitmap, filename)
                }
                ImageExportUtil.shareBitmap(context, uri)
                ExportResult.Success(null) // No URI needed for share action
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ExportResult.Error(e.message ?: "Failed to export image")
    }
}

/**
 * The exported image: the card on the same surface colour the in-app card uses, at least 9:16,
 * with an app footer.
 * Density is fixed (1080 px = 400 dp, no font scaling) so the image looks the same from every device.
 */
@Composable
fun SunnahExportCard(sunnah: Sunnah) {
    CompositionLocalProvider(LocalDensity provides Density(EXPORT_WIDTH_PX / 400f, fontScale = 1f)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = with(LocalDensity.current) { EXPORT_MIN_HEIGHT_PX.toDp() })
                .background(MaterialTheme.colorScheme.surface)
        ) {
            SunnahFullCard(
                sunnah = sunnah,
                scrollable = false,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 24.dp, bottom = 88.dp) // bottom space is reserved for the footer
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.sunnahlogo),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary // the app's text colour on cards
                )
            }
        }
    }
}

data class ExportParams(
    /** Activity context, needed to render the composable off-screen. Not retained. */
    val context: Context,
    /** The caller's composition, so the image uses the same theme the user sees in the app. */
    val parentComposition: CompositionContext,
    val sunnah: Sunnah,
    val action: ExportAction
)

enum class ExportAction {
    SAVE,
    SHARE
}

sealed class ExportResult {
    data class Success(val uri: Uri?) : ExportResult()
    data class Error(val message: String) : ExportResult()
}
