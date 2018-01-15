package org.tag_them.metoothanks

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.TRANSPARENT
import android.support.design.widget.Snackbar
import android.text.TextPaint
import android.util.TypedValue
import android.view.WindowManager
import android.widget.EditText
import org.jetbrains.anko.*
import org.tag_them.metoothanks.activities.Edit


fun fetchColor(ctx: Context, id: Int): Int {
        val typedValue = TypedValue()
        ctx.theme.resolveAttribute(id, typedValue, true)
        return typedValue.data
}

fun fitBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        var resultBitmap: Bitmap = bitmap
        when {
                resultBitmap.width > width   -> {
                        val ratio: Float = height.toFloat() / resultBitmap.width.toFloat()
                        resultBitmap = Bitmap.createScaledBitmap(
                                bitmap, width, (resultBitmap.height * ratio).toInt(), false
                        )
                }
                
                resultBitmap.height > height -> {
                        val ratio: Float = height.toFloat() / resultBitmap.height.toFloat()
                        resultBitmap = Bitmap.createScaledBitmap(
                                bitmap, (resultBitmap.width * ratio).toInt(), height, false
                        )
                }
        }
        
        return resultBitmap
}

fun calculateTextWidth(text: String, textSize: Float): Int {
        val textPaint = TextPaint()
        textPaint.textSize = textSize
        
        val maxLine = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                              .map { textPaint.measureText(it).toInt() }
                              .max()
                      ?: 0
        
        return maxLine + textPaint.measureText(" ").toInt()
}

fun calculateTextHeight(text: String, textSize: Float): Int {
        val lines = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
        return (lines * textSize).toInt()
}

fun Context.openTextInputDialog(initialText: String = "", action: (text_gotten: String) -> Unit) {
        var textbox: EditText? = null
//        (this.alert {
//                customView {
//                        linearLayout {
//                                padding = dip(20)
//
//                                textbox = editText(initialText).apply {
//                                        setSingleLine(false)
//                                        setBackgroundColor(TRANSPARENT)
//                                        bottomPadding = dip(70)
//                                }.lparams(width = org.jetbrains.anko.matchParent)
//                        }
//                }
//
//                positiveButton(R.string.ok_hand_sign_emoji) {
//                        action(textbox?.text.toString())
//                }
//        }.build() as AlertDialog).apply {
//                // makes the keyboard pop when the dialog is shown
//                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
//        }.show()
}

fun Edit.snackbar(message: String) =
        Snackbar.make(findViewById(R.id.canvas_view), message, Snackbar.LENGTH_LONG).setAction(
                "Action", null
        ).show()

fun <T> ArrayList<T>.swapWithNextItem(item: T) {
        swap(item, 1)
}

fun <T> ArrayList<T>.swapWithPrevItem(item: T) {
        swap(item, -1)
}

private fun <T> ArrayList<T>.swap(item: T, relativePosition: Int) {
        val index = indexOf(item)
        if (index + relativePosition in 0 until size)
                set(index, set(index + relativePosition, item))
}

/**
 * returns the same array without its first element
 */
fun <T> List<T>.rest(): List<T> = this.subList(1, size)
