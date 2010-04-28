package com.android.ray.veader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//import com.android.lee.pdbreader.SoftwarePassionView.OrderAdapter;
import com.android.ray.veader.R;

import com.android.ray.veader.SimpleGestureFilter.SimpleGestureListener;

import com.android.ray.veader.provider.BookColumn;
import com.android.ray.veader.provider.BookmarkColumn;
import com.android.ray.veader.provider.CatalogColumn;
import com.android.ray.veader.util.libraryManager;

//import com.totsp.androidexamples.MyApplication;
//import com.totsp.androidexamples.Main.SelectDataTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView.OnItemSelectedListener;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class LibraryList extends ListActivity  {

	public class _cursorAdapter extends CursorAdapter {

		protected TextView tt, bt, txtRemark;
		protected ImageView viewIcon;
		protected View v;

		public _cursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			View v = view;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_library, null);
			}

			tt = (TextView) v.findViewById(R.id.toptext);
			bt = (TextView) v.findViewById(R.id.bottomtext);
			txtRemark = (TextView) v.findViewById(R.id.txtRemark);
			viewIcon = (ImageView) v.findViewById(R.id.rowicon);

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.row_library, parent, false);

			return v;
		}
	}

	public class bookmarkCursorAdapter extends CursorAdapter {
		protected TextView tt, bt, txtRemark;
		protected ImageView viewIcon;
		protected View v;

		public bookmarkCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
		}

		public void bindView(View view, Context context, Cursor cursor) {
			
		
			// application.getDataHelper().getBookCountByCatalogid(cursor.getInt(cursor.getColumnIndex(CatalogColumn._ID)));
			View v = view;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_library, null);
			}
	
			v.setBackgroundResource(R.drawable.bgbrown);
			tt = (TextView) v.findViewById(R.id.toptext);
			bt = (TextView) v.findViewById(R.id.bottomtext);
			int intFontColor = Color.rgb(240, 255, 217);
			bt.setTextColor(intFontColor);
			txtRemark = (TextView) v.findViewById(R.id.txtRemark);
			viewIcon = (ImageView) v.findViewById(R.id.rowicon);
			String bookmarktitle = cursor.getString(cursor
					.getColumnIndex(BookmarkColumn.NAME));
			int strChapter = cursor.getInt(cursor
					.getColumnIndex(BookmarkColumn.CHAPTER));
			String strChapterTitle = cursor.getString((cursor
					.getColumnIndex(BookmarkColumn.CHAPTERTITLE)));
			int pageno = cursor.getInt(cursor
					.getColumnIndex(BookmarkColumn.PAGE));
			int totalPage = cursor.getInt(cursor
					.getColumnIndex(BookmarkColumn.TOTALPAGE));

			float percent = (float) pageno / (float) totalPage;
			String createdate = cursor.getString((cursor
					.getColumnIndex(BookmarkColumn.CREATEDATE)));
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss"); // set the format to sql date time
			//Date date = new Date(createdate);
		
			String strpercent = "Chapter:"+String.valueOf(strChapter)+" (" +String.valueOf(Math.round((percent * 100)))
					+ "%)"+createdate;
			//String strRemark = strpercent + "-Create date:" + strDt;
			viewIcon.setImageResource(R.drawable.btn_bookmark);
			if (tt != null) {
				tt.setText(szfont(bookmarktitle, 8));
			}
			if (bt != null) {
				bt.setText(Html.fromHtml("<font size='7'>"+ strChapterTitle.trim()+"</font>"));
				bt.setTextSize(15);
			
			}
			if (strChapterTitle==null)strChapterTitle="";
			if (txtRemark != null)
				txtRemark.setText(szfont(strpercent,8));
			txtRemark.setTextSize(11);
			 intFontColor = Color.rgb(255, 255, 153);
			txtRemark.setTextColor(intFontColor);
			tt.setTextSize(12);
	
			

		}
