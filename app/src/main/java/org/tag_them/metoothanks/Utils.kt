package org.tag_them.metoothanks

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.TRANSPARENT
import android.support.design.widget.Snackbar
import android.text.TextPaint
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import org.jetbrains.anko.*


fun fetchColor(ctx: Context, id: Int): Int {
	val typedValue = TypedValue()
	ctx.theme.resolveAttribute(id, typedValue, true)
	return typedValue.data
}

fun fitBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
	var resultBitmap: Bitmap = bitmap
	
	if (resultBitmap.width > width) {
		val ratio: Float = height.toFloat() / resultBitmap.width.toFloat()
		println(ratio)
		resultBitmap = Bitmap.createScaledBitmap(bitmap, width, (resultBitmap.height * ratio).toInt(), false)
	}
	
	if (resultBitmap.height > height) {
		val ratio: Float = height.toFloat() / resultBitmap.height.toFloat()
		println("$ratio $height ${resultBitmap.height}")
		resultBitmap = Bitmap.createScaledBitmap(bitmap, (resultBitmap.width * ratio).toInt(), height, false)
	}
	
	return resultBitmap
}

fun calculateTextWidth(text: String, textSize: Float): Int {
	val textPaint = TextPaint()
	textPaint.textSize = textSize
	
	var maxLine = 0
	for (s in text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
		val currentLine = textPaint.measureText(s).toInt()
		if (currentLine > maxLine)
			maxLine = currentLine
	}
	
	return maxLine + textPaint.measureText(" ").toInt()
}

fun calculateTextHeight(text: String, textSize: Float): Int {
	val lines = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
	return (lines * textSize).toInt()
}

fun Context.openTextInputDialog(initialText: String = "", action: (text_gotten: String) -> Unit) {
	var textbox: EditText? = null
	(this.alert {
		customView {
			linearLayout {
				padding = dip(20)
				
				textbox = editText(initialText).apply {
					setSingleLine(false)
					setBackgroundColor(TRANSPARENT)
					bottomPadding = dip(70)
				}.lparams(width = org.jetbrains.anko.matchParent)
			}
		}
		
		positiveButton(R.string.ok_hand_sign_emoji) {
			action(textbox?.text.toString())
		}
	}.build() as AlertDialog).apply {
		// makes the keyboard pop when the dialog is shown
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
	}.show()
}

fun Draw.snackbar(message: String) =
		Snackbar.make(layout.canvas_view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show()

fun <T> ArrayList<T>.swapWithNextItem(item: T) {
	swap(item, 1)
}

fun <T> ArrayList<T>.swapWithPrevItem(item: T) {
	swap(item, -1)
}

private fun <T> ArrayList<T>.swap(item: T, relativePosition: Int) {
	try {
		val index = indexOf(item)
		set(index, set(index + relativePosition, item))
	} catch (e: Exception) {
		e.printStackTrace()
	}
}
