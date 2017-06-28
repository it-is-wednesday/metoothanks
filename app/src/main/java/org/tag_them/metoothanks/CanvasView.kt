package org.tag_them.metoothanks

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.os.SystemClock
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.tag_them.metoothanks.activities.Draw
import org.tag_them.metoothanks.items.Image
import org.tag_them.metoothanks.items.Item
import org.tag_them.metoothanks.items.Text
import java.io.Serializable

val CORNER_RADIUS = 25f
val EDGE_WIDTH = 19f

class CanvasView : View, Serializable {
	private var selected_item: Item? = null
	val items = ArrayList<Item>()
	
	lateinit var hostActivity: Draw
	
	constructor(context: Context?) : super(context)
	
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	
	init {
		isDrawingCacheEnabled = true
	}
	
	val framePaint = Paint().apply {
		style = Paint.Style.STROKE
		strokeWidth = EDGE_WIDTH
		color = fetchColor(context, R.attr.colorPrimary)
	}
	
	override fun onDraw(canvas: Canvas) {
		items.forEach {
			it.draw(canvas)
		}
		
		if (selected_item != null)
			canvas.drawRoundRect(selected_item!!.bounds, CORNER_RADIUS, CORNER_RADIUS, framePaint)
	}
	
	
	fun addImage(bitmap: Bitmap, center: Boolean = false) =
			// fitBitmap – resizing the image in case it's bigger than the screen
			Image(fitBitmap(bitmap, width, height), hostView = this).apply {
				if (center)
					move(this@CanvasView.width / 2 - this.width / 2,
					     this@CanvasView.height / 2 - this.height / 2)
			}.addToItems()
	
	fun addText(text: String) = Text(text, width, hostView = this).addToItems()
	
	
	// if a touch is registered less than 101 milliseconds
	// since the last touch, it will be treated as a drag/slide
	private val MAX_ELAPSED_TIME_TO_DRAG = 100
	
	// last time the canvas was touched with one pointer
	private var prevMoveTouchTime = SystemClock.elapsedRealtime()
	
	// last time the canvas was touched with two pointers
	private var prevResizeTouchTime = SystemClock.elapsedRealtime()
	
	// the distance between the X coordinate of the pointer and the item's left edge (used for dragging)
	private var gripDistanceX = 0
	
	// the distance between the Y coordinate of the pointer and the item's top edge (used for dragging)
	private var gripDistanceY = 0
	
	// an array of gripDistanceX and gripDistanceY for two pointers (used for resize)
	private var pointersGripDistance = Array(2, { Point() })
	
	// pointers' coordinates at the beginning of a resize gestrure
	private var pointersGrip = Array(2, { Point() })
	
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		if (event == null) return false
		
		val pointerCount = event.pointerCount
		
		fun calcGripX(x: Int): Int {
			return when (x) {
				in 0..(selected_item!!.left + selected_item!!.width / 2) -> x - selected_item!!.left
				else                                                     -> selected_item!!.right - x
			}
		}
		
		fun calcGripY(y: Int): Int {
			return when (y) {
				in 0..(selected_item!!.top + selected_item!!.height / 2) -> y - selected_item!!.top
				else                                                     -> selected_item!!.bottom - y
			}
		}
		
		when (pointerCount) {
			1 -> {
				val x = event.x.toInt()
				val y = event.y.toInt()
				
				// looking for a picture that got touched;
				// since the first pictures in the array get drawn first,
				// we iterate over the array from its end in order to get the
				// furthest picture (picture in the front)
				for (item in items.reversed()) {
					if (SystemClock.elapsedRealtime() - prevMoveTouchTime < MAX_ELAPSED_TIME_TO_DRAG && selected_item != null) {
						selected_item?.move(x - gripDistanceX, y - gripDistanceY)
					} else {
						selectNone()
						if (item.touched(x, y)) {
							gripDistanceX = x - item.left
							gripDistanceY = y - item.top
							
							item.select()
						}
					}
					
					prevMoveTouchTime = SystemClock.elapsedRealtime()
				}
			}
			2 -> {
				if (selected_item != null) {
					if (SystemClock.elapsedRealtime() - prevResizeTouchTime < MAX_ELAPSED_TIME_TO_DRAG) {
						val pointers = Array(pointerCount, {
							Point(event.getX(it).toInt(),
								event.getY(it).toInt())
						})
						
						for (index in pointers.indices) {
							if (pointersGrip[index].x in 0..(selected_item!!.left + selected_item!!.width / 2))
								selected_item?.left = pointers[index].x - pointersGripDistance[index].x
							else
								selected_item?.right = pointers[index].x + pointersGripDistance[index].x
							
							if (pointersGrip[index].y in 0..(selected_item!!.top + selected_item!!.height / 2))
								selected_item?.top = pointers[index].y - pointersGripDistance[index].y
							else
								selected_item?.bottom = pointers[index].y + pointersGripDistance[index].y
						}
					} else {
						pointersGripDistance = Array(2) {
							Point(calcGripX(event.getX(it).toInt()),
								calcGripY(event.getY(it).toInt()))
						}
						pointersGrip = Array(2) {
							Point(event.getX(it).toInt(), event.getY(it).toInt())
						}
					}
					
					prevResizeTouchTime = SystemClock.elapsedRealtime()
				}
			}
		}
		invalidate()
		
		return true
	}
	
	fun Item.select() {
		selected_item = this
		with(hostActivity.layout) {
			with(item_toolbar) {
				menu.clear()
				inflateMenu(R.menu.item_menu)
				inflateMenu(item_menu_id)
				setOnMenuItemClickListener {
					when (it.itemId) {
						R.id.action_item_delete        -> selected_item!!.removeFromItems()
						R.id.action_item_move_forward  -> items.swapWithNextItem(selected_item!!)
						R.id.action_item_move_backward -> items.swapWithPrevItem(selected_item!!)
						else                           -> handleMenuItemClick(it)
					}
					
					true
				}
				visibility = Toolbar.VISIBLE
			}
			adjustItemManagementToolbarY(this@CanvasView.bottom)
		}
		
		invalidate()
	}
	
	fun selectNone() {
		selected_item = null
		with(hostActivity.layout) {
			item_toolbar.visibility = Toolbar.INVISIBLE
			adjustItemManagementToolbarY(bottom)
		}
	}
	
	fun Item.addToItems() = items.add(this).run { select(); invalidate(); }
	
	fun Item.removeFromItems() = items.remove(this).run { selectNone(); invalidate() }
}