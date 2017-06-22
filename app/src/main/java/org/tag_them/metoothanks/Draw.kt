package org.tag_them.metoothanks

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.*
import org.tag_them.metoothanks.R.id.*
import org.tag_them.metoothanks.layouts.draw_layout

class Draw : AppCompatActivity() {
	val layout = draw_layout()
	
	val OPEN_IMAGE_REQUEST_CODE = 1
	val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		layout.setContentView(this)
		setSupportActionBar(layout.toolbar)
	}
	
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_draw, menu)
		return true
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			action_add_image -> {
				Intent(Intent.ACTION_GET_CONTENT).apply {
					addCategory(Intent.CATEGORY_OPENABLE)
					type = "image/*"
				}.startActivity(OPEN_IMAGE_REQUEST_CODE)
			}
			action_add_text  -> {
				// executed only if the result of the openTextInputDialog isn't null,
				// meaning the user hit the positive button rather than the back button
				openTextInputDialog {
					layout.canvas_view.addText(it)
					toast(it)
				}
			}
			action_export    ->
				if (checkExternalMedia()) {
					ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
										    WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
				}
		}
		
		return super.onOptionsItemSelected(item)
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			OPEN_IMAGE_REQUEST_CODE -> if (data != null)
				layout.canvas_view.addImage(MediaStore.Images.Media.getBitmap(contentResolver, data.data))
		}
	}
	
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			when (requestCode) {
				WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {
					writeToSD(layout.canvas_view)
					snackbar("Saved to /metoothanks")
				}
			}
	}
	
	fun Intent.startActivity(requestCode: Int) = startActivityForResult(this, requestCode)
}
