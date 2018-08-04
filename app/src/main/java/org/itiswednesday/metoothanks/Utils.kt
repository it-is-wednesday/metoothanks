package org.itiswednesday.metoothanks

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.support.design.widget.Snackbar
import android.text.TextPaint
import android.util.TypedValue
import android.view.WindowManager
import android.widget.EditText
import org.itiswednesday.metoothanks.activities.Edit
import kotlin.math.sqrt


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

    return textPaint.measureText(text).toInt()
}

fun calculateTextHeight(text: String, textSize: Float): Int {
    val lines = text.split("\n").size
    return (lines * textSize).toInt()
}

fun Context.openTextInputDialog(initialText: String = "", action: (text_gotten: String) -> Unit) {
    val input = EditText(this).apply {
        setText(initialText)
        setSingleLine(false)
        setBackgroundColor(Color.TRANSPARENT)
        setPadding(20, 20, 20, 20)
    }

    val builder = AlertDialog.Builder(this)
            .setView(input)
            .setPositiveButton(R.string.ok_hand_sign_emoji) { _, _ ->
                action(input.text.toString())
            }.create()

    // makes the keyboard pop when the dialog is shown
    builder.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    builder.show()
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

class Line(private val p1: Point, private val p2: Point) {
    constructor(pp: PointPair) : this(pp.first, pp.second)

    val length: Float
        get() = sqrt(square(p1.y - p2.y) + square(p1.x - p2.y))
}

fun square(num: Int): Float = (num * num).toFloat()

data class PointPair(var first: Point, var second: Point) {
    constructor(init: (Int) -> Point) : this(init(0), init(1))

    operator fun get(index: Int) = if (index == 0) first else second

    // get the point closer to (0, 0)
    val closer
        get() = if (first.x + first.y > second.x + second.y) second else first

    // get the point further to (0, 0)
    val further
        get() = if (first.x + first.y > second.x + second.y) first else second
}
