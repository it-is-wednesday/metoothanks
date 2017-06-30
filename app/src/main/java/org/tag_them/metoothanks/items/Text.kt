package org.tag_them.metoothanks.items

import android.graphics.Canvas
import android.graphics.Point
import android.text.TextPaint
import android.view.MenuItem
import org.tag_them.metoothanks.*
import kotlin.concurrent.thread


val DEFAULT_TEXT_SIZE = 120f
val MIN_TEXT_SIZE = 40
val TEXT_SIZE_CHANGE_STEP = 20
val ALIGNMENT_LEFT = 1
val ALIGNMENT_RIGHT = 2
val WIDTH_FIX_INTERVAL = 10L

class Text(text: String, val canvasWidth: Int, hostView: CanvasView) :
		Item(hostView = hostView, width = canvasWidth, height = calculateTextHeight(text, DEFAULT_TEXT_SIZE)) {
	
	override fun handleMenuItemClick(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_text_align_left  -> textString.alignment = ALIGNMENT_LEFT
			R.id.action_text_align_right -> textString.alignment = ALIGNMENT_RIGHT
			R.id.action_item_edit        -> textString.edit()
			R.id.action_text_enlarge     -> textString.increaseFontSize()
			R.id.action_text_shrink      -> textString.decreaseFontSize()
			else                         -> return false
		}
		
		hostView.postInvalidate()
		
		return true
	}
	
	override val item_menu_id: Int = R.menu.item_text_menu
	
	private val textString = TextString(text)
	
	// makes sure the actual text fits the item's minWidth
	val width_fitter = thread {
		while (true) {
			Thread.sleep(WIDTH_FIX_INTERVAL)
			
			val width = Math.min(width, canvasWidth)
			textString.fitToWidth(width)
			right = left + width

//			val textHeight = calculateTextHeight(text, textString.fontSize)
//			if (this.height < textHeight)
//				bottom = top + textHeight
		}
	}
	
	init {
		if (width_fitter.state == Thread.State.NEW)
			width_fitter.start()
	}
	
	override fun draw(canvas: Canvas) {
		textString.draw(left, top, right, canvas)
	}
	
	override fun resize(pointers: Array<Point>, pointersGrip: Array<Point>, pointersGripDistance: Array<Point>) {
		for (index in pointers.indices) {
			if (pointersGrip[index].x in 0..(left + width / 2))
				with(pointers[index].x - pointersGripDistance[index].x) {
					if (this < right - textString.minWidth)
						left = this
				}
			else
				with(pointers[index].x + pointersGripDistance[index].x) {
					if (this > left + textString.minWidth)
						right = this
				}
		}
	}
	
	
	internal inner class TextString(private var text: String) {
		var alignment: Int = 0
		
		private val textPaint: TextPaint
		private var originalText: String
		var minWidth: Int
		
		
		init {
			textPaint = TextPaint().apply {
				isAntiAlias = true
				textSize = DEFAULT_TEXT_SIZE
				color = 0xFF000000.toInt()
			}
			
			originalText = text
			alignment = ALIGNMENT_LEFT
			minWidth = calculateTextWidth(text, fontSize)
		}
		
		/**
		 * Adds or removes newlines to make the text fit nicely to the specified minWidth
		 
		 * @param width to fit into
		 */
		fun fitToWidth(width: Int) {
			val stringBuilder = StringBuilder()
			var widthSum = 0
			for (word in originalText.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
				if (word != "\n")
					widthSum += textPaint.measureText(word + " ").toInt()
				if (widthSum > width + textPaint.measureText(" ")) {
					stringBuilder.append("\n").append(word).append(" ")
					widthSum = textPaint.measureText(word + " ").toInt()
					continue
				}
				stringBuilder.append(word).append(" ")
			}
			
			text = stringBuilder.toString()
		}
		
		// canvas.drawText draws the text a bit too low; this is used to counter it
		val DRAW_Y_GAP = 20
		
		fun draw(left: Int, top: Int, right: Int, canvas: Canvas) {
			// since canvas.drawText() doesn't draw multiline text properly,
			// we'll draw each line separately
			val strings = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			for (i in strings.indices) {
				val xPosition = when (alignment) {
					ALIGNMENT_LEFT -> left.toFloat()
					else           -> right.toFloat() - calculateTextWidth(strings[i], fontSize)
				}
				// added 1 to i because for some reason canvas.drawText() treats the y value
				// as the position of the bottom edge of the image rather than the top edge
				canvas.drawText(strings[i], xPosition, top + (i + 1) * textPaint.textSize - DRAW_Y_GAP, textPaint)
			}
		}
		
		fun edit() {
			hostView.context.openTextInputDialog(originalText) {
				originalText = it
				fitToWidth(this@Text.width)
				minWidth = calculateTextWidth(text, fontSize)
				left = 0; right = canvasWidth
			}
		}
		
		fun increaseFontSize() {
			textPaint.textSize = textPaint.textSize + TEXT_SIZE_CHANGE_STEP
			bottom = calculateTextHeight(text, fontSize)
			minWidth = calculateTextWidth(text, fontSize)
		}
		
		fun decreaseFontSize() {
			if (textPaint.textSize > MIN_TEXT_SIZE + TEXT_SIZE_CHANGE_STEP) {
				textPaint.textSize = textPaint.textSize - TEXT_SIZE_CHANGE_STEP
				bottom = calculateTextHeight(text, fontSize)
				minWidth = calculateTextWidth(text, fontSize)
			}
		}
		
		val fontSize: Float
			get() = textPaint.textSize
		
		override fun toString(): String = text

//		val minWidth: Int
//			get() = calculateTextWidth(text, fontSize)
	}
}