package org.tag_them.metoothanks.layouts

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.Toolbar
import android.view.ViewManager
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.ankoView
import org.tag_them.metoothanks.CanvasView
import org.tag_them.metoothanks.Draw
import org.tag_them.metoothanks.R

class draw_layout() : AnkoComponent<Draw> {
	lateinit var canvas_view: CanvasView
	lateinit var toolbar: Toolbar
	lateinit var item_toolbar: Toolbar
	
	val ITEM_TOOLBAR_HEIGHT = 50
	
	override fun createView(ui: AnkoContext<Draw>) = with(ui) {
		fun fetchColor(id: Int) = org.tag_them.metoothanks.fetchColor(ctx, id)
		
		verticalLayout {
			val lbottom = bottom
			toolbar = toolbar {
				backgroundColor = fetchColor(R.attr.colorPrimary)
				setTitleTextColor(Color.WHITE)
			}
			
			canvas_view = canvasView {}
			
			item_toolbar = toolbar {
				y = lbottom - dip(ITEM_TOOLBAR_HEIGHT).toFloat()
				backgroundColor = Color.TRANSPARENT
				visibility = Toolbar.INVISIBLE
			}.lparams(width = matchParent, height = dip(ITEM_TOOLBAR_HEIGHT))
			
			canvas_view.itemToolbar = item_toolbar
		}
	}
	
	inline fun ViewManager.canvasView(init: (@AnkoViewDslMarker CanvasView).() -> Unit): CanvasView {
		return ankoView({ ctx: Context -> CanvasView(ctx) }, theme = 0, init = { init() })
	}
}
