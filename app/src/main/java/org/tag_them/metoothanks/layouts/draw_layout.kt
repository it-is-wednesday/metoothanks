package org.tag_them.metoothanks.layouts

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.Toolbar
import android.view.ViewManager
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.ankoView
import org.tag_them.metoothanks.CanvasView
import org.tag_them.metoothanks.activities.Draw
import org.tag_them.metoothanks.R

val ITEM_TOOLBAR_HEIGHT = 50

class draw_layout : AnkoComponent<Draw> {
	lateinit var canvas_view: CanvasView
	lateinit var toolbar: Toolbar
	lateinit var item_management_toolbar: Toolbar
	lateinit var item_toolbar: Toolbar
	
	
	override fun createView(ui: AnkoContext<Draw>) = with(ui) {
		fun fetchColor(id: Int) = org.tag_them.metoothanks.fetchColor(ctx, id)
		
		verticalLayout {
			val lbottom = bottom
			toolbar = toolbar {
				backgroundColor = fetchColor(R.attr.colorPrimary)
				setTitleTextColor(Color.WHITE)
			}
			
			canvas_view = canvasView {
				hostActivity = owner
			}.lparams(width = matchParent, height = matchParent)
			
			item_management_toolbar = toolbar {
				backgroundColor = Color.TRANSPARENT
				inflateMenu(R.menu.item_management_menu)
			}.lparams(width = wrapContent, height = dip(ITEM_TOOLBAR_HEIGHT))
			
			item_toolbar = toolbar {
				y = lbottom - dip(ITEM_TOOLBAR_HEIGHT*2).toFloat()
				backgroundColor = Color.TRANSPARENT
				visibility = Toolbar.INVISIBLE
			}.lparams(width = wrapContent, height = dip(ITEM_TOOLBAR_HEIGHT))
			
			
			adjustItemManagementToolbarY(lbottom)
		}
	}
	
	fun adjustItemManagementToolbarY(layoutBottom: Int) = with(item_management_toolbar) {
		y = layoutBottom -
		    if (item_toolbar.visibility == Toolbar.INVISIBLE) dip(ITEM_TOOLBAR_HEIGHT).toFloat()
		    else dip(ITEM_TOOLBAR_HEIGHT * 2).toFloat()
	}
	
	inline fun ViewManager.canvasView(init: (@AnkoViewDslMarker CanvasView).() -> Unit): CanvasView {
		return ankoView({ ctx: Context -> CanvasView(ctx) }, theme = 0, init = { init() })
	}
}
