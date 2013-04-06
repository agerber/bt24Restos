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

public class NewActivity extends Activity  {

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
	}
		
}