package com.example.kotlinjc_camera

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.serialization.Serializable
import java.io.OutputStream

@Serializable
object ImageS

@Composable
fun ImageScreen(
    innerPadding: PaddingValues,
    image: Bitmap? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                if (image != null) {
                    saveToGallery(context, image)
                }
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        if (image != null) {
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = "Captured image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Back"
                    )
                }

                Button(onClick = {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        saveToGallery(context, image)
                    }
                }) {
                    Text("Save")
                }
            }
        } else {
            Text("No image")
        }
    }
}

private fun saveToGallery(context: Context, capturePhotoBitmap: Bitmap) {
    val resolver: ContentResolver = context.applicationContext.contentResolver

    val imageCollection: Uri = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    // Publish a new image.
    val nowTimestamp: Long = System.currentTimeMillis()
    val imageContentValues: ContentValues = ContentValues().apply {

        put(MediaStore.Images.Media.DISPLAY_NAME, "Your image name" + ".jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.DATE_TAKEN, nowTimestamp)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/YourAppNameOrAnyOtherSubFolderName")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            put(MediaStore.Images.Media.DATE_TAKEN, nowTimestamp)
            put(MediaStore.Images.Media.DATE_ADDED, nowTimestamp)
            put(MediaStore.Images.Media.DATE_MODIFIED, nowTimestamp)
            put(MediaStore.Images.Media.AUTHOR, "Your Name")
            put(MediaStore.Images.Media.DESCRIPTION, "Your description")
        }
    }

    val imageMediaStoreUri: Uri? = try {
        resolver.insert(imageCollection, imageContentValues)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    // Write the image data to the new Uri.
    imageMediaStoreUri?.let { uri ->
        runCatching {
            resolver.openOutputStream(uri).use { outputStream: OutputStream? ->
                checkNotNull(outputStream) { "Couldn't create file for gallery, MediaStore output stream is null" }
                capturePhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageContentValues.clear()
                imageContentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, imageContentValues, null, null)
            }
        }.onFailure { exception: Throwable ->
            exception.message?.let(::println)
            resolver.delete(uri, null, null)
        }
    }
}

@PreviewScreenSizes
@Composable
fun ImageScreenPreview() {
    ImageScreen(innerPadding = PaddingValues()) {}
}
