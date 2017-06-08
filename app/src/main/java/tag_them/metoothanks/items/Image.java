package tag_them.metoothanks.items;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;

import tag_them.metoothanks.CanvasView;
import tag_them.metoothanks.R;

public class Image extends Item {
	private BitmapDrawable bitmapDrawable;
	
	public Image(Bitmap bitmap, CanvasView hostView) {
		super(hostView, bitmap.getWidth(), bitmap.getHeight());
		bitmapDrawable = new BitmapDrawable(hostView.getContext().getResources(), bitmap);
		bitmapDrawable.setBounds(0, 0, right, bottom);
		bitmapDrawable.setGravity(Gravity.FILL);
	}
	
	@Override
	public void draw(Canvas canvas) {
		getPaint().setAlpha(255);
		bitmapDrawable.setBounds(left, top, right, bottom);
		bitmapDrawable.draw(canvas);
		drawFrame(canvas);
	}
	
	@Override
	public int getMenuID() {
		return R.menu.item_image_menu;
	}
	
	@Override
	public CanvasView.ClickListener getClickListener() {
		return item -> {
			switch (item.getItemId()) {
				case R.id.action_image_rotate_counterclockwise:
					rotateImage(-90);
					return true;
					
				case R.id.action_image_rotate_clockwise:
					rotateImage(90);
					return true;
			}
			return true;
		};
	}
	
	private void rotateImage(float degrees) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		
		Bitmap bitmap = Bitmap.createBitmap(bitmapDrawable.getBitmap(), 0, 0,
			bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight(), matrix, true);
		
		swapWidthHeight();
		
		bitmapDrawable = new BitmapDrawable(hostView.getResources(), bitmap);
		hostView.postInvalidate();
	}
	
	private void swapWidthHeight() {
		int tmp = right;
		right = bottom;
		bottom = tmp;
	}
}