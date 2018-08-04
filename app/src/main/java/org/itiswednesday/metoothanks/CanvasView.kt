package org.itiswednesday.metoothanks

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import org.itiswednesday.metoothanks.activities.Edit
import org.itiswednesday.metoothanks.items.Image
import org.itiswednesday.metoothanks.items.Item
import org.itiswednesday.metoothanks.items.Text
import java.io.Serializable
import android.graphics.DashPathEffect



const val CORNER_RADIUS = 25f
const val EDGE_WIDTH = 10f

class CanvasView : View, Serializable {
    private var selectedItem: Item? = null
    private val items = ArrayList<Item>()

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private val framePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = EDGE_WIDTH
        color = fetchColor(context, R.attr.colorPrimary)
        pathEffect = DashPathEffect(floatArrayOf(50f, 50f), 0f)
    }

    override fun onDraw(canvas: Canvas) {
        items.forEach {
            it.draw(canvas)
        }

        if (selectedItem != null)
            canvas.drawRoundRect(selectedItem!!.bounds, CORNER_RADIUS, CORNER_RADIUS, framePaint)
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
    private var gripDistanceFromLeftEdge = 0

    // the distance between the Y coordinate of the pointer and the item's top edge (used for dragging)
    private var gripDistanceFromTopEdge = 0

    // pointers' coordinates at the beginning of a resize
    private var pointersGrip = PointPair({ Point() })

    // bounds value at the beginning of the resize
    private var originalBounds = RectF()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false

        val pointerCount = event.pointerCount

        when (pointerCount) {
            1 -> {
                val x = event.x.toInt()
                val y = event.y.toInt()

                // looking for a picture that got touched;
                // since the first pictures in the array get drawn first,
                // we iterate over the array from its end in order to get the
                // furthest picture (picture in the front)
                for (item in items.reversed()) {
                    if (SystemClock.elapsedRealtime() -
                            prevMoveTouchTime < MAX_ELAPSED_TIME_TO_DRAG && selectedItem != null)
                        selectedItem?.move(
                                x - gripDistanceFromLeftEdge, y - gripDistanceFromTopEdge)
                    else {
                        selectNone()
                        if (item.touched(x, y)) {
                            gripDistanceFromLeftEdge = x - item.left
                            gripDistanceFromTopEdge = y - item.top

                            item.select()
                        }
                    }

                    prevMoveTouchTime = SystemClock.elapsedRealtime()
                }
            }
            2 -> {
                if (SystemClock.elapsedRealtime() -
                        prevResizeTouchTime < MAX_ELAPSED_TIME_TO_DRAG) {
                    val pointers = PointPair(
                            init = { Point(event.getX(it).toInt(), event.getY(it).toInt()) })

                    selectedItem?.resize(pointers, pointersGrip, originalBounds)
                } else {
                    pointersGrip = PointPair {
                        Point(event.getX(it).toInt(), event.getY(it).toInt())
                    }

                    selectedItem?.let { originalBounds = it.bounds }
                }

                prevResizeTouchTime = SystemClock.elapsedRealtime()
            }
        }

        invalidate()

        return true
    }

    private fun Item.select() {
        selectedItem = this

        val clickListener = { item: MenuItem ->
            when (item.itemId) {
                R.id.action_item_delete        -> selectedItem!!.removeFromItems()
                R.id.action_item_move_forward  -> items.swapWithNextItem(selectedItem!!)
                R.id.action_item_move_backward -> items.swapWithPrevItem(selectedItem!!)
                else                           -> handleMenuItemClick(item)
            }

            true
        }

        with(itemToolbar) {
            menu.clear()
            inflateMenu(R.menu.item_menu)
            inflateMenu(itemMenuID)
            setOnMenuItemClickListener(clickListener)
            visibility = Toolbar.VISIBLE
        }

        invalidate()
    }


    fun selectNone() {
        selectedItem = null
        itemToolbar.menu.clear()
    }

    private fun Item.addToItems() {
        items.add(this)
        this.select()
        invalidate()
    }

    private fun Item.removeFromItems() {
        items.remove(this)
        selectNone()
        invalidate()
    }

    private val itemToolbar
        get() = (context as Edit).findViewById<Toolbar>(R.id.item_toolbar)
}