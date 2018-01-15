package org.tag_them.metoothanks.layouts

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Button
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jsoup.Jsoup
import org.tag_them.metoothanks.BuildConfig
import org.tag_them.metoothanks.R
import org.tag_them.metoothanks.activities.Welcome
import org.tag_them.metoothanks.fetchColor
import java.lang.ref.WeakReference

val IMAGE_PATH = "image path"
val GITHUB_RELEASES_PAGE = "https://github.com/tag-them/metoothanks/releases/latest"
val TEXT_SIZE = 24f
val BUTTON_PADDING_HORIZONTAL = 40
val BUTTON_PADDING_VERTICAL = 20

val BUTTON_SPACING = 50
val BUTTON_WIDTH = 900

val EMPTY_CANVAS_ID = 1
val LOAD_FROM_GALLERY_ID = 2

class start_layout : AnkoComponent<Welcome> {
	lateinit var empty_canvas_button: Button
	lateinit var load_from_gallery_button: Button
	
	@SuppressLint("ResourceType")
	override fun createView(ui: AnkoContext<Welcome>): View = with(ui) {
		val accentColor = fetchColor(ctx, R.attr.colorAccent)
		
		relativeLayout {
			backgroundColor = accentColor
			owner.window.apply {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
					clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
					statusBarColor = accentColor
					navigationBarColor = accentColor
				}
			}
			
			imageView(R.drawable.metoothanks).lparams { alignParentTop() }
			
			empty_canvas_button = button("empty canvas") {
				id = EMPTY_CANVAS_ID
				backgroundColor = fetchColor(ctx, R.attr.colorPrimary)
				setPadding(BUTTON_PADDING_HORIZONTAL, BUTTON_PADDING_VERTICAL, BUTTON_PADDING_HORIZONTAL, BUTTON_PADDING_VERTICAL)
				setTextColor(Color.WHITE)
				textSize = TEXT_SIZE
				setAllCaps(false)
				
				// onClick is configured in Welcome
			}.lparams {
				width = BUTTON_WIDTH
				centerInParent()
			}
			
			load_from_gallery_button = button("load from gallery") {
				id = LOAD_FROM_GALLERY_ID
				backgroundColor = fetchColor(ctx, R.attr.colorPrimary)
				setPadding(BUTTON_PADDING_HORIZONTAL, BUTTON_PADDING_VERTICAL, BUTTON_PADDING_HORIZONTAL, BUTTON_PADDING_VERTICAL)
				setTextColor(Color.WHITE)
				textSize = TEXT_SIZE
				setAllCaps(false)
				
				// onClick is configured in Welcome
			}.lparams {
				width = BUTTON_WIDTH
				bottomOf(EMPTY_CANVAS_ID)
				topMargin = BUTTON_SPACING
				centerHorizontally()
			}
			
			val update_notifier = button(R.string.update_checking) {
				backgroundColor = Color.TRANSPARENT
				setTextColor(Color.WHITE)
				setAllCaps(false)
				onClick {
					ctx.startActivity(Intent(
							Intent.ACTION_VIEW, Uri.parse(GITHUB_RELEASES_PAGE)))
				}
			}.lparams {
				alignParentBottom()
				centerHorizontally()
			}
			
                        VersionCheck(WeakReference(update_notifier)).execute("https://github.com/tag-them/metoothanks/releases")
		}
	}
 
	companion object {
		class VersionCheck(private val button: WeakReference<Button>) : AsyncTask<String, Unit, Boolean>() {
			override fun doInBackground(vararg url: String?): Boolean =
				// getting the latest version number from the right area on GitHub's releases page,
				// then comparing it to the local version number
				Jsoup.connect(url[0]).get()
					.getElementsByClass("release-meta")[0]
					.getElementsByClass("css-truncate-target")[0]
					.html() != BuildConfig.VERSION_NAME
			
			override fun onPostExecute(newVersionAvailable: Boolean) {
				button.get()?.setText(
					if (newVersionAvailable)
						R.string.update_availabe
					else
						R.string.update_not_available
				)
			}
		}
	}
}

