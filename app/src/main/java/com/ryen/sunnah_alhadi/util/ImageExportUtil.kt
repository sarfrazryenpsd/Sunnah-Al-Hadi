package com.ryen.sunnah_alhadi.util

import android.app.Activity
import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Cards are always PNG: flat colours and sharp text stay crisp and the files are small (~50-150 KB),
 * where JPEG would blur the letters for little size gain. All functions throw on failure.
 */
object ImageExportUtil {

    /**
     * Renders [composable] off-screen at [width] px wide; the height is whatever the content needs.
     * A ComposeView only composes once attached to a window, so [context] must be an Activity
     * (or wrap one); the view is attached invisibly to its content root for one frame.
     * [parent] links the off-screen composition to the caller's, so it inherits the app's theme
     * and other CompositionLocals, the same way Dialog and Popup do.
     */
    suspend fun captureComposableAsBitmap(
        context: Context,
        parent: CompositionContext,
        width: Int,
        composable: @Composable () -> Unit
    ): Bitmap = withContext(Dispatchers.Main.immediate) {
        val activity = checkNotNull(context.findActivity()) { "Cannot render card outside an Activity" }
        val root = activity.findViewById<ViewGroup>(android.R.id.content)
        val view = ComposeView(activity).apply {
            visibility = View.INVISIBLE
            setParentCompositionContext(parent)
            setContent(composable)
        }
        try {
            root.addView(view, FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.WRAP_CONTENT))
            awaitFrame() // let composition and async resources settle
            // awaitFrame resumes before that frame's layout pass, so lay out explicitly before drawing.
            view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, width, view.measuredHeight)
            view.drawToBitmap()
        } finally {
            root.removeView(view)
        }
    }

    /** Writes the PNG to the app cache and returns a FileProvider uri (for sharing). */
    fun saveBitmapLocally(context: Context, bitmap: Bitmap, filename: String): Uri {
        val file = File(File(context.cacheDir, "images").apply { mkdirs() }, "$filename.png")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    /**
     * Saves the PNG into the device gallery (Pictures/SunnahAlHadi) via MediaStore.
     * On Android 8-9 the caller must hold WRITE_EXTERNAL_STORAGE.
     */
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, filename: String): Uri {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/SunnahAlHadi")
                put(MediaStore.Images.Media.IS_PENDING, 1) // hide from the gallery until fully written
            }
        }
        val uri = checkNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)) {
            "MediaStore insert returned null"
        }
        try {
            checkNotNull(resolver.openOutputStream(uri)) { "Cannot open gallery file" }
                .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null)
            }
        } catch (e: Exception) {
            resolver.delete(uri, null, null) // don't leave an empty or half-written image behind
            throw e
        }
        return uri
    }

    fun shareBitmap(context: Context, uri: Uri) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            // ClipData carries the read grant to the share sheet itself, so it can show a preview.
            clipData = ClipData.newRawUri(null, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(send, "Share Sunnah Card").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
