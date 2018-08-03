package org.tag_them.metoothanks

import android.os.Environment
import android.media.MediaScannerConnection
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.*

fun CanvasView.exportToCanvas(canvas: Canvas) {
    selectNone()
    canvas.drawColor(Color.WHITE)
    draw(canvas)
}

fun writeToStorage(canvasview: CanvasView, writeTemporarily: Boolean = false): Uri {
    val root = android.os.Environment.getExternalStorageDirectory()

    // setting the filename to the time of creation
    val sdf = DateFormat.getDateTimeInstance()
    val currentDate = Date()
    val filename = sdf.format(currentDate) + ".jpeg"

    val file = File(
            if (writeTemporarily) canvasview.context.externalCacheDir
            else File(root.absolutePath + "/metoothanks").apply { mkdirs() },
            filename)

    FileOutputStream(file).use {
        // draw current canvas state to the file
        val bitmap = Bitmap.createBitmap(canvasview.width, canvasview.height,
                                         Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvasview.exportToCanvas(canvas)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        it.close()
    }

    // add new image file to gallery
    if (!writeTemporarily)
        MediaScannerConnection.scanFile(canvasview.context, arrayOf<String>(file.toString()),
                                        null) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=" + uri)
        }

    return Uri.fromFile(file)
}

fun checkExternalMedia(): Boolean {
    val mExternalStorageAvailable: Boolean
    val mExternalStorageWritable: Boolean
    val state = Environment.getExternalStorageState()

    if (Environment.MEDIA_MOUNTED == state) {
        // Can read and write the media
        mExternalStorageWritable = true
        mExternalStorageAvailable = mExternalStorageWritable
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY == state) {
        // Can only read the media
        mExternalStorageAvailable = true
        mExternalStorageWritable = false
    } else {
        // Can't read or write
        mExternalStorageWritable = false
        mExternalStorageAvailable = mExternalStorageWritable
    }

    return mExternalStorageAvailable && mExternalStorageWritable
}