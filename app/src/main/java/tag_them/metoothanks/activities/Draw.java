package tag_them.metoothanks.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import tag_them.metoothanks.CanvasView;
import tag_them.metoothanks.R;
import tag_them.metoothanks.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import static tag_them.metoothanks.Utils.openTextInputDialog;
import static tag_them.metoothanks.Utils.snackbar;

public class Draw extends AppCompatActivity {
	private static final int OPEN_IMAGE_REQUEST_CODE = 0,
			EXPORT_IMAGE_REQUEST_CODE = 1,
			OPEN_SETTINGS_REQUEST_CODE = 2;
	private static final String TAG = "MEDIA";
	
	private CanvasView canvasView;
	
	private Toolbar itemToolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_drawing);
		
		canvasView = (CanvasView) findViewById(R.id.canvas);
		itemToolbar = (Toolbar) findViewById(R.id.item_toolbar);
		canvasView.setItemToolbar(itemToolbar);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		new Utils.VersionChecker(this).execute("https://raw.githubusercontent.com/tag-them/metoothanks/master/app/latest_version");

//		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//		fab.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//					.setAction("Action", null).show();
//			}
//		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_drawing, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		switch (id) {
			case R.id.action_add_image:
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(intent, OPEN_IMAGE_REQUEST_CODE);
				return true;
			
			case R.id.action_add_text:
				final EditText editText = new EditText(this);
				openTextInputDialog(this, editText, (dialog, which) -> canvasView.addText(editText.getText().toString()));
				return true;

//			case R.id.action_settings:
//				intent = new Intent(this, Settings.class);
//				startActivity(intent);
//				return true;
			
			case R.id.action_export:
				if (checkExternalMedia())
					ActivityCompat.requestPermissions(this,
							new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							EXPORT_IMAGE_REQUEST_CODE);
				
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case EXPORT_IMAGE_REQUEST_CODE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					writeToSDFile();
					snackbar(canvasView, "Saved to /metoothanks");
				}
				
				break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case OPEN_IMAGE_REQUEST_CODE:
				try {
					if (data != null) {
						// create a bitmap from the image in the received URI
						Bitmap bitmap =
								MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
						canvasView.addImage(bitmap);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				break;
		}
	}
	
	private void writeToSDFile() {
		File root = android.os.Environment.getExternalStorageDirectory();
		
		// setting the filename to the time of creation
		DateFormat sdf = DateFormat.getDateTimeInstance();
		Date currentDate = new Date();
		String filename = sdf.format(currentDate) + ".jpeg";
		
		File dir = new File(root.getAbsolutePath() + "/metoothanks");
		dir.mkdirs();
		File file = new File(dir, filename);
		
		try {
			System.out.println(file.exists());
			FileOutputStream f = new FileOutputStream(file);
			// draw current canvas state to the file
			Bitmap bitmap = Bitmap.createBitmap(canvasView.getWidth(), canvasView.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvasView.prepareToExport(canvas);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, f);
			f.close();
			
			// add new image file to gallery
			MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
					(path, uri) -> {
						Log.i("ExternalStorage", "Scanned " + path + ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
					});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkExternalMedia() {
		boolean mExternalStorageAvailable;
		boolean mExternalStorageWritable;
		String state = Environment.getExternalStorageState();
		String message = "";
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// Can read and write the media
			mExternalStorageAvailable = mExternalStorageWritable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// Can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWritable = false;
			message = "External storage is available but not writeable :(";
		} else {
			// Can't read or write
			mExternalStorageAvailable = mExternalStorageWritable = false;
			message = "External storage isn't available :(";
		}
		
		if (!message.equals(""))
			snackbar(canvasView, message);
		
		return mExternalStorageAvailable && mExternalStorageWritable;
	}
	
	private boolean checkPermission() {
		boolean result = ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
		
		String message = "Permission to write external storage " + (result ? "granted" : "not granted!");
		Log.i(TAG, message);
		
		return result;
	}
}
