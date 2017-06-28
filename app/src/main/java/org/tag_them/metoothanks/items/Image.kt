package org.tag_them.metoothanks.items

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.MenuItem
import org.tag_them.metoothanks.CanvasView
import org.tag_them.metoothanks.R


class Image(bitmap: Bitmap, hostView: CanvasView) : Item(hostView, bitmap.width, bitmap.height) {
	override fun handleMenuItemClick(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_image_rotate_clockwise        -> rotateImage(90f)
			R.id.action_image_rotate_counterclockwise -> rotateImage(-90f)
			else                                      -> return false
		}
		
		hostView.postInvalidate()
		
		return true
	}
	
	override val item_menu_id: Int = R.menu.item_image_menu
	
	var bitmapDrawable = BitmapDrawable(hostView.context.resources, bitmap).apply {
		setBounds(0, 0, right, bottom)
		gravity = Gravity.FILL
	}
	
	override fun draw(canvas: Canvas) {
		paint.alpha = 255
		
		with(bitmapDrawable) {
			setBounds(left, top, right, bottom)
			draw(canvas)
		}
		hostView.postInvalidate()
	}
	
	private fun rotateImage(angle: Float) {
		swapWidthHeight()
		bitmapDrawable = BitmapDrawable(hostView.resources, rotate(bitmapDrawable.bitmap, angle))
		hostView.postInvalidate()
	}
	
	private fun swapWidthHeight() {
		print("before: $width $height $bounds")
		val tmp = height
		bottom = top + width
		right = left + tmp
		println(", after: $width $height $bounds")
	}
	
	fun rotate(source: Bitmap, angle: Float): Bitmap {
		val matrix = Matrix()
		matrix.postRotate(angle)
		return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
	}
}

