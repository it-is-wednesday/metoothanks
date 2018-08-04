package org.itiswednesday.metoothanks.activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import org.itiswednesday.metoothanks.R
import java.io.FileNotFoundException

const val IMAGE_PATH = "image"
const val GITHUB_RELEASES_PAGE = "https://github.com/it-is-wednesday/metoothanks/releases/latest"

class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        findViewById<Button>(R.id.button_empty_canvas).setOnClickListener {
            val intent = Intent(this, Edit::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.button_load_from_galley).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, OPEN_IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                OPEN_IMAGE_REQUEST_CODE -> if (data != null)
                    try {
                        val intent = Intent(this, Edit::class.java)
                        intent.putExtra(IMAGE_PATH, data.data)
                        startActivity(intent)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
            }
    }
}
