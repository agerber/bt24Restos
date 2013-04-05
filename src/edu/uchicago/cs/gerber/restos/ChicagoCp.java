package edu.uchicago.cs.gerber.restos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

//generic template class for ContentProvider by Adam Gerber and Dylan Hall, University of Chicago
public class ChicagoCp extends ContentProvider {


	static Map<String,String> mapCols = new HashMap<String,String>(); //DO-NOT MODIFY
	//********************************
	//START MODIFY: You are required to modify and maintain the following
	//********************************
	//IMPORTANT: ATTEMPT TO UPGRADE THE DATABASE_VERSION if your app does not appear to be saving records. 
	//increment the DATABASE_VERSION if you change the schema OR if you want to clear all records when testing
	private static final int DATABASE_VERSION = 24;
	
	//the AUTHORITY must be the same as the one you define in your manifest. Example below
	    //<provider
		    //android:name=".ChicagoContentProvider"
		    //android:authorities="edu.uchicago.cs.gerber" >
	    //</provider>
	private static final String AUTHORITY = "edu.uchicago.cs.gerber";
	
	//if I were to publish more than one CP, I could differentiate them with the base_path
	//here I'm just using the restaurants. 
	private static final String BASE_PATH = "restaurants";

	public static class Db extends SQLiteOpenHelper {

		//this sql string is built in the static context
		private static String strCreate;  //DO-NOT MODIFY
		//database name: this is the name of the sqlite db. It must have the .db extension. 
		private static final String DATABASE_NAME = "restos.db";
		//this database has only one table,  called resto.
		public static final String TABLE_NAME = "resto";
		
		//column names: you may modify all variable names and values, except COLUMN_ID and "_id" should remain as they are
		//to access these from another class, do it like so:  ChicagoContentProvider.DbHleper.COLUMN_CITY
		public static final String COLUMN_ID = "_id"; //DO NOT MODIFY; this should be called _id, this is a convention in android
		public static final String COLUMN_REST_NAME = "name";
		public static final String COLUMN_CITY = "city";
		public static final String COLUMN_PHONE = "phone";
		public static final String COLUMN_NOTE = "note";
		

		//you must maintain this block to be parellel with the above fields
		//do not change the first line:  mapCols.put(COLUMN_ID, "integer primary key autoincrement");
		//modify the rest to conform the the type
		//for a good discussion of sqlite types, see http://www.sqlite.org/datatype3.html
		static {
			mapCols.put(COLUMN_ID, "integer primary key autoincrement"); //do not modify
			mapCols.put(COLUMN_REST_NAME, "text not null"); 
			mapCols.put(COLUMN_CITY, "text not null");
			mapCols.put(COLUMN_PHONE, "text not null");
			mapCols.put(COLUMN_NOTE, "text not null");
		
		}

		//********************************
		//END MODIFY. The following code (in this inner class and its outer class) should not be modified. 
		//********************************
		
		static {
			strCreate = "create table " + TABLE_NAME + "(";
			for (String str : mapCols.keySet()) {
				strCreate += str +" " +  mapCols.get(str).toString() + ", ";
			}
			//strip the last comma and space
			strCreate = strCreate.substring(0, strCreate.length() -2);
			strCreate += ");";
			
		}
		

		//constructor
		public Db(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
		
			database.execSQL(strCreate);
		}

		//this fires if you increment the DATABASE_VERSION
		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion,
				int newVersion) {

			database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(database);
		}

		
	} //end inner class
	

	//all restos id
	private static final int RESTOS = 10;
	//one resto id
	private static final int RESTO_ID = 20;

	// database helper
	private Db hlpDatabase;

	//this what other apps use content://edu.uhcicago.cs.gerber/restaurants  
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);


	//********************************
	//this defines the kinds of URI patterns that this CP can take 
	//********************************
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, RESTOS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", RESTO_ID);
	}
	
	@Override
	public boolean onCreate() {
		//instantiate the helper on onCreate
		hlpDatabase = new Db(getContext());
		return false;
		
	}


	//********************************
	//CRUD (Create, Read, Update, and Delete) operations are abstracted into the following methods
	//********************************
	
	//CREATE
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//notice the use of ContentValues -- this is a shuttle that carries key/value pairs
		
		int nType = sURIMatcher.match(uri);
		//uses the dtb directly
		SQLiteDatabase dtb = hlpDatabase.getWritableDatabase();
	
		long lNumId = 0;
		switch (nType) {
			//it must insert the records in the db, not a single record
			case RESTOS:
				lNumId = dtb.insert(ChicagoCp.Db.TABLE_NAME, null, values);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		//return the Uri with the row_id of the newly created row
		return Uri.parse(BASE_PATH + "/" + lNumId);
	}
	
	
	//READ
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder to build an SQL string
		SQLiteQueryBuilder sqbBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		sqbBuilder.setTables(ChicagoCp.Db.TABLE_NAME);

		int nType = sURIMatcher.match(uri);
		switch (nType) {
			case RESTOS:
				break;
			case RESTO_ID:
				// Adding the ID to the original query
				sqbBuilder.appendWhere(ChicagoCp.Db.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase dtb = hlpDatabase.getWritableDatabase();
		Cursor cur = sqbBuilder.query(dtb, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cur.setNotificationUri(getContext().getContentResolver(), uri);

		return cur;
	}
	
	//UPDATE
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int nType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = hlpDatabase.getWritableDatabase();
		int nRowsUpdated = 0;
		switch (nType) {
			case RESTOS:
				nRowsUpdated = sqlDB.update(ChicagoCp.Db.TABLE_NAME, 
						values, 
						selection,
						selectionArgs);
				break;
			case RESTO_ID:
				String strId = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					nRowsUpdated = sqlDB.update(ChicagoCp.Db.TABLE_NAME, 
							values,
							ChicagoCp.Db.COLUMN_ID + "=" + strId, 
							null);
				} else {
					nRowsUpdated = sqlDB.update(ChicagoCp.Db.TABLE_NAME, 
							values,
							ChicagoCp.Db.COLUMN_ID + "=" + strId 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return nRowsUpdated;
	}

	//DELETE
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int nType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = hlpDatabase.getWritableDatabase();
		int nRowsDeleted = 0;
		switch (nType) {
			case RESTOS:
				nRowsDeleted = sqlDB.delete(ChicagoCp.Db.TABLE_NAME, selection,
						selectionArgs);
				break;
			case RESTO_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					nRowsDeleted = sqlDB.delete(ChicagoCp.Db.TABLE_NAME,
							ChicagoCp.Db.COLUMN_ID + "=" + id, 
							null);
				} else {
					nRowsDeleted = sqlDB.delete(ChicagoCp.Db.TABLE_NAME,
							ChicagoCp.Db.COLUMN_ID + "=" + id 
							+ " and " + selection,
							selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return nRowsDeleted;
	}


	//make sure that the columns they're questing in the projection actually exist in the table
	private void checkColumns(String[] projection) {
		
		String[] strAvailables = new String[mapCols.size()];
		
		int nC = 0;
		for (String str : mapCols.keySet()) 
			strAvailables[nC++] = str;

		
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(strAvailables));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}
	

} 