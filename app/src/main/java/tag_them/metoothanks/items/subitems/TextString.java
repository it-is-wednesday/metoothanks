package tag_them.metoothanks.items.subitems;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.widget.EditText;

import tag_them.metoothanks.activities.Draw;

import static tag_them.metoothanks.Utils.calculateTextWidth;
import static tag_them.metoothanks.Utils.openTextInputDialog;

public class TextString {
	
	public static final int DEFAULT_TEXT_SIZE = 120,
		MIN_TEXT_SIZE = 40,
		TEXT_SIZE_CHANGE_STEP = 20;
	
	private int alignment;
	public static final int ALIGNMENT_LEFT = 1,
		ALIGNMENT_RIGHT = 2;
	
	private TextPaint textPaint;
	private String text, originalText;
	
	public TextString(String text) {
		this.text = text;
		originalText = text;
		
		textPaint = new TextPaint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(DEFAULT_TEXT_SIZE);
		textPaint.setColor(0xFF000000);
		
		alignment = ALIGNMENT_LEFT;
	}
	
	/**
	 * Adds or removes newlines to make the text fit nicely to the specified width
	 *
	 * @param width to fit into
	 */
	public void fitToWidth(int width) {
		StringBuilder stringBuilder = new StringBuilder();
		int widthSum = 0;
		
		for (String s : originalText.split(" ")) {
			if (!s.equals("\n"))
				widthSum += textPaint.measureText(s + " ");
			if (widthSum > width) {
				stringBuilder.append("\n").append(s).append(" ");
				widthSum = (int) textPaint.measureText(s + " ");
				continue;
			}
			stringBuilder.append(s).append(" ");
		}
		
		text = stringBuilder.toString();
		
	}
	
	public void draw(int left, int top, int right, Canvas canvas) {
		// since canvas.drawText() doesn't draw multiline text properly,
		// we'll draw each line separately
		String[] strings = text.split("\n");
		for (int i = 0; i < strings.length; i++) {
			float xPosition;
			switch (alignment) {
				case ALIGNMENT_LEFT:
					xPosition = left;
					break;
				
				case ALIGNMENT_RIGHT:
				default:
					xPosition = right - calculateTextWidth(strings[i], getFontSize());
			}
			// added 1 to i because for some reason canvas.drawText() treats the y value
			// as the position of the bottom edge of the image rather than the top edge
			canvas.drawText(strings[i], xPosition
				, top + (i + 1) * textPaint.getTextSize(), textPaint);
		}
	}
	
	public void edit(Context context) {
		final EditText editText = new EditText(context);
		editText.setText(originalText);
		openTextInputDialog(context, editText, (dialog, which) -> originalText = editText.getText().toString());
	}
	
	public void increaseFontSize() {
		textPaint.setTextSize(textPaint.getTextSize() + TEXT_SIZE_CHANGE_STEP);
	}
	
	public void decreaseFontSize() {
		if (textPaint.getTextSize() > MIN_TEXT_SIZE + TEXT_SIZE_CHANGE_STEP)
			textPaint.setTextSize(textPaint.getTextSize() - TEXT_SIZE_CHANGE_STEP);
	}
	
	public float getFontSize() {
		return textPaint.getTextSize();
	}
	
	public String toString() {
		return text;
	}
	
	public int getAlignment() {
		return alignment;
	}
	
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
	
	public int getWidth() {
		return calculateTextWidth(text, getFontSize());
	}
}
