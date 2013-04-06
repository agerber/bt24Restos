package edu.uchicago.cs.gerber.restos;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

// this code-base was loosely adapted from http://www.vogella.com/articles/AndroidSQLite/article.html
//********************************
//This is the Main class
//********************************
public class MainList extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static String TAG = "MainList";
	// some constants that you may use for the startActivityForResult -
	// currently we're only using NEW_RESTO_ACTION
	public static final int NEW_RESTO_ACTION = 1000;
	public static final int EDIT_RESTO_ACTION = 1001; // you can use this when
														// you fire up the
														// EditAcivity

	// used for the ListView of this class
	private SimpleCursorAdapter scaAdapter;
	private SharedPreferences shpPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_list);
		this.getListView().setDividerHeight(2);
		shpPref = PreferenceManager.getDefaultSharedPreferences(this);
		fillAdapter();
		// this makes the listView of this ListActity responsive to optionsMenu
		registerForContextMenu(getListView());

	}

	// ********************
	// this onResume purpose is to update all style setting from preferences
	// color, format and others.
	// ********************
	@Override
	protected void onResume() {
		setListAdapter(scaAdapter);
		//scaAdapter.setViewBinder(VIEW_BINDER);
		super.onResume();
	}

	// since we called registerForContextMenu(getListView()) above, we can use
	// this now
	@Override
	protected void onListItemClick(ListView lsv, View vwx, int position, long id) {

		// when you click on any of the items in the listview, it passes control
		// to the context menu
		lsv.showContextMenuForChild(vwx);

	}

	
	// ********************************
	// The following two methods create the actionbar
	// since I've set my property android:showAsAction="always" in my xml
	// I see these menu items in the actionbar
	// ********************************
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_action:
			fireNewRestoActivity();
			return true;
		case R.id.prefs_action:
			firePrefActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// ********************************
	// The following two methods are used for the context Menu
	// ********************************
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater mni = getMenuInflater();
		mni.inflate(R.menu.conmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.conCall:
			callingRestaurant(item);
			break;
		case R.id.conWeb:
			// https://www.google.com/search?q=phoenix+restaurant+chicago&btnI=Im+Feeling+Lucky
			//openSelectedWebSearch(item);
			
			break;
			
		case R.id.conMap:
			//openSelectedMapSearch(item);
			break;
		case R.id.conEdit:
			// create an EditActivty and call it using startActivityForResult()
			// similar to NewActivity
			fireEditRestoActivity(item);
			// Toast.makeText(this, "view/edit", Toast.LENGTH_SHORT).show();
			break;
		case R.id.conDelete:
		//	deleteRestaurant(item);
			break;
		default:
			break;
		}
		return true;
	}
	// ********************************
	// go to calling intent and call number
	// ********************************
	private void callingRestaurant(final MenuItem item) {
		String[] strArrColumnPhone = { ChicagoCp.Db.COLUMN_PHONE };
		// request that the content resolver to call query and get Cursor
		// Result
		Cursor cResult = getDBCursorFromItem(item, strArrColumnPhone);
		try {
			Intent itn = new Intent(Intent.ACTION_CALL);
			itn.setData(Uri.parse(String.format("tel:%s", cResult.getString(0))));
			if (cResult.getString(0).equals(""))
				throw new ActivityNotFoundException();
			else
				startActivity(itn);
		} catch (ActivityNotFoundException e) {
			// catch just in case application caller fail
			Toast.makeText(this, "Call fail, or blank number",
					Toast.LENGTH_SHORT).show();
		}
		
	}





	private void firePrefActivity() {
		Intent intent = new Intent(this, FragPrefsActivity.class);
		startActivity(intent);
	}
	// ********************************
	//	Fire up New activity for user to enter new restaurant
	// ********************************
	private void fireNewRestoActivity() {
		Intent itn = new Intent(this, NewActivity.class);
		itn.putExtra("requestCode", NEW_RESTO_ACTION);
		// here were're passing in a tag of NEW_RESTO_ACTION so that when
		// onActivityResult is called
		// we will have the request_code
		startActivityForResult(itn, NEW_RESTO_ACTION);
	}
	// ********************************
	//	Fire up New activity for user to edit current restaurant
	// ********************************
	private void fireEditRestoActivity(final MenuItem item) {
		Intent itn = new Intent(this, NewActivity.class);
		// get all data of selected item put in intent's Extras
		// to display in NewActivity.
		String[] strArrColumn = { ChicagoCp.Db.COLUMN_ID,
				ChicagoCp.Db.COLUMN_REST_NAME, ChicagoCp.Db.COLUMN_CITY,
				ChicagoCp.Db.COLUMN_PHONE, ChicagoCp.Db.COLUMN_NOTE };
		Cursor cResult = getDBCursorFromItem(item, strArrColumn);
		itn.putExtra("requestCode", EDIT_RESTO_ACTION);
		itn.putExtra("id", cResult.getString(0));
		itn.putExtra("name", cResult.getString(1));
		itn.putExtra("city", cResult.getString(2));
		itn.putExtra("phone", cResult.getString(3));
		itn.putExtra("note", cResult.getString(4));
	
		// here were're passing in a tag of NEW_RESTO_ACTION so that when
		// onActivityResult is called
		// we will have the request_code

		startActivityForResult(itn, EDIT_RESTO_ACTION);
	}
	// ********************************
	//	get Cursor with column of strArrColumn  from selected item
	// ********************************
	private Cursor getDBCursorFromItem(MenuItem item, String[] strArrColumn) {
		AdapterContextMenuInfo acmInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		// reform the Uri with the id
		Uri uri = Uri.parse(ChicagoCp.CONTENT_URI + "/" + acmInfo.id);
		String[] strArrSelectArg = { Long.toString(acmInfo.id) };
		// request that the content resolver to call query and get Cursor Result
		Cursor cResult = getContentResolver().query(uri, strArrColumn,
				ChicagoCp.Db.COLUMN_ID + "=?", strArrSelectArg, null);
		// Check whether the result is 0 or not.
		// if not, move the cursor to next row and get data
		cResult.moveToNext();
		return cResult;
	}

	// ********************************
	// fillApater uses both an adapter and a loader
	// ********************************
	private void fillAdapter() {
		// use a cursor loader to manage this
		// notice that this class implements
		// LoaderManager.LoaderCallbacks<Cursor>
		// param1 specific tag_id,param2 any params you want to pass in, and
		// param3 who to call back
		getLoaderManager().initLoader(0, null, this);
		// these are the db fields from which we get the data
		String[] strSources = new String[] { ChicagoCp.Db.COLUMN_REST_NAME,
				ChicagoCp.Db.COLUMN_CITY, ChicagoCp.Db.COLUMN_PHONE };
		// these are ui Fields to which the data will be set (notice one-to-one
		// mapping)
		int[] nTargets = new int[] { R.id.list_restaurant_name,
				R.id.list_restaurant_city, R.id.list_restaurant_phone };

		// params of SimpleCursorAdapter constructor: Context context, int
		// layout, Cursor c, String[] from, int[] to
		scaAdapter = new SimpleCursorAdapter(
		// context -- this ListActivity
				this,
				// target list item layout (the ui component encapsulating the
				// data fields)
				R.layout.row,
				// the cursor may be null as we're using the cursor loader to
				// managed this
				null,
				// source fields (as strings) from the db
				strSources,
				// target fields (as int resource ids) to the ui
				nTargets, 0);

		// call the setListAdapter to bind the data
		setListAdapter(scaAdapter);
		//scaAdapter.setViewBinder(VIEW_BINDER);
	}

	
	// some deprecated techniques
	// Cursor cur = getContentResolver().query(CONTENT_URI, null, null, null,
	// null);
	// Cursor cur = managedQuery(CONTENT_URI, null, null, null, null);

	// ********************************
	// The following three methods are used for the Loader
	// This is the cursor loader; it's asyncrhonous so you won't get a ANR error
	// if you list is huge
	// The loader manages the cursor so you don't have to!
	// Creates a new loader after the initLoader () call
	// ********************************

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] strProjections = { ChicagoCp.Db.COLUMN_ID,
				ChicagoCp.Db.COLUMN_REST_NAME, ChicagoCp.Db.COLUMN_CITY,
				ChicagoCp.Db.COLUMN_PHONE };
		CursorLoader cursorLoader = new CursorLoader(this,
				ChicagoCp.CONTENT_URI, strProjections, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		scaAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		scaAdapter.swapCursor(null);
	}

	// ********************************
	// Called when a returning activity is finished
	// requestCode was the original request code send to the activity
	// resultCode is the return code, RESULT_OK means everything is cool.
	// ********************************

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		
		//you can use this to handle cancelled or aborted attempts. 
		
		
		// if -1, it's ok RESULT_OK, and 0 if RESULT_CANCELLED or aborted
		// somehow.
		// you don't have to make use if this data, but you may
		switch (requestCode) {
		case NEW_RESTO_ACTION:
			if (resultCode != RESULT_OK){
				 Log.i(TAG, "new resto aborted or cancelled.");
			}
			
			break;
		case EDIT_RESTO_ACTION:
			if (resultCode != RESULT_OK){
				 Log.i(TAG, "edit resto aborted or cancelled.");
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, intent);
		// if you need to pass data back to this activity
	}
}