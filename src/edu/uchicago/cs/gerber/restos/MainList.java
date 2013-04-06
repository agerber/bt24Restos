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
public class MainList extends ListActivity {

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
	}

}