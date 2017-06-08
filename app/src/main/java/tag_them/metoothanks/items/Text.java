package tag_them.metoothanks.items;

import android.graphics.Canvas;

import tag_them.metoothanks.CanvasView;
import tag_them.metoothanks.R;
import tag_them.metoothanks.items.subitems.TextString;

import static tag_them.metoothanks.Utils.calculateTextHeight;
import static tag_them.metoothanks.Utils.calculateTextWidth;
import static tag_them.metoothanks.items.subitems.TextString.DEFAULT_TEXT_SIZE;

public class Text extends Item {
	private TextString textString;
	
	private final int WIDTH_FIX_INTERVAL = 10;
	
	public Text(String text, final int canvasWidth, CanvasView hostView) {
		super(hostView, calculateTextWidth(text, DEFAULT_TEXT_SIZE), calculateTextHeight(text, DEFAULT_TEXT_SIZE));
		textString = new TextString(text);
		
		// makes sure the actual text fits the item's width
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(WIDTH_FIX_INTERVAL);
					
					int width = Math.min(getWidth(), canvasWidth);
					textString.fitToWidth(width);
					right = left + width;
					
					int textHeight = calculateTextHeight(text, textString.getFontSize());
					if (getHeight() < textHeight)
						bottom = top + textHeight;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		textString.draw(left, top, right, canvas);
		drawFrame(canvas);
	}
	
	@Override
	public CanvasView.ClickListener getClickListener() {
		return item -> {
			switch (item.getItemId()) {
				case R.id.action_text_align_left:
					textString.setAlignment(TextString.ALIGNMENT_LEFT);
					
					
					hostView.postInvalidate();
					return true;
				
				case R.id.action_text_align_right:
					textString.setAlignment(TextString.ALIGNMENT_RIGHT);
					
					hostView.postInvalidate();
					return true;
				
				case R.id.action_item_edit:
					textString.edit(hostView.getContext());
					
					hostView.postInvalidate();
					return true;
				
				case R.id.action_text_shrink:
					textString.decreaseFontSize();
					
					hostView.postInvalidate();
					return true;
				
				case R.id.action_text_enlarge:
					textString.increaseFontSize();
					
					int textWidth = textString.getWidth();
					if (textWidth > getWidth())
						right = left + textWidth;
					
					hostView.postInvalidate();
					return true;
			}
			return false;
		};
	}
	
	@Override
	public int getMenuID() {
		return R.menu.item_text_menu;
	}
	
	@Override
	public String toString() {
		return textString.toString();
	}
}