package org.tag_them.metoothanks.layouts

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.widget.Button
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.tag_them.metoothanks.R
import org.tag_them.metoothanks.activities.Welcome
import org.tag_them.metoothanks.fetchColor
import org.tag_them.metoothanks.newVersionAvailable

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
				addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
				clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
				statusBarColor = accentColor
				navigationBarColor = accentColor
			}
			
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
				topOf(LOAD_FROM_GALLERY_ID)
				bottomMargin = BUTTON_SPACING
				centerHorizontally()
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
				centerInParent()
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
			newVersionAvailable(update_notifier)
		}
	}
}

