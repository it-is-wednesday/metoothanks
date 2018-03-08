package org.tag_them.metoothanks.items

import android.graphics.Canvas
import android.graphics.Point
import android.text.TextPaint
import android.view.MenuItem
import org.tag_them.metoothanks.*


val DEFAULT_TEXT_SIZE = 120f
val MIN_TEXT_SIZE = 40
val TEXT_SIZE_CHANGE_STEP = 20
val WIDTH_FIX_INTERVAL = 10L

class Text(text: String, canvasWidth: Int, hostView: CanvasView) :
        Item(
                canvas = hostView,
                width = minOf(canvasWidth, calculateTextWidth(text, DEFAULT_TEXT_SIZE)),
                height = calculateTextHeight(text, applyFontHeightFix(DEFAULT_TEXT_SIZE))
        ) {

        var text: String = text
                private set(value) {
                        field = value
                        matchBoundariesWithText()
                }

        private val textPaint: TextPaint = TextPaint().apply {
                isAntiAlias = true
                textSize = DEFAULT_TEXT_SIZE
                color = 0xFF000000.toInt()
        }

        private enum class Alignment { LTR, RTL }

        private var alignment: Alignment = Alignment.LTR

        init {
                this.text = fitToWidth(canvasWidth, text, fontSize)
        }

        private var currentTextWidth = calculateTextWidth(text, fontSize)

        override fun handleMenuItemClick(item: MenuItem): Boolean {
                when (item.itemId) {
                        R.id.action_text_align_left  -> alignment = Alignment.LTR
                        R.id.action_text_align_right -> alignment = Alignment.RTL
                        R.id.action_item_edit        -> edit()
                        R.id.action_text_enlarge     -> increaseFontSize()
                        R.id.action_text_shrink      -> decreaseFontSize()
                        else                         -> return false
                }

                canvas.postInvalidate()

                return true
        }

        override val itemMenuID: Int = R.menu.item_text_menu

        override fun draw(canvas: Canvas) {
                val x = (if (alignment == Alignment.LTR) left else right).toFloat()
                val lines = text.split("\n")
                lines.indices.forEach {
                        canvas.drawText(lines[it], x, top + (it + 1) * applyFontHeightFix(fontSize), textPaint)
                }
        }

        override fun resize(pointers: PointPair, pointersGrip: PointPair,
                            pointersGripDistance: PointPair) {
                for (index in 0..1) {
                        if (pointersGrip[index].x in 0..(left + width / 2))
                                with(pointers[index].x - pointersGripDistance[index].x) {
                                        left = this
                                }
                        else
                                with(pointers[index].x + pointersGripDistance[index].x) {
                                        right = this
                                }

                        if (width < calculateTextWidth(text, fontSize)) {
                                text = fitToWidth(width, text, fontSize)
                                matchBoundariesWithText()
                        }
                }
        }

        fun edit() {
                canvas.context.openTextInputDialog(initialText = text) {
                        text = it
                        matchBoundariesWithText()
                }
        }

        private fun matchBoundariesWithText() {
                bottom = top + calculateTextHeight(text, applyFontHeightFix(fontSize))
                currentTextWidth = calculateTextWidth(text, fontSize)

                // comparing the current frame width to the new text's width
                if (width < currentTextWidth)
                        when (alignment) {
                                Alignment.LTR -> right = left + currentTextWidth
                                else          -> left = right - currentTextWidth
                        }
        }

        private fun changeFontSize(difference: Int) {
                textPaint.textSize += difference
                matchBoundariesWithText()
        }

        private fun increaseFontSize() {
                changeFontSize(TEXT_SIZE_CHANGE_STEP)
        }

        private fun decreaseFontSize() {
                if (fontSize > MIN_TEXT_SIZE + TEXT_SIZE_CHANGE_STEP)
                        changeFontSize(-TEXT_SIZE_CHANGE_STEP)
        }

        private val fontSize: Float
                get() = textPaint.textSize

        override fun toString(): String = text
}


/* helper functions */

/**
 * TextPaint.textSize is a bit larger than the actual
 * height of the font. This is a dirty hack around it.
 */
fun applyFontHeightFix(originalFontSize: Float): Float {
        val fixedFactor = 0.25f
        return originalFontSize * (1 - fixedFactor)
}

fun String.lastLine() = this.split("\n").last()

fun fitToWidth(width: Int, text: String, textSize: Float): String {
        tailrec fun fitToWidth1(width: Int, text: String, words: List<String>, textSize: Float): String =
                if (words.isEmpty()) text.trim()
                else {
                        val newWidth = calculateTextWidth(text.lastLine() + words.first(), textSize)
                        val newText = text + (if (newWidth > width) "\n" else " ") + words.first()
                        fitToWidth1(width, newText, words.rest(), textSize)
                }

        return fitToWidth1(width, "", text.split(" "), textSize)
}
