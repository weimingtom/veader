package org.ray.veader;

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

import org.ray.veader.R;
import org.ray.veader.pdb.AbstractBookInfo;
import org.ray.veader.provider.BookColumn;
import org.ray.veader.util.Constatnts;

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
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;



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
	TextView txtbooksfound;
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
//private String strRegexFileType="(.*)([.]pdb?|PDB?|txt?|TXT?)$";
private String strRegexFileType="(.*)([.]pdb?|PDB?)$";
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

			int catid = this.application.getDataHelper().getMaxLibID();
			for (File _f : file.listFiles()) {
				if (!_f.isDirectory()) {
					String _filename = _f.getName().toString().trim();
					//if (Pattern.matches("(.*)([.]pdb?|PDB?|txt?|TXT?)$", _filename)) {
						if (Pattern.matches(strRegexFileType, _filename)) {
							
						
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

	private int listDir(File _file) {
		TextView textCurrentFolder = (TextView) findViewById(R.id.currentfolder);
		textCurrentFolder.setText(_file.toString());
		File[] files = _file.listFiles();
		boolean hasEbook = false;
		int pdbcount = 0;
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
				if (Pattern.matches(strRegexFileType, file.getName()
						.trim())) {
					pdbcount++;
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
		txtbooksfound = (TextView) findViewById(R.id.txtbooksfound);
		txtbooksfound.setText(getString(R.string.booksfound)+String.valueOf(pdbcount)  );
		return pdbcount;
	}

	public void onBackPressed() {
		try {
			/*
			 * Intent intent = new Intent(); Bundle bundle = new Bundle();
			 * intent.setClassName(DirectoryBrowser.this, LibraryList.class
			 * .getName()); intent.putExtras(bundle); startActivity(intent);
			 */

			// int selectedRow = (int) id;
			// Log.d("selectedID", String.valueOf(id));
			Bundle bdlChapter = new Bundle();
			bdlChapter.putBoolean("ACTADDLIB", true);
			Intent i = new Intent();

			i.putExtras(bdlChapter);
			this.setResult(RESULT_OK, i);
			finish();
		} catch (Exception e) {
			this.debug(e.getMessage());

		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
txtbooksfound = (TextView) findViewById(R.id.txtbooksfound);
		setContentView(R.layout.directoryexplorer);
		dbname = getResources().getString(R.string.dbname).toString();
	
		// fnInitDB();
		int bookcount = 0;
		
		this.application = (MyApplication) this.getApplication();
		//try {

			listDir(new File("/sdcard/"));

		
		final Button btnSelect = (Button) findViewById(R.id.btnSelect);
	
		//txtbooksfound.setText(String.valueOf(bookcount) + "books found");
		btnSelect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				InsertLibrary(selectedLib.getpath(), selectedLib
						.getDescription(), selectedLib.getname());
			}
		});
		textCurrentFolder = (TextView) findViewById(R.id.currentfolder);
		int intFontColor = Color.rgb(105, 60, 44);
		txtbooksfound.setTextColor(intFontColor);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int selectedRow = (int) id;
		if (selectedRow == 0) {

			listDir(new File("/sdcard/"));
		} else {
			this.selectedLib = _library.get(selectedRow);
			final File file = new File(_library.get(selectedRow).getpath());

			if (file.isDirectory()) {

				listDir(file);

			} else {

			}
		}
	}

}