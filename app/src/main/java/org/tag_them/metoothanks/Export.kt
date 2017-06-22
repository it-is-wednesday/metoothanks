package org.tag_them.metoothanks

import android.os.Environment
import android.media.MediaScannerConnection
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.*

fun CanvasView.exportToCanvas(canvas: Canvas) {
	selectNone()
	canvas.drawColor(Color.WHITE)
	draw(canvas)
}

fun writeToSD(canvasview: CanvasView) {
	val root = android.os.Environment.getExternalStorageDirectory()
	
	// setting the filename to the time of creation
	val sdf = DateFormat.getDateTimeInstance()
	val currentDate = Date()
	val filename = sdf.format(currentDate) + ".jpeg"
	println("ahh")
	
	val dir = File(root.absolutePath + "/metoothanks")
	dir.mkdirs()
	val file = File(dir, filename)
	
	System.out.println(file.exists())
	val fos = FileOutputStream(file)
	
	// draw current canvas state to the file
	val bitmap = Bitmap.createBitmap(canvasview.width, canvasview.height, Bitmap.Config.ARGB_8888)
	val canvas = Canvas(bitmap)
	canvasview.exportToCanvas(canvas)
	println("shit")
	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
	fos.close()
	
	// add new image file to gallery
	MediaScannerConnection.scanFile(canvasview.context, arrayOf<String>(file.toString()), null) { path, uri ->
		Log.i("ExternalStorage", "Scanned $path:")
		Log.i("ExternalStorage", "-> uri=" + uri)
	}
}

fun checkExternalMedia(): Boolean {
	val mExternalStorageAvailable: Boolean
	val mExternalStorageWritable: Boolean
	val state = Environment.getExternalStorageState()
	var message = ""
	
	if (Environment.MEDIA_MOUNTED.equals(state)) {
		// Can read and write the media
		mExternalStorageWritable = true
		mExternalStorageAvailable = mExternalStorageWritable
	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		// Can only read the media
		mExternalStorageAvailable = true
		mExternalStorageWritable = false
		message = "External storage is available but not writeable :("
	} else {
		// Can't read or write
		mExternalStorageWritable = false
		mExternalStorageAvailable = mExternalStorageWritable
		message = "External storage isn't available :("
	}
	
	return mExternalStorageAvailable && mExternalStorageWritable
}