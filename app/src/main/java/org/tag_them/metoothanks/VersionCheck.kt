package org.tag_them.metoothanks

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.widget.Button
import org.jetbrains.anko.alert
import org.jsoup.Jsoup

fun newVersionAvailable(button: Button) =
		object : AsyncTask<String, Unit, Boolean>() {
			override fun doInBackground(vararg url: String?): Boolean =
					// getting the latest version number from the right area on GitHub's releases page,
					// then comparing it to the local version number
					Jsoup.connect(url[0]).get()
							.getElementsByClass("release-meta")[0]
							.getElementsByClass("css-truncate-target")[0]
							.html() != BuildConfig.VERSION_NAME
			
			override fun onPostExecute(newVersionAvailable: Boolean) {
				button.setText(
						if (newVersionAvailable)
							R.string.update_availabe
						else
							R.string.update_not_available)
			}
		}.execute("https://github.com/tag-them/metoothanks/releases")