private Spanned szfont(String str, int fontsize){
	
	return Html.fromHtml("<font style='font-size:"+String.valueOf(fontsize)+"px'>"+str+"</font>");
}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.row_library, parent, false);
			bindView(v, context, cursor);
			return v;

		}
	}

	public class bookCursorAdapter extends CursorAdapter {
		protected TextView tt, bt, txtRemark;
		protected ImageView viewIcon;
		protected View v;

		public bookCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
		}

		public void bindView(View view, Context context, Cursor cursor) {
			// super.bindView(view, context, cursor);

			// int bookscount =
			// application.getDataHelper().getBookCountByCatalogid(cursor.getInt(cursor.getColumnIndex(CatalogColumn._ID)));
			View v = view;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_library, null);
			}

			tt = (TextView) v.findViewById(R.id.toptext);
			bt = (TextView) v.findViewById(R.id.bottomtext);
			txtRemark = (TextView) v.findViewById(R.id.txtRemark);
			viewIcon = (ImageView) v.findViewById(R.id.rowicon);
			String libname = cursor.getString(cursor
					.getColumnIndex(CatalogColumn.NAME));
			String libdesc = cursor.getString(cursor
					.getColumnIndex(CatalogColumn.PATH));
			viewIcon.setImageResource(R.drawable.dvi);
			if (tt != null) {
				tt.setText(libname);
			}
			if (bt != null) {
				bt.setText(libdesc);
			}

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.row_library, parent, false);
			bindView(v, context, cursor);
			return v;

		}
	}

	/*
	 * private void getLibraries(File _file) { // TextView textCurrentFolder =
	 * (TextView) // findViewById(R.id.currentfolder); //
	 * textCurrentFolder.setText(_file.toString());
	 * 
	 * try { _library = new ArrayList<Library>(); _library =
	 * this.application.getDataHelper().getAll();
	 * 
	 * this.m_adapter = new libraryAdapter(this, R.layout.row_directory,
	 * _library); setListAdapter(this.m_adapter);
	 * 
	 * Log.i("ARRAY", "" + _library.size()); } catch (Exception e) {
	 * Log.e("BACKGROUND_PROC", e.getMessage()); } // runOnUiThread(returnRes);
	 * }
	 * 
	 * private void getBooks(int catalogid) {
	 * 
	 * try { _booklist = new ArrayList<Books>(); _booklist =
	 * this.application.getDataHelper().getBooksByCatalogID( catalogid);
	 * 
	 * this._bookAdapter = new BooksAdapter(this, R.layout.row_directory,
	 * _booklist); setListAdapter(this._bookAdapter);
	 * 
	 * Log.i("ARRAY", "" + _booklist.size()); } catch (Exception e) {
	 * 
	 * Log.e("BACKGROUND_PROC", e.getMessage()); // debug(e.getMessage()); } //
	 * runOnUiThread(returnRes); }
	 */
	public class LibCursorAdapter extends CursorAdapter {
		protected TextView tt, bt, txtRemark;
		protected ImageView viewIcon;
		protected View v;

		public LibCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
		}

		public void bindView(View view, Context context, Cursor cursor) {
			// super.bindView(view, context, cursor);
			View v = view;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_library, null);
			}

			tt = (TextView) v.findViewById(R.id.toptext);
			bt = (TextView) v.findViewById(R.id.bottomtext);
			txtRemark = (TextView) v.findViewById(R.id.txtRemark);
			viewIcon = (ImageView) v.findViewById(R.id.rowicon);
			int bookscount = application.getDataHelper()
					.getBookCountByCatalogid(
							cursor.getInt(cursor
									.getColumnIndex(CatalogColumn._ID)));

			String libname = cursor.getString(cursor
					.getColumnIndex(CatalogColumn.NAME));
			String libdesc = cursor.getString(cursor
					.getColumnIndex(CatalogColumn.PATH));
			if (tt != null) {
				tt.setText(libname);
			}
			if (bt != null) {
				bt.setText(libdesc);
			}
			txtRemark.setText("(" + Integer.toString(bookscount) + " books)");
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.row_library, parent, false);
			bindView(v, context, cursor);
			return v;

		}
	}

	private List<String> items = null;
	String dbpath, dbname, dblocation;
	TextView textCurrentFolder;
	private MyApplication application;
	private ProgressDialog m_ProgressDialog = null;

	// private ArrayList<Library> _library = null;
	// private ArrayList<Books> _booklist = null;
	// private libraryAdapter m_adapter;
	// private BooksAdapter _bookAdapter;
	private bookCursorAdapter _bookcursorAdapter;
	private LibCursorAdapter _libcursorAdapter;
	private bookmarkCursorAdapter _bookmarkcursorAdapter;
	Thread thread;
	private Cursor libCursor, bookCursor, bookmarkCursor;
	private SimpleGestureFilter filter;
	private GestureDetector gestureDetector;

	View.OnTouchListener gestureListener;

	private static String[] CatalogField = new String[] { CatalogColumn._ID,
			CatalogColumn.NAME, CatalogColumn.PATH, CatalogColumn.DESCRIPTION,
			CatalogColumn.CATAGORYID };

	private static final String[] BookField = new String[] { BookColumn._ID,
			BookColumn.NAME, BookColumn.AUTHOR, BookColumn.ENDCODE,
			BookColumn.PATH, BookColumn.RATING };
	private static final String[] BookmarkField = new String[] {
			BookmarkColumn._ID, BookmarkColumn.NAME, BookmarkColumn.PAGE,
			BookmarkColumn.TOTALPAGE, BookmarkColumn.BOOKID,
			BookmarkColumn.CREATEDATE, BookmarkColumn.CHAPTER,BookmarkColumn.CHAPTERTITLE };
	private static final String[] PROJECTION = new String[] {
			CatalogColumn._ID, // 0
			CatalogColumn.DESCRIPTION, // 1
			CatalogColumn.NAME, // 1
			CatalogColumn.PATH, // 1

	};

	public static final int MENU_ITEM_DELETE = Menu.FIRST;
	public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;

	// dial
	private static final int ENCODE_DIALOG = 3;
	private static final int diag_confirmdelete = 1;

	private static final int diag_Menu = 0;

	private static final int diag_goto = 4;

	public void debug(String msg) {

		new AlertDialog.Builder(this).setTitle("hello").setMessage(msg)
				.setIcon(0).setPositiveButton("OK", null).create().show();
	}

	@Override
	public void onBackPressed() {
		boolean isBookList = this.getListAdapter().equals(_bookcursorAdapter);
		boolean isLibList = this.getListAdapter().equals(_libcursorAdapter);
		if (isBookList) {
			try {

				libCursor = managedQuery(CatalogColumn.CONTENT_URI,
						CatalogField, null, null,
						CatalogColumn.DEFAULT_SORT_ORDER);
				this.setListAdapter(new LibCursorAdapter(this, libCursor));
			} catch (Exception e) {

				textCurrentFolder.setText(e.getMessage().toString());

			}
		}

		else {

			// this.finish();

		}
		super.onBackPressed();
	}

	public void showLib() {
		libCursor = managedQuery(CatalogColumn.CONTENT_URI, CatalogField, null,
				null, CatalogColumn.DEFAULT_SORT_ORDER);
		this._libcursorAdapter = new LibCursorAdapter(this, libCursor);
		this.setListAdapter(_libcursorAdapter);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			Log.d("info?" , info.toString());
		} catch (ClassCastException e) {
			Log.e("menu", "bad menuInfo", e);
			return false;
		}

		switch (item.getItemId()) {
		case MENU_ITEM_DELETE: {
			boolean isLibList = this.getListAdapter().equals(_libcursorAdapter);
			boolean isBookMarkList = this.getListAdapter().equals(
					_bookmarkcursorAdapter);
			Log.d("selected id?", String.valueOf(info.id));
			// Delete the note that the context menu is for
			Uri pdbUri;
			if (isLibList) {
				pdbUri = Uri.parse(CatalogColumn.CONTENT_URI + "/" + info.id);

				Log.d("uri", pdbUri.toString());
				int result = getContentResolver().delete(pdbUri, "", null);
			}
			if (isBookMarkList) {

				pdbUri = Uri.parse(BookmarkColumn.CONTENT_URI + "/" + info.id);

				Log.d("uri", pdbUri.toString());
				int result = getContentResolver().delete(pdbUri, "", null);
			}

			// getLibraries(new File("/sdcard/"));
			return true;
		}
		}
		return false;
	}

	private static final int REQUEST_ADDLIB = 0x125;

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.librarylist);
		dbname = getResources().getString(R.string.dbname).toString();
		textCurrentFolder = (TextView) findViewById(R.id.currentfolder);

		// this.copyfile("/data/data/com.android.lee.pdbreader/databases/"
		// + dbname, dblocation);
		this.application = (MyApplication) this.getApplication();

		libCursor = managedQuery(CatalogColumn.CONTENT_URI, CatalogField, null,
				null, CatalogColumn.DEFAULT_SORT_ORDER);
		_libcursorAdapter = new LibCursorAdapter(this, libCursor);
		this.setListAdapter(_libcursorAdapter);

		getListView().setOnCreateContextMenuListener(this);
		 Button btnAdd = (Button) findViewById(R.id.btnAddLib);
		 Button btnBookMark = (Button) findViewById(R.id.btn_bookmark);
		 Button btnHome = (Button) findViewById(R.id.btn_home);
		//final LinearLayout loAdd = (LinearLayout) findViewById(R.id.layoutbtnaddlib);
	//	final LinearLayout loBookmark = (LinearLayout) findViewById(R.id.layoutbtnbookmark);
		//final LinearLayout loLib = (LinearLayout) findViewById(R.id.layoutbtnlib);
		btnAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent();

				intent.setClassName(LibraryList.this, DirectoryBrowser.class
						.getName());
			Log.d("addoncick","");
				LibraryList.this.startActivityForResult(intent, REQUEST_ADDLIB);

			}
		});

		btnBookMark.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("bmkonclick","");
				LibraryList.this.listAllBookMark();
			}
		});

		btnHome.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("home","");
				LibraryList.this.showLib();
			}
		});
		ListView list = this.getListView();

	
		if (getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey("ACTION")) {

			String strAction = getIntent().getExtras().getString("ACTION");
			if (strAction.equals("BOOKMARK")) {
				listAllBookMark();
			}
		}

	}

	public void listAllBookMark() {
		bookmarkCursor = managedQuery(BookmarkColumn.CONTENT_URI,
				BookmarkField, null, null, BookmarkColumn.DEFAULT_SORT_ORDER);
		this._bookmarkcursorAdapter = new bookmarkCursorAdapter(this,
				bookmarkCursor);
		setListAdapter(_bookmarkcursorAdapter);
		

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		boolean isBookList = this.getListAdapter().equals(_bookcursorAdapter);
		if (!isBookList) {
			AdapterView.AdapterContextMenuInfo info;
			try {
				info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			} catch (ClassCastException e) {
				Log.e("", "bad menuInfo", e);
				return;
			}

			menu.setHeaderTitle("Delete");

			// Add a menu item to delete the note
			menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
		}

	}

	// @override
	protected Dialog onCreateDialog(int id) {
		Log.d("creatingdialog", Integer.toString(id));
		switch (id) {
		case diag_Menu:
			final CharSequence[] items = { getString(R.string.menu_deletelib) };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("");

			builder.setItems(items, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int item) {

					switch (item) {
					case 0:
						Context mContext = getApplicationContext();
						// LibraryList.this.debug("hihi");
						long id = LibraryList.this.getListView()
								.getSelectedItemId();
						LibraryList.this.debug(String.valueOf(id));
						Log.d("menuitem?", "");
						dismissDialog(diag_Menu);
						showDialog(diag_confirmdelete);

						break;

					}
					// dismissDialog (diag_Menu);
					Toast.makeText(getApplicationContext(), items[item],
							Toast.LENGTH_SHORT).show();
					// dismissDialog(diag_Menu);
				}
			});

			return builder.create();

		case diag_confirmdelete:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete?")
					.setCancelable(false).setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									LibraryList.this.debug("delete");
								}
							}).setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									// LibraryList.this.debug("delete");
								}
							});
			AlertDialog alert = builder.create();
			return alert;
		}
		return null;
	}

	

	protected void onListItemClick(ListView l, View v, int position, long id) {
		int selectedRow = (int) id;
		boolean isBookList = this.getListAdapter().equals(
				this._bookcursorAdapter);
		boolean isBookMarkList = this.getListAdapter().equals(
				this._bookmarkcursorAdapter);
		if (isBookList) {
			Log.d("bookid:?", String.valueOf(id));
			bookCursor = managedQuery(BookColumn.CONTENT_URI, BookField,
					"_id=" + String.valueOf(selectedRow), null,
					BookColumn.DEFAULT_SORT_ORDER);
			if (bookCursor.moveToNext()) {
				Intent intent = new Intent();
				intent.putExtra("ID", id);
				Log.d("BOOKID", String.valueOf(id));
				Log.d("PATH", bookCursor.getString(bookCursor.getColumnIndex(BookColumn.PATH)));
				Log.d("ENCODE",  bookCursor.getString(bookCursor.getColumnIndex(BookColumn.ENDCODE)));
				// intent.setClassName(LibraryList.this, VeaderActivity.class
				// .getName());
				// startActivity(intent);
				intent.putExtra("TARGET", "READ");
				intent.putExtra("PATH", bookCursor.getString(bookCursor.getColumnIndex(BookColumn.PATH)));
				intent.putExtra("ENCODE", bookCursor.getString(bookCursor.getColumnIndex(BookColumn.ENDCODE)));
				intent.setClassName(LibraryList.this, chapterDialog.class
						.getName());
				// startActivity(intent);
				startActivity(intent);
			}
		} else if (isBookMarkList) {
			Log.d("bookmark:?", String.valueOf(id));
			Cursor _bmcursor = managedQuery(BookmarkColumn.CONTENT_URI,
					BookmarkField, "_id=" + String.valueOf(selectedRow), null,
					BookColumn.DEFAULT_SORT_ORDER);
			if (_bmcursor.moveToNext()) {
				long bookid = _bmcursor.getLong(_bmcursor
						.getColumnIndex(BookmarkColumn.BOOKID));
				int pageno = _bmcursor.getInt(_bmcursor
						.getColumnIndex(BookmarkColumn.PAGE));
				int totalPage = _bmcursor.getInt(_bmcursor
						.getColumnIndex(BookmarkColumn.TOTALPAGE));
				int chapter = _bmcursor.getInt(_bmcursor
						.getColumnIndex(BookmarkColumn.CHAPTER));
				float percent = (float) pageno / (float) totalPage;
				Log.d("bookid:?", String.valueOf(bookid));
				Log.d("pageno:?", String.valueOf(pageno));
				Log.d("totalPage:?", String.valueOf(totalPage));
				Log.d("chapter:?", String.valueOf(chapter));
				Intent intent = new Intent();
				intent.putExtra("ID", bookid);
				intent.putExtra("CHAPTER", chapter);
				intent.putExtra("PAGE", pageno);
				intent.putExtra("TOTALPAGE", totalPage);
				intent.putExtra("PERCENT", percent);

				Log.d("BOOKID", String.valueOf(id));
				intent.setClassName(LibraryList.this, VeaderActivity.class
						.getName());
				startActivity(intent);
			}
		} else {

			// libCursor.moveToPosition(selectedRow-1);
			Log.d("cursorpos:", String.valueOf(selectedRow));
			// int catalogid =
			// libCursor.getInt(libCursor.getColumnIndex(CatalogColumn._ID));
			bookCursor = managedQuery(BookColumn.CONTENT_URI, BookField,
					"catalogid=" + String.valueOf(selectedRow), null,
					BookColumn.DEFAULT_SORT_ORDER);
			this._bookcursorAdapter = new bookCursorAdapter(this, bookCursor);
			this.setListAdapter(_bookcursorAdapter);

		}
	}
	private static final int REQUEST_BOOKMARK = 0x126;
	private static final int MENU_EXIT = 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(0, MENU_ZOOM, MENU_ZOOM, getResources().getString(
		// R.string.menu_text_size));

		// menu.add(0, MENU_COLOR, MENU_COLOR, R.string.menu_color);
		// menu.add(0, MENU_CHARSET, MENU_CHARSET, R.string.menu_charset);
		menu.add(0, MENU_EXIT, MENU_EXIT, R.string.menu_exit);

		// menu.add(0, MENU_BOOKMARK, MENU_BOOKMARK, R.string.menu_bookmark);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == MENU_EXIT) {

			this.finish();
		}
		return true;
	}



}