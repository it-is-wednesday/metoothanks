package org.tag_them.metoothanks.items

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.text.TextPaint
import android.view.MenuItem
import org.tag_them.metoothanks.*


val DEFAULT_TEXT_SIZE = 120f
val MIN_TEXT_SIZE = 40
val TEXT_SIZE_CHANGE_STEP = 20
val WIDTH_FIX_INTERVAL = 10L

class Text(var text: String, private val canvasWidth: Int, hostView: CanvasView) :
        Item(
                canvas = hostView,
                width = minOf(canvasWidth, calculateTextWidth(text, DEFAULT_TEXT_SIZE)),
                height = calculateTextHeight(text, DEFAULT_TEXT_SIZE)
        ) {
        
        private enum class Alignment { LTR, RTL }
        
        private var alignment: Alignment = Alignment.LTR
        private var originalText: String = text
        private var minWidth = calculateTextWidth(text, fontSize)
        
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
        
        private val DRAW_Y_GAP = 20
        
        override fun draw(canvas: Canvas) {
                // since canvas.drawText() doesn't draw multiline text properly,
                // we'll draw each line separately
                val strings = text.split(
                        "\n".toRegex()
                ).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in strings.indices) {
                        val xPosition = when (alignment) {
                                Alignment.LTR -> left.toFloat()
                                else          -> right.toFloat() - calculateTextWidth(
                                        strings[i], fontSize
                                )
                        }
                        // added 1 to i because for some reason canvas.drawText() treats the y value
                        // as the position of the bottom edge of the image rather than the top edge
                        canvas.drawText(
                                strings[i], xPosition,
                                top + (i + 1) * textPaint.textSize - DRAW_Y_GAP,
                                textPaint
                        )
                }
        }
        
        override fun resize(pointers: Array<Point>, pointersGrip: Array<Point>,
                            pointersGripDistance: Array<Point>) {
                for (index in pointers.indices) {
                        if (pointersGrip[index].x in 0..(left + width / 2))
                                with(pointers[index].x - pointersGripDistance[index].x) {
                                        if (this < right - minWidth)
                                                left = this
                                }
                        else
                                with(pointers[index].x + pointersGripDistance[index].x) {
                                        if (this > left + minWidth)
                                                right = this
                                }
                }
                
                text = fitToWidth(width, text, DEFAULT_TEXT_SIZE)
        }
        
        
        private val textPaint: TextPaint
                get() =
                        TextPaint().apply {
                                isAntiAlias = true
                                textSize = DEFAULT_TEXT_SIZE
                                color = 0xFF000000.toInt()
                        }
        
        /**
         * Adds or removes newlines to make the text fit nicely to the specified minWidth
         
         * @param width to fit into
         */
//                fun fitToWidth(width: Int) {
//                        val stringBuilder = StringBuilder()
//                        var widthSum = 0
//                        for (word in originalText.split(
//                                " ".toRegex()
//                        ).dropLastWhile { it.isEmpty() }.toTypedArray()) {
//                                if (word != "\n")
//                                        widthSum += textPaint.measureText(word + " ").toInt()
//                                if (widthSum > width + textPaint.measureText(" ")) {
//                                        stringBuilder.append("\n").append(word).append(" ")
//                                        widthSum = textPaint.measureText(word + " ").toInt()
//                                        continue
//                                }
//                                stringBuilder.append(word).append(" ")
//                        }
//
//                        text = stringBuilder.toString()
//                }
        
        // canvas.drawText draws the text a bit too low; this is used to counter it
        
        fun edit() {
                canvas.context.openTextInputDialog(originalText) {
                        originalText = it
//                        text = fitToWidth(
//                                this@Text.width,
//                                text,
//                                text.split(" "),
//                                DEFAULT_TEXT_SIZE
//                        )
                        text = fitToWidth(width, text, DEFAULT_TEXT_SIZE)
                        text = fitToWidth(width, text, DEFAULT_TEXT_SIZE)
                        minWidth = calculateTextWidth(text, fontSize)
                        left = 0; right = canvasWidth
                }
        }
        
        private fun increaseFontSize() {
                textPaint.textSize = textPaint.textSize + TEXT_SIZE_CHANGE_STEP
                bottom = calculateTextHeight(text, fontSize)
                minWidth = calculateTextWidth(text, fontSize)
        }
        
        private fun decreaseFontSize() {
                if (textPaint.textSize > MIN_TEXT_SIZE + TEXT_SIZE_CHANGE_STEP) {
                        textPaint.textSize = textPaint.textSize - TEXT_SIZE_CHANGE_STEP
                        bottom = calculateTextHeight(text, fontSize)
                        minWidth = calculateTextWidth(text, fontSize)
                }
        }
        
        private val fontSize: Float
                get() = textPaint.textSize
        
        override fun toString(): String = text
}

fun String.lastLine() = this.split("\n").last()

fun fitToWidth(width: Int, text: String, textSize: Float): String {
        tailrec fun fitToWidth1(width: Int, text: String, words: List<String>,
                                textSize: Float): String =
                if (words.isEmpty()) text.trim()
                else {
                        val newWidth = calculateTextWidth(text.lastLine() + words.first(), textSize)
                        val newText = text + (if (newWidth > width) "\n" else " ") + words.first()
                        fitToWidth1(width, newText, words.rest(), textSize)
                }
        
        return fitToWidth1(width, "", text.split(" "), textSize)
}
