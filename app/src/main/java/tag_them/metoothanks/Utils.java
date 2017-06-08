package tag_them.metoothanks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

public final class Utils {
	static <T> ArrayList<T> reverse(ArrayList<T> arrayList) {
		ArrayList<T> result = new ArrayList<>(arrayList);
		Collections.reverse(result);
		
		return result;
	}
	
	public static void openTextInputDialog(Context context, EditText editText, DialogInterface.OnClickListener clickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		editText.setSingleLine(false);
		editText.setBackgroundColor(Color.WHITE);
		
		LayoutInflater inflater;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.textbox_dialog, null);
		linearLayout.addView(editText, 0);
		builder.setView(linearLayout);
		
		// for some reason lollipop can't handle emoji
		int confirmationStringID = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? R.string.confirmation_string : R.string.ok_hand_sign_emoji;
		builder.setPositiveButton(confirmationStringID, clickListener);
		
		AlertDialog dialog = builder.create();
		
		// makes the keyboard pop when the dialog is shown
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		dialog.show();
	}
	
	
	public static void snackbar(View view, String message) {
		Snackbar.make(view, message, Snackbar.LENGTH_LONG)
			.setAction("Action", null).show();
		
	
	}
	
	public static int fetchColor(int colorID, Context context) {
		TypedValue typedValue = new TypedValue();
		
		TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{colorID});
		int color = a.getColor(0, 0);
		
		a.recycle();
		
		return color;
	}
	
	public static int calculateTextWidth(String text, float textSize) {
		TextPaint textPaint = new TextPaint();
		textPaint.setTextSize(textSize);
		
		int maxLine = 0;
		for (String s : text.split("\n")) {
			int currentLine = (int) textPaint.measureText(s);
			if (currentLine > maxLine)
				maxLine = currentLine;
		}
		
		return maxLine + (int) textPaint.measureText(" ");
	}
	
	public static int calculateTextHeight(String text, float textSize) {
		int lines = text.split("\n").length;
		return (int) (lines * textSize);
	}
	
	static <T> void swapWithNextItem(T item, ArrayList<T> a) {
		swap(item, 1, a);
	}
	
	static <T> void swapWithPrevItem(T item, ArrayList<T> a) {
		swap(item, -1, a);
	}
	
	private static <T> void swap(T item, int relativePosition, ArrayList<T> a) {
		try {
			int index = a.indexOf(item);
			a.set(index, a.set(index + relativePosition, item));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class VersionChecker extends AsyncTask<String, Void, String> {
		Context context;
		
		public VersionChecker(Context context) {
			this.context = context;
		}
		
		protected String doInBackground(String... url) {
			try {
				URL fileurl = new URL(url[0]);
				URLConnection connection = fileurl.openConnection();
				System.out.println(connection.getInputStream() == null);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String version = bufferedReader.readLine();
				return version;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		protected void onPostExecute(String latestVersion) {
			System.out.println("Latest version is " + latestVersion + ". Installed version is " + BuildConfig.VERSION_NAME);
			if (!latestVersion.equals(BuildConfig.VERSION_NAME)) {
				new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Light_Dialog))
					.setMessage(R.string.update_availabe)
					.setPositiveButton(R.string.update_now, (dialog, which) -> {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tag-them/metoothanks/releases/latest"));
						context.startActivity(browserIntent);
					})
					.setNegativeButton(R.string.ignore, (dialog, which) -> dialog.dismiss())
					.create()
					.show();
			}
		}
	}
}
