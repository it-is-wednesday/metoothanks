package org.itiswednesday.metoothanks.activities

import android.Manifest
import android.app.ActionBar
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import kotlinx.android.synthetic.main.activity_edit.*
import org.itiswednesday.metoothanks.*
import org.itiswednesday.metoothanks.R.id.*


const val OPEN_IMAGE_REQUEST_CODE = 1
const val EXPORT_IMAGE_REQUEST_CODE = 2
const val SHARE_IMAGE_REQUEST_CODE = 3

class Edit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.title = resources.getString(R.string.app_name)

        canvas.post {
            intent?.extras?.let {
                val uri = Uri.parse(it.get(IMAGE_PATH).toString())
                canvas.addImage(bitmapFromUri(uri))
            }
        }
    }

    private fun bitmapFromUri(uri: Uri): Bitmap =
            MediaStore.Images.Media.getBitmap(contentResolver, uri)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            OPEN_IMAGE_REQUEST_CODE ->
                if (data != null)
                    canvas.addImage(bitmapFromUri(data.data))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_draw, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        fun export() {
            if (checkExternalMedia())
                ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        EXPORT_IMAGE_REQUEST_CODE
                )
        }

        fun addImage() {
            Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }.startActivity(OPEN_IMAGE_REQUEST_CODE)
        }

        fun share() {
            if (checkExternalMedia()) {
                val uri = writeToStorage(canvas, writeTemporarily = true)

                val i = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/jpeg"
                }

                Intent.createChooser(i, resources.getText(R.string.send_to))
                        .startActivity(SHARE_IMAGE_REQUEST_CODE)
            }
        }

        when (item.itemId) {
            action_add_image -> addImage()
            action_add_text  -> openTextInputDialog { canvas.addText(it) }
            action_export    -> export()
            action_share     -> share()
            action_close     -> close()
        }

        return false
    }


    override fun onBackPressed() = close()

    private fun close() {
        AlertDialog.Builder(this).apply {
            setMessage(R.string.exit_confirmation)
            setPositiveButton(R.string.choose_exit) { _, _ -> super.onBackPressed() }
            setNegativeButton(R.string.choose_stay) { _, _ -> }
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            when (requestCode) {
                EXPORT_IMAGE_REQUEST_CODE -> {
                    writeToStorage(canvas)
                    snackbar("Saved to /metoothanks")
                }
            }
    }

    private fun Intent.startActivity(requestCode: Int) = startActivityForResult(
            this, requestCode
    )

    private val canvas
        get() = findViewById<CanvasView>(R.id.canvas_view)
}
