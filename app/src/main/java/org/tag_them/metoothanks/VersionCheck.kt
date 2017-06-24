package org.tag_them.metoothanks

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import org.jetbrains.anko.alert
import org.jsoup.Jsoup

fun checkForNewVersion(context: Context) {
	object : AsyncTask<String, Unit, Boolean>() {
		override fun doInBackground(vararg url: String?): Boolean =
				Jsoup.connect(url[0]).get()
						.getElementsByClass("release-meta")[0]
						.getElementsByClass("css-truncate-target")[0]
						.html() != BuildConfig.VERSION_NAME
		
		override fun onPostExecute(newVersionAvailable: Boolean) {
			if (newVersionAvailable)
				context.alert(R.string.update_availabe) {
					positiveButton(R.string.update_now) {
						context.startActivity(Intent(
								Intent.ACTION_VIEW, Uri.parse("https://github.com/tag-them/metoothanks/releases/latest")))
					}
					negativeButton(R.string.ignore) {
						dialog ->
						dialog.dismiss()
					}
				}.show()
		}
	}.execute("https://github.com/tag-them/metoothanks/releases")
}