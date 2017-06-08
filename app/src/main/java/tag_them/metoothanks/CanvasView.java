package tag_them.metoothanks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import tag_them.metoothanks.items.Image;
import tag_them.metoothanks.items.Item;
import tag_them.metoothanks.items.Text;

import java.util.ArrayList;

import static tag_them.metoothanks.Utils.reverse;
import static tag_them.metoothanks.Utils.swapWithNextItem;
import static tag_them.metoothanks.Utils.swapWithPrevItem;


public class CanvasView extends View {
	private ArrayList<Item> items = new ArrayList<>();
	private Item selectedItem;
	
	private Toolbar itemToolbar;
	
	// starting point of a drag gesture
	private int gripX, gripY,
		gripRight, gripBottom;
	private float heightWidthratio;
	
	// time of last touch
	private long prevTouchTime = SystemClock.elapsedRealtime();
	
	private static final int ACTION_MOVE = 1,
		ACTION_RESIZE = 2;
	private int lastAction, grippedEdge;
	
	// if a touch is registered less than 101 milliseconds
	// since the last touch, it will be treated as a drag/slide
	private final int MAX_ELAPSED_TIME_TO_DRAG = 100;
	
	public CanvasView(Context context) {
		super(context);
		setDrawingCacheEnabled(true);
	}
	
	public CanvasView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		setDrawingCacheEnabled(true);
	}
	
	public void addImage(Bitmap bitmap) {
		// resizing the image in case it's bigger than the view/canvas
		float ratio;
		float viewW = getMeasuredWidth(),
			viewH = getMeasuredHeight();
		Bitmap finalBitmap = bitmap;
		if (finalBitmap.getWidth() > viewW) {
			ratio = viewW / finalBitmap.getWidth();
			finalBitmap = Bitmap.createScaledBitmap(bitmap, getMeasuredWidth(), (int) (finalBitmap.getHeight() * ratio), false);
		}
		if (finalBitmap.getHeight() > viewH) {
			ratio = viewH / finalBitmap.getHeight();
			finalBitmap = Bitmap.createScaledBitmap(bitmap, (int) (finalBitmap.getWidth() * ratio), getMeasuredHeight(), false);
		}
		
		// registering the image
		Image newImage = new Image(finalBitmap, this);
		items.add(newImage);
		selectItem(newImage);
		invalidate();
	}
	
	public void addText(String text) {
		Text newText = new Text(text, getMeasuredWidth(), this);
		items.add(newText);
		selectItem(newText);
	}
	
	public void removeSelectedItem() {
		items.remove(selectedItem);
		selectNone();
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		for (Item i : items)
			i.draw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX(),
			y = (int) event.getY();
		
		// looking for a picture that got touched;
		// since the first pictures in the array get drawn first,
		// we iterate over the array from its end in order to get the
		// furthest picture (picture in the front)
		for (Item i : reverse(items)) {
			if (SystemClock.elapsedRealtime() - prevTouchTime < MAX_ELAPSED_TIME_TO_DRAG
				&& selectedItem != null) {
				switch (lastAction) {
					case ACTION_MOVE:
						selectedItem.move(x - gripX, y - gripY);
						break;
					
					case ACTION_RESIZE:
						switch (grippedEdge) {
							case Item.LEFT_EDGE:
								selectedItem.setLeft(x - gripX);
								break;
							case Item.TOP_EDGE:
								selectedItem.setTop(y - gripY);
								break;
							case Item.RIGHT_EDGE:
								selectedItem.setRight(x - gripRight);
								break;
							case Item.BOTTOM_EDGE:
								selectedItem.setBottom(y - gripBottom);
								break;
							case Item.TOP_LEFT_CORNER:
								selectedItem.setLeft(x - gripX);
								selectedItem.setTop(selectedItem.getBottom() - (int) (selectedItem.getWidth() * heightWidthratio));
								break;
							case Item.TOP_RIGHT_CORNER:
								selectedItem.setRight(x - gripRight);
								selectedItem.setTop(selectedItem.getBottom() - (int) (selectedItem.getWidth() * heightWidthratio));
								break;
							case Item.BOTTOM_RIGHT_CORNER:
								selectedItem.setRight(x - gripRight);
								selectedItem.setBottom(selectedItem.getTop() + (int) (selectedItem.getWidth() * heightWidthratio));
								break;
							case Item.BOTTOM_LEFT_CORNER:
								selectedItem.setLeft(x - gripX);
								selectedItem.setBottom(selectedItem.getTop() + (int) (selectedItem.getWidth() * heightWidthratio));
								break;
						}
						break;
				}
			} else {
				heightWidthratio = (float) i.getHeight() / (float) i.getWidth();
				gripX = x - i.getLeft();
				gripY = y - i.getTop();
				gripRight = x - i.getRight();
				gripBottom = y - i.getBottom();
				
				grippedEdge = i.getTouchedEdge(x, y);
				if (grippedEdge != Item.EDGE_NONE) {
					selectItem(i);
					lastAction = ACTION_RESIZE;
				} else {
					if (i.isTouched(x, y)) {
						selectItem(i);
						lastAction = ACTION_MOVE;
					} else {
						selectNone();
					}
				}
			}
			
			prevTouchTime = SystemClock.elapsedRealtime();
		}
		
		invalidate();
		
		return true;
	}
	
	public void prepareToExport(Canvas canvas) {
		selectNone();
		canvas.drawColor(Color.WHITE);
		draw(canvas);
	}
	
	private void selectNone() {
		itemToolbar.setVisibility(Toolbar.INVISIBLE);
		
		if (selectedItem != null)
			selectedItem.deselect();
		selectedItem = null;
	}
	
	private void selectItem(Item item) {
//		// swapping with the last image in the array
//		// so the selected image will be drawn at the front
//		Collections.swap(images, images.indexOf(image), images.size() - 1);

//		 add a delete option to the menu bar
		
		itemToolbar.getMenu().clear();
		itemToolbar.inflateMenu(R.menu.item_menu);
		itemToolbar.inflateMenu(item.getMenuID());
		itemToolbar.setOnMenuItemClickListener(getOnMenuItemClickListener(item.getClickListener()));
		itemToolbar.setVisibility(Toolbar.VISIBLE);
		
		if (selectedItem != null)
			selectedItem.deselect();
		selectedItem = item;
		selectedItem.select();
	}
	
	public void setItemToolbar(Toolbar itemToolbar) {
		this.itemToolbar = itemToolbar;
	}
	
	public Toolbar.OnMenuItemClickListener getOnMenuItemClickListener(final ClickListener additionalListener) {
		return item -> {
			int id = item.getItemId();
			switch (id) {
				case R.id.action_item_delete:
					removeSelectedItem();
					invalidate();
					return true;
					
				case R.id.action_item_move_forward:
					swapWithNextItem(selectedItem, items);
					invalidate();
					return true;
					
				case R.id.action_item_move_backward:
					swapWithPrevItem(selectedItem, items);
					invalidate();
					return true;
					
				default:
					additionalListener.handleClick(item);
			}
			return false;
		};
	}
	
	public interface ClickListener {
		boolean handleClick(MenuItem item);
	}
}