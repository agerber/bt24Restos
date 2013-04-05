package edu.uchicago.cs.gerber.restos;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewActivity extends Activity implements OnClickListener {

	// used to insert a record into the db via the content-provider
	ContentResolver cnrResolver;
	Cursor cur;
	Intent itnCaller; // to keep intent's caller
	// refs to our Views
	EditText edtName, edtCity, edtPhone, edtNote;
	boolean bNotesFlag = false;
	//ImageView imvImage;
	Uri uriImage;
	Bitmap bitmap;
	String strPath;
	Button btnSubmit, btnCancel;
	TextView txtName, txtCity, txtPhone, txtNote;
	int nRequestCode; // to keep a request code from caller
	public static final int RESULT_CANCELLED = 0;
	private static final int REQUEST_CODE_IMAGE = 9000;
	private static final int REQUEST_CODE_CAM = 9001;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		// return a cursor to my db using the CP
		cnrResolver = getContentResolver();
		cur = getContentResolver().query(ChicagoCp.CONTENT_URI, null,
				null, null, null);
		itnCaller = getIntent();
		
		// assign objects to references
		txtName = (TextView) findViewById(R.id.txtName);
		txtCity = (TextView) findViewById(R.id.txtCity);
		txtPhone = (TextView) findViewById(R.id.txtPhone);
		txtNote = (TextView) findViewById(R.id.txtNote);
		
		
		edtName = (EditText) findViewById(R.id.edtName);
		edtCity = (EditText) findViewById(R.id.edtCity);
		edtPhone = (EditText) findViewById(R.id.edtPhone);
		edtNote = (EditText) findViewById(R.id.edtNote);
		//imvImage = (ImageView) findViewById(R.id.imageView1);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		//edtNote.setOnFocusChangeListener(this);
		// Get request code,if the code soesn't exist, NEW_RESTO_ACTION is
		// default
		nRequestCode = itnCaller.getIntExtra("requestCode",
				MainList.NEW_RESTO_ACTION);
		// initialize strPath to blank string
		// if no picture selected, update or insert path will be blank
		strPath = "";
		// assign behavior to onClick()
		btnSubmit.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		

		
		//registerForContextMenu(imvImage);
		// If the requestCode is EDIT_NEW_RESTO, we need to get values to
		// display.
		// During Submit, we also need to Update instead of add a new
		// Restaurant
		if (nRequestCode == MainList.EDIT_RESTO_ACTION) {
			edtName.setText(itnCaller.getStringExtra("name"));
			edtCity.setText(itnCaller.getStringExtra("city"));
			edtPhone.setText(itnCaller.getStringExtra("phone"));
			edtNote.setText(itnCaller.getStringExtra("note"));
			// set up picture on image view if exist
			//showImageFromPath(imvImage, itnCaller.getStringExtra("path"));
		}

	}
	
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	    if (keyCode == KeyEvent.KEYCODE_BACK) {
//	        moveTaskToBack(true);
//	        return true;
//	    }
//	    return super.onKeyDown(keyCode, event);
//	}
//	
//	@Override
//	public void onFocusChange(View v, boolean hasFocus) {
//		if(hasFocus){
//			makeTopViewsGone();
//			//bNotesFlag = true;
//			
//		} else {
//			
//			//makeTopViewsVisible();
//			//bNotesFlag = false;
//		}
//		
//	}
	
//	@Override
//	public boolean onKey(View v, int keyCode, KeyEvent event) {
//		if ((keyCode == KeyEvent.KEYCODE_BACK && bNotesFlag)) {
//	
//			//hideSoftKeyboard(this);
//			return true;
//		} else {
//			return super.onKeyDown(keyCode, event);
//		} 
//		
//	}
//	public static void hideSoftKeyboard(Activity activity) {
//	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
//	}
//	
//	private void makeTopViewsGone(){
//		
//		txtName.setVisibility(View.GONE);
//		txtCity.setVisibility(View.GONE);
//		txtPhone.setVisibility(View.GONE);
//		
//		edtName.setVisibility(View.GONE);
//		edtCity.setVisibility(View.GONE);
//		edtPhone.setVisibility(View.GONE);
//
//	}
//	
//	private void makeTopViewsVisible(){
//		
//		txtName.setVisibility(View.VISIBLE);
//		txtCity.setVisibility(View.VISIBLE);
//		txtPhone.setVisibility(View.VISIBLE);
//		
//		edtName.setVisibility(View.VISIBLE);
//		edtCity.setVisibility(View.VISIBLE);
//		edtPhone.setVisibility(View.VISIBLE);
//		
//	}

	// Accept string-uri path and imageView, to setup picture.
	// if the uri are empty string, do nothing.
	// if there are some error, set the picture to default image
	// and display it.
	// if successful, update image.
	// ********************************
	//	Image cannot be taken without external storage
	// ********************************
//	private void showImageFromPath(ImageView imv, String strUri) {
//		if (strUri.equals("")) {
//			return;
//		} else {
//			try {
//				strPath = strUri;
//				uriImage = Uri.parse(strUri);
//				InputStream stream = getContentResolver().openInputStream(
//						uriImage);
//				bitmap = BitmapFactory.decodeStream(stream);
//				stream.close();
//				//imvImage.setImageBitmap(bitmap);
//
//			} catch (Exception e) {
//				// There can be many exception here, filenotfoundexception
//				// exception about bitmap and input stream
//				// just catch and print the exception
//				Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
//			}
//		}
//	}

	// this uses ContentValues as a shuttle to pass data to the ContentResolver
	// which passing data through the CP to the db
	// try to add a record, if the name field is blank, return false; otherwise
	// add record to db and return true
	private boolean addRecord() {
		if (!edtName.getText().toString().equals("")) {
			ContentValues cnv = new ContentValues();
			cnv.put(ChicagoCp.Db.COLUMN_REST_NAME, edtName.getText().toString());
			cnv.put(ChicagoCp.Db.COLUMN_CITY, edtCity.getText().toString());
			cnv.put(ChicagoCp.Db.COLUMN_PHONE, edtPhone.getText().toString());
			cnv.put(ChicagoCp.Db.COLUMN_NOTE, edtNote.getText().toString());
			//cnv.put(ChicagoCp.Db.COLUMN_PICPATH, strPath);
			// if requested code equals EDIT_RESTO_ACTION, update instead of
			// adding new record
			if (nRequestCode == MainList.EDIT_RESTO_ACTION) {
				String[] strArrTemp = { itnCaller.getStringExtra("id") };
				cnrResolver.update(ChicagoCp.CONTENT_URI, cnv,
						ChicagoCp.Db.COLUMN_ID + "=?", strArrTemp);
			} else {
				cnrResolver.insert(ChicagoCp.CONTENT_URI, cnv);
			}
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void onClick(View v) {

		// saveContent();
		switch (v.getId()) {
		case R.id.btnSubmit:
			if (addRecord()) {
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(this, "You need to fill in the name field",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btnCancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		default:

			break;
		}
	}

	// ********************************
	// The following two methods are used for the context Menu
	// User can choose between select from file and take a picture from camera
	// ********************************
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//		// TODO Auto-generated method stub
//		super.onCreateContextMenu(menu, v, menuInfo);
//		MenuInflater mni = getMenuInflater();
//		mni.inflate(R.menu.imagemenu, menu);
//	}

//	@Override
//	public boolean onContextItemSelected(final MenuItem item) {
//		// TODO Auto-generated method stub
//		switch (item.getItemId()) {
//		case R.id.conImageFile:
//			// callpickimage method, which will call another
//			// intent of gallery.
//			pickImage();
//			break;
//		case R.id.conImageCam:
//			takeImage();
//			break;
//		default:
//			break;
//		}
//		return true;
//	}

	// This method will call Intent to view GALLERY of the phone
	// for user to pick picture
	// Picking image code is ADAPTED from
	// **http://viralpatel.net/blogs/pick-image-from-galary-android-app/
//	public void pickImage() {
//		Intent intent = new Intent();
//		intent.setType("image/*");
//		intent.setAction(Intent.ACTION_GET_CONTENT);
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//		startActivityForResult(intent, REQUEST_CODE_IMAGE);
//	}
//
//	//this takes the picture
//	public void takeImage() {
//		Intent intent = new Intent(this, CameraActivity.class);
//		PhotoHandler phhHandler = new PhotoHandler(getApplicationContext());
//		intent.putExtra("picpath", "");
//		startActivityForResult(intent, REQUEST_CODE_CAM);
//	}






	// After select image from method pickImage(), this onactivityResult
	// will be called. this will update imvImage to selected image
	// from gallery intent
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == REQUEST_CODE_IMAGE
//				&& resultCode == Activity.RESULT_OK) {
//			try {
//				// We need to recyle unused bitmaps
//				if (bitmap != null) {
//					bitmap.recycle();
//				}
//				strPath = data.getData().toString();
//				InputStream stream = getContentResolver().openInputStream(
//						data.getData());
//				bitmap = BitmapFactory.decodeStream(stream);
//				stream.close();
//				// This uses in scaling large picture to 640*480
//				// I set this value according to my phone and emulator
//
//				bitmap = bitmap.createScaledBitmap(bitmap, 640, 480, true);
//				imvImage.setImageBitmap(bitmap);
//
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//				Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT)
//						.show();
//			} catch (IOException e) {
//				e.printStackTrace();
//				Toast.makeText(this, "I/O Exception", Toast.LENGTH_SHORT)
//						.show();
//			}
//		} else if (requestCode == REQUEST_CODE_CAM) {
//			// If resultcode is RESULT_OK, it means that
//			// User has taken a photo in CameraActivity
//			// Let User pick one from the result
//			if (resultCode == RESULT_OK) {
//				//This block will refresh Gallery by calling media Scanner
//				//http://yeblon.com/programmatically-refresh-android-gallery-after-storingdeleting-or-updating-a-photo
//				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//						Uri.parse("file://"
//								+ Environment.getExternalStorageDirectory())));
//				//Let User choose from all picture he/she took in camera activity
//				pickImage();
//			}
//
//		}
//	}
}