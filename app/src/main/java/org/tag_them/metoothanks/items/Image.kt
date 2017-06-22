package org.tag_them.metoothanks.items

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.MenuItem
import org.tag_them.metoothanks.CanvasView
import org.tag_them.metoothanks.R
import android.provider.MediaStore.Images.Media.getBitmap


class Image(bitmap: Bitmap, hostView: CanvasView) : Item(hostView, bitmap.width, bitmap.height) {
	
	override fun handleMenuItemClick(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_image_rotate_clockwise        -> rotateImage(90f)
			R.id.action_image_rotate_counterclockwise -> rotateImage(-90f)
			else -> return false
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
	
	private fun rotateImage(degrees: Float) {
		val matrix = Matrix()
		matrix.postRotate(degrees)
		
		val bitmap = Bitmap.createBitmap(bitmapDrawable.bitmap, 0, 0,
							   bitmapDrawable.intrinsicWidth, bitmapDrawable.intrinsicHeight, matrix, true)
		
		swapWidthHeight()
		
		bitmapDrawable = BitmapDrawable(hostView.resources, bitmap)
		hostView.postInvalidate()
	}
	
	private fun swapWidthHeight() {
		val tmp = right
		right = bottom
		bottom = tmp
	}
}