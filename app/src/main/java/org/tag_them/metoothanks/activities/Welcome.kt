package org.tag_them.metoothanks.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.startActivity
import org.tag_them.metoothanks.layouts.start_layout
import org.tag_them.metoothanks.requestPermission

class Welcome : AppCompatActivity() {
	private val OPEN_CAMERA_REQUEST_CODE = 0
	private val CAPTURE_IMAGE_REQUEST_CODE = 2
	
	val layout = start_layout()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		layout.setContentView(this)
		
		layout.empty_canvas_button.setOnClickListener { startActivity<Draw>() }
		
		layout.load_from_gallery_button.setOnClickListener {
			Intent(Intent.ACTION_GET_CONTENT).apply {
				addCategory(Intent.CATEGORY_OPENABLE)
				type = "image/*"
			}.apply { startActivityForResult(this, OPEN_IMAGE_REQUEST_CODE) }
		}
		
		/*		if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA))
					layout.take_picture_button.visibility = View.INVISIBLE
				else
					layout.take_picture_button.setOnClickListener { requestPermission(Manifest.permission.CAMERA, OPEN_CAMERA_REQUEST_CODE) }*/
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			OPEN_IMAGE_REQUEST_CODE -> if (data?.data != null) startActivity<Draw>(IMAGE_PATH to data.data.toString())
		//			CAPTURE_IMAGE_REQUEST_CODE -> if (data?.data != null) startActivity<Draw>(BITMAP to data.extras.get("data"))
		}
	}
	
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		
		if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			when (requestCode) {
			//				OPEN_CAMERA_REQUEST_CODE -> startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAPTURE_IMAGE_REQUEST_CODE)
				OPEN_CAMERA_REQUEST_CODE -> {
					Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
						if (resolveActivity(packageManager) != null)
							startActivityForResult(this, CAPTURE_IMAGE_REQUEST_CODE)
					}
				}
			}
	}
}
