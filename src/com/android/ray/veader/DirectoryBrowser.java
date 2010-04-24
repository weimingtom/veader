package com.android.ray.veader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.android.lee.pdbreader.SoftwarePassionView.OrderAdapter;

//import com.totsp.androidexamples.MyApplication;
//import com.totsp.androidexamples.Main.SelectDataTask;

import android.app.AlertDialog;
import android.app.ListActivity;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.android.ray.veader.R;
import com.android.ray.veader.pdb.AbstractBookInfo;
import com.android.ray.veader.provider.BookColumn;
import com.android.ray.veader.util.Constatnts;

import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import android.view.View.OnClickListener;

public class DirectoryBrowser extends ListActivity {

	private class libraryAdapter extends ArrayAdapter<Library> {

		private ArrayList<Library> items;

		public libraryAdapter(Context context, int textViewResourceId,
				ArrayList<Library> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_library, null);
			}
			Library o = items.get(position);
			if (o != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText(o.getname());
				}
				if (bt != null) {
					bt.setText(o.getpath());
				}
			}
			return v;
		}
	}
	private List<String> items = null;
	String dbpath, dbname, dblocation;
	TextView textCurrentFolder;
	private MyApplication application;
	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Library> _library = null;

	private libraryAdapter m_adapter;
	private Library selectedLib;

	Thread thread;

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if (_library != null && _library.size() > 0) {
				m_adapter.notifyDataSetChanged();
				m_adapter.clear();
				for (int i = 0; i < _library.size(); i++)
					m_adapter.add(_library.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			System.out.println("File copied.");
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void debug(String msg) {

		new AlertDialog.Builder(this).setTitle("hello").setMessage(msg)
				.setIcon(0).setPositiveButton("OK", null).create().show();
	}

	protected void fnInitDB() {
		try {
			SQLiteDatabase myDB = null;

			myDB = this.openOrCreateDatabase(dbname, MODE_PRIVATE, null);

			dbpath = "/sdcard/reader/";

			dblocation = dbpath + dbname;
			myDB.beginTransaction();
			myDB
					.execSQL("CREATE TABLE IF NOT EXISTS catalog (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
							+ "path varchar, name varchar, description varchar, catagoryid varchar);");

			myDB
					.execSQL("CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
							+ "path varchar, description varchar, title varchar, size INT, pagecount int, authorid int, catalogid INT);");
			myDB
					.execSQL("CREATE TABLE IF NOT EXISTS author (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
							+ "name varchar, description varchar,  dob date, dod date);");
			myDB
					.execSQL("CREATE TABLE IF NOT EXISTS bookmark (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
							+ "name varchar, description varchar, bookid INT, pages int, dob date, dod date);");
			myDB.setTransactionSuccessful();
			myDB.endTransaction();
			this.copyfile("/data/data/com.android.lee.pdbreader/databases/"
					+ dbname, dblocation);

		} catch (SQLiteException e) {

			TextView textCurrentFolder = (TextView) findViewById(R.id.currentfolder);
			textCurrentFolder.setText(e.getMessage());

		}

	}

	private void InsertLibrary(final String... args) {

		String _msg;
		boolean executestatus, hasEbook;
		String path = args[0];
		hasEbook = false;
		boolean _pathExists = DirectoryBrowser.this.application.getDataHelper()
				.pathExists(path);
		String libTitle;
		libTitle = DirectoryBrowser.this.getResources().getString(
				R.string.library_create).toString();
		if (!_pathExists) {
			// this.dialog.setMessage("Creating New");
			File file = new File(path);
			ArrayList<Books> booklist = null;
			// Books book = null;
			int catid = this.application.getDataHelper()
			.getMaxLibID();
			for (File _f : file.listFiles()) {
				if (!_f.isDirectory()) {
					String _filename = _f.getName().toString().trim();
					if (Pattern.matches("(.*)([.]pdb)$", _filename)) {
						// book = new Books();
						

						// int catid= (catid.)?0:catid;

						hasEbook = true;
						SharedPreferences pref = getSharedPreferences(
								Constatnts.PREF_TAG, Context.MODE_PRIVATE);

						int charset = pref.getInt(Constatnts.DEFAULT_ENCODE, 0);
						AbstractBookInfo book = AbstractBookInfo.newBookInfo(
								_f, -1);
						book.setcatalogid(catid + 1);
						book.setDescription("");
						// book.setName(name)(_filename);
						book.setpath(_f.getPath());
						try {

							String encode = getResources().getStringArray(
									R.array.charset)[charset];
							// book.setEncode(encode);
							book.setEncode(encode);
							book.setFile(_f);

							ContentValues values = new ContentValues();
							// values.put(BookColumn.NAME, book.getname());
							values.put(BookColumn.NAME, book.getname());
							values.put(BookColumn.PATH, book.getpath());
							values.put(BookColumn.ENDCODE, encode);
							values.put(BookColumn.CATALOGID, book
									.getcatalogid());
							this.getContentResolver().insert(
									BookColumn.CONTENT_URI, values);
						} catch (IOException e) {
							Log.d("DirectoryBrowser", e.getMessage(), e);
							// skip
						}
						
					}
				}
			}
			if (hasEbook) {
				DirectoryBrowser.this.application.getDataHelper()
						.insertLibrary(args[0], args[1], args[2], (catid + 1));

				_msg = DirectoryBrowser.this.getResources().getString(
						R.string.library_added).toString();
				executestatus = true;
			} else {
				_msg = DirectoryBrowser.this.getResources().getString(
						R.string.library_nobook).toString();

			}
		} else {

			// _msg = "Unable to Create, Library Exists Already";
			_msg = DirectoryBrowser.this.getResources().getString(
					R.string.library_cannotcreate).toString();

			executestatus = false;

		}
		new AlertDialog.Builder(this).setTitle(libTitle).setMessage(_msg)
				.setIcon(0).setPositiveButton("OK", null).create().show();
	}

	private void listDir(File _file) {
		TextView textCurrentFolder = (TextView) findViewById(R.id.currentfolder);
		textCurrentFolder.setText(_file.toString());
		File[] files = _file.listFiles();
		boolean hasEbook = false;
		try {
			_library = new ArrayList<Library>();

			Library _lib, _lib2;
			_lib = new Library();
			_lib2 = new Library();
			_lib.setname(getString(R.string.goto_root));
			_lib.setpath("/sdcard");
			_library.add(_lib);

			if (_file.isDirectory()) {
				String parentPath = (_file.getParent() == null) ? "" : _file
						.getParent();
				// String s = "123abc456AbC789";
				_lib2.setname(getString(R.string.parentdir));
				_lib2.setpath(parentPath);
				_library.add(_lib2);
			}

			for (File file : files) {
				if (file.isDirectory()) {
					hasEbook = false;

					_lib = new Library();
					// Library _lib = new Library();
					_lib.setname(file.getName());
					_lib.setDescription(file.getName());
					_lib.setpath(file.getPath());
					_library.add(_lib);
				}
			}
			this.m_adapter = new libraryAdapter(this, R.layout.row_directory,
					_library);
			setListAdapter(this.m_adapter);

			Log.i("ARRAY", "" + _library.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		// runOnUiThread(returnRes);
	}

	public void onBackPressed() {
		try {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClassName(DirectoryBrowser.this, LibraryList.class
					.getName());
			intent.putExtras(bundle);
			startActivity(intent);
		} catch (Exception e) {
			this.debug(e.getMessage());

		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.directoryexplorer);
		dbname = getResources().getString(R.string.dbname).toString();
		textCurrentFolder = (TextView) findViewById(R.id.currentfolder);

		// fnInitDB();

		this.application = (MyApplication) this.getApplication();
		try {

			listDir(new File("/sdcard/"));

		} catch (Exception e) {

			textCurrentFolder.setText(e.getMessage().toString());

		}
		final Button btnSelect = (Button) findViewById(R.id.btnSelect);

		btnSelect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				InsertLibrary(selectedLib.getpath(), selectedLib
						.getDescription(), selectedLib.getname());
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int selectedRow = (int) id;
		if (selectedRow == 0) {

			listDir(new File("/sdcard/"));
		} else {
			this.selectedLib = _library.get(selectedRow);
			final File file = new File(_library.get(selectedRow).getpath());
			// new AlertDialog.Builder(this)
			// .setTitle("Ãö©ó Android BMI")
			// .setMessage(_library.get(selectedRow).getpath())
			// .show();
			if (file.isDirectory()) {

				listDir(file);

			} else {
				// new AlertDialog.Builder(FileList.this)
				// .setTitle("This file is not a directory")
				// .setNeutralButton("OK", new
				// DialogInterface.OnClickListener(){
				// public void onClick(DialogInterface dialog, int button){
				// //do nothing
				// }
				// })
				// .show();
			}
		}
	}

	/*private class InsertDataTask extends AsyncTask<String, Void, Void> {
		private final ProgressDialog dialog = new ProgressDialog(
				DirectoryBrowser.this);
		private String msg;
		boolean executeStatus;

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Creating library...");
			this.dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		protected Void doInBackground(final String... args) {
			String path = args[0];
			if (this.dialog.isShowing())
				this.dialog.dismiss();
			this.dialog.setMessage(args[0]);
			try {
				boolean _pathExists = DirectoryBrowser.this.application
						.getDataHelper().pathExists(path);
				String xx = (_pathExists) ? "true" : "false";
				this.dialog.setMessage(xx);
				if (!_pathExists) {
					// this.dialog.setMessage("Creating New");
					DirectoryBrowser.this.application.getDataHelper()
							.insertLibrary(args[0], args[1], "");
				} else {

					msg = DirectoryBrowser.this.getResources().getString(
							R.string.library_cannotcreate).toString();
					executeStatus = false;

				}

				
				 * else{ new AlertDialog.Builder(DirectoryBrowser.this)
				 * .setTitle("already added") .setNeutralButton("OK", new
				 * DialogInterface.OnClickListener(){ public void
				 * onClick(DialogInterface dialog, int button){
				 * 
				 * } }) .show();}
				 

			} catch (Exception e) {
				this.dialog.setMessage(e.getMessage());

			}
			return null;
		}

		// can use UI thread here
		protected void onPostExecute(final Void unused) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			// reset the output view by retrieving the new data
			// (note, this is a naive example, in the real world it might make
			// sense
			// to have a cache of the data and just append to what is already
			// there, or such
			// in order to cut down on expensive database operations)
			// new SelectDataTask().execute();
		}
	}*/

}