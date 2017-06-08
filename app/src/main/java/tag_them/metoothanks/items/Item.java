package tag_them.metoothanks.items;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;

import tag_them.metoothanks.CanvasView;
import tag_them.metoothanks.R;

import java.util.function.Consumer;

import static tag_them.metoothanks.Utils.fetchColor;

public abstract class Item {
	int left, top, right, bottom;
	private boolean selected;
	
	private static final int FRAME_WIDTH = 12;
	private static final int CORNER_DRAW_RADIUS = 16;
	
	private static final int MIN_RIB_SIZE = 100;
	public static final int LEFT_EDGE = 1,
		TOP_EDGE = 2,
		RIGHT_EDGE = 3,
		BOTTOM_EDGE = 4,
		TOP_LEFT_CORNER = 5,
		TOP_RIGHT_CORNER = 6,
		BOTTOM_RIGHT_CORNER = 7,
		BOTTOM_LEFT_CORNER = 8,
		EDGE_NONE = 0;
	private final int EDGE_WIDTH = 40;
	
	private Paint paint;
	
	CanvasView hostView;
	
	Consumer<Canvas> drawFrameMethod;
	
	Item(CanvasView hostView, int width, int height) {
		this.hostView = hostView;
		bottom = height;
		right = width;
		
		paint = new Paint();
		move(0, 0);
	}
	
	public abstract void draw(Canvas canvas);
	
	public int getTouchedEdge(int touchX, int touchY) {
		if (touchX < left - EDGE_WIDTH || touchX > right + EDGE_WIDTH ||
			touchY < top - EDGE_WIDTH || touchY > bottom + EDGE_WIDTH)
			return EDGE_NONE;
		
		boolean leftTouched = touchX > left - EDGE_WIDTH && touchX < left + EDGE_WIDTH,
			topTouched = touchY > top - EDGE_WIDTH && touchY < top + EDGE_WIDTH,
			rightTouched = touchX > right - EDGE_WIDTH && touchX < right + EDGE_WIDTH,
			bottomTouched = touchY > bottom - EDGE_WIDTH && touchY < bottom + EDGE_WIDTH;
		
		if (leftTouched) {
			if (topTouched)
				return TOP_LEFT_CORNER;
			if (bottomTouched)
				return BOTTOM_LEFT_CORNER;
			return LEFT_EDGE;
		}
		
		if (rightTouched) {
			if (topTouched)
				return TOP_RIGHT_CORNER;
			if (bottomTouched)
				return BOTTOM_RIGHT_CORNER;
			return RIGHT_EDGE;
		}
		
		if (topTouched)
			return TOP_EDGE;
		
		if (bottomTouched)
			return BOTTOM_EDGE;
		
		return EDGE_NONE;
	}
	
	/**
	 * Draws a frame around the item if it's selected
	 * This method is not integrated into draw() since we want the frame to be drawn after the
	 * item has been drawn, to make it appear above the item.
	 * drawFrame() should be called at the end of draw().
	 *
	 * @param canvas to be drawn into
	 */
	void drawFrame(Canvas canvas) {
		if (isSelected()) {
			// draw frame
			getPaint().setColor(fetchColor(R.attr.colorPrimary, hostView.getContext()));
			getPaint().setStyle(Paint.Style.STROKE);
			getPaint().setStrokeWidth(FRAME_WIDTH);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
				drawSharpFrame(canvas);
			else
				drawRoundFrame(canvas);
		}
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void drawRoundFrame(Canvas canvas) {
		canvas.drawRoundRect(left, top, right, bottom, CORNER_DRAW_RADIUS, CORNER_DRAW_RADIUS, getPaint());
	}
	
	private void drawSharpFrame(Canvas canvas) {
		canvas.drawRect(left, top, right, bottom, getPaint());
	}
	
	public boolean isTouched(int touchX, int touchY) {
		return touchX > left && touchX < right
			&& touchY > top && touchY < bottom;
	}
	
	public abstract CanvasView.ClickListener getClickListener();
	
	public abstract int getMenuID();
	
	public void move(int x, int y) {
		right = x + (right - left);
		bottom = y + (bottom - top);
		left = x;
		top = y;
	}
	
	Paint getPaint() {
		return paint;
	}
	
	private boolean isSelected() {
		return selected;
	}
	
	public void select() {
		selected = true;
	}
	
	public void deselect() {
		selected = false;
	}
	
	public int getWidth() {
		return right - left;
	}
	
	public int getHeight() {
		return bottom - top;
	}
	
	public int getLeft() {
		return left;
	}
	
	public void setLeft(int left) {
		if (left < right - MIN_RIB_SIZE)
			this.left = left;
	}
	
	public int getTop() {
		return top;
	}
	
	public void setTop(int top) {
		if (top < bottom - MIN_RIB_SIZE)
			this.top = top;
	}
	
	public int getRight() {
		return right;
	}
	
	public void setRight(int right) {
		if (right > left + MIN_RIB_SIZE)
			this.right = right;
	}
	
	public int getBottom() {
		return bottom;
	}
	
	public void setBottom(int bottom) {
		if (bottom > top + MIN_RIB_SIZE)
			this.bottom = bottom;
	}
}