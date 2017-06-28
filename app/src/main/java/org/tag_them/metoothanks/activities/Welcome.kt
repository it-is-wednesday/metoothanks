package org.tag_them.metoothanks.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.startActivity
import org.tag_them.metoothanks.layouts.IMAGE_PATH
import org.tag_them.metoothanks.layouts.start_layout

class Welcome : AppCompatActivity() {
	val layout = start_layout()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		layout.setContentView(this)
		
		layout.empty_canvas_button.setOnClickListener {
			startActivity<Draw>()
		}
		
		layout.load_from_gallery_button.setOnClickListener {
			Intent(Intent.ACTION_GET_CONTENT).apply {
				addCategory(Intent.CATEGORY_OPENABLE)
				type = "image/*"
			}.apply { startActivityForResult(this, OPEN_IMAGE_REQUEST_CODE) }
		}
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			OPEN_IMAGE_REQUEST_CODE -> if (data != null)
				startActivity<Draw>(IMAGE_PATH to data.data.toString())
		}
	}
}
