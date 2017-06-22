package org.tag_them.metoothanks.items

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.Menu
import android.view.MenuItem
import org.tag_them.metoothanks.CanvasView
import org.tag_them.metoothanks.EDGE_WIDTH

abstract class Item(val hostView: CanvasView, width: Int, height: Int) {
	var left = 0
		set(value) {
			if (value < right - EDGE_WIDTH) field = value
		}
	var top = 0
		set(value) {
			if (value < bottom - EDGE_WIDTH) field = value
		}
	var right = width
		set(value) {
			if (value > left + EDGE_WIDTH) field = value
		}
	var bottom = height
		set(value) {
			if (value > top + EDGE_WIDTH) field = value
		}
	
	val width
		get() = right - left
	val height
		get() = bottom - top
	
	protected val paint = Paint()
	
	val bounds
		get() = RectF(Rect(left, top, right, bottom))
	
	abstract val item_menu_id: Int
	
	abstract fun handleMenuItemClick(item: MenuItem): Boolean
	
	abstract fun draw(canvas: Canvas)
	
	fun touched(touchx: Int, touchy: Int): Boolean = touchx in left..right && touchy in top..bottom
	
	fun move(x: Int, y: Int) {
		right = x + (right - left)
		bottom = y + (bottom - top)
		left = x
		top = y
	}
}
