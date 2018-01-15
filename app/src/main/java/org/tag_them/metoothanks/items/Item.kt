package org.tag_them.metoothanks.items

import android.graphics.*
import android.view.MenuItem
import org.tag_them.metoothanks.CanvasView
import org.tag_them.metoothanks.EDGE_WIDTH

abstract class Item(val canvas: CanvasView, width: Int, height: Int) {
        open var left = 0
                set(value) {
                        if (value < right - EDGE_WIDTH) field = value
                }
        open var top = 0
                set(value) {
                        if (value < bottom - EDGE_WIDTH) field = value
                }
        open var right = width
                set(value) {
                        if (value > left + EDGE_WIDTH) field = value
                }
        open var bottom = height
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
        
        abstract val itemMenuID: Int
        
        abstract fun handleMenuItemClick(item: MenuItem): Boolean
        
        abstract fun draw(canvas: Canvas)
        
        fun touched(touchX: Int, touchY: Int): Boolean = touchX in left..right && touchY in top..bottom
        
        fun move(x: Int, y: Int) {
                right = x + (right - left)
                bottom = y + (bottom - top)
                left = x
                top = y
        }
        
        open fun resize(pointers: Array<Point>, pointersGrip: Array<Point>, pointersGripDistance: Array<Point>) {
                for (index in pointers.indices) {
                        if (pointersGrip[index].x in 0..(left + width / 2))
                                left = pointers[index].x - pointersGripDistance[index].x
                        else
                                right = pointers[index].x + pointersGripDistance[index].x
                        
                        if (pointersGrip[index].y in 0..(top + height / 2))
                                top = pointers[index].y - pointersGripDistance[index].y
                        else
                                bottom = pointers[index].y + pointersGripDistance[index].y
                }
                
        }
}