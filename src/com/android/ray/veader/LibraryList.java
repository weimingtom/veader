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

//import com.android.lee.pdbreader.SoftwarePassionView.OrderAdapter;
import com.android.ray.veader.R;

import com.android.ray.veader.SimpleGestureFilter.SimpleGestureListener;

import com.android.ray.veader.provider.BookColumn;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class LibraryList extends ListActivity implements SimpleGestureListener {

	private List<String> items = null;
	String dbpath, dbname, dblocation;
	TextView textCurrentFolder;
	private MyApplication application;
	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Library> _library = null;
	private ArrayList<Books> _booklist = null;
	private libraryAdapter m_adapter;
	private BooksAdapter _bookAdapter;
	private Runnable viewOrders;
	private int libid;
	Thread thread;
	private SimpleGestureFilter filter;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	@Override
	public void onBackPressed() {
		boolean isBookList = this.getListAdapter().equals(_bookAdapter);
		if (isBookList) {
			try {

				getLibraries(new File("/sdcard/"));

			} catch (Exception e) {

				textCurrentFolder.setText(e.getMessage().toString());

			}
		} else {
			super.onBackPressed();
//			this.onDestroy();
		}

	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);

		setContentView(R.layout.librarylist);
		dbname = getResources().getString(R.string.dbname).toString();
		textCurrentFolder = (TextView) findViewById(R.id.currentfolder);
		// textCurrentFolder.setText(dbname);
		// fnInitDB();
		// this.copyfile("/data/data/com.android.lee.pdbreader/databases/"
		// + dbname, dblocation);
		this.application = (MyApplication) this.getApplication();
		try {

			getLibraries(new File("/sdcard/"));

		} catch (Exception e) {

			textCurrentFolder.setText(e.getMessage().toString());

		}
		getListView().setOnCreateContextMenuListener(this);
		final Button btnAdd = (Button) findViewById(R.id.btnAddLib);

		btnAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// new
				// InsertDataTask().execute(textCurrentFolder.getText().toString(),
				// "a");
				// libraryManager libManager = new libraryManager();
				// @override

				Intent intent = new Intent();

				intent.setClassName(LibraryList.this, DirectoryBrowser.class
						.getName());
				startActivity(intent);

			}
		});
		ListView list = this.getListView();
	/*
		this.filter = new SimpleGestureFilter(this, this);
		this.filter.setMode(SimpleGestureFilter.MODE_TRANSPARENT);

		gestureDetector = new GestureDetector(new MyGestureDetector());
		
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
			
				Log.d("---onDown----", event.toString());
				long selectedid = LibraryList.this.getListView().getSelectedItemPosition();
				long selectedids = LibraryList.this.getListView().getSelectedItemId();
				Log.d("--selected item pos", String.valueOf(selectedid));
				Log.d("--selected item id", String.valueOf(selectedids));
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};*/
//list.setOnLongClickListener(l)
	    list.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
            	Log.i("onlongclick","");
                return false;
            }
        });

		list.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				Library lib = 				(Library) LibraryList.this.getListView().getItemAtPosition(pos);
				Log.d("id?",String.valueOf( lib.getid()));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Log.d("nothing", "");
			}});
		
		
		
		
	}
	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e("", "bad menuInfo", e);
            return;
        }

        //Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
       // if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
         //   return;
      //  }

        // Setup the menu header
        menu.setHeaderTitle("Delete");

        // Add a menu item to delete the note
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
    }
	   public static final int MENU_ITEM_DELETE = Menu.FIRST;
	    public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e("menu", "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
            	Log.d("selected id?", String.valueOf(info.id));
                // Delete the note that the context menu is for
            	Uri pdbUri = Uri.parse(CatalogColumn.CONTENT_URI + "/" + info.id);
        		ContentValues values = new ContentValues();
        		// values.put(BookColumn.NAME, mBook.mName);
        		

        		Log.d("uri", pdbUri.toString());
        		int result = getContentResolver().delete(pdbUri, "", null);
            
                return true;
            }
        }
        return false;
    }
    
    
    
	private void getLibraries(File _file) {
		// TextView textCurrentFolder = (TextView)
		// findViewById(R.id.currentfolder);
		// textCurrentFolder.setText(_file.toString());

		try {
			_library = new ArrayList<Library>();
			_library = this.application.getDataHelper().getAll();

			this.m_adapter = new libraryAdapter(this, R.layout.row_directory,
					_library);
			setListAdapter(this.m_adapter);

			Log.i("ARRAY", "" + _library.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		// runOnUiThread(returnRes);
	}

	private void getBooks(int catalogid) {
		// TextView textCurrentFolder = (TextView)
		// findViewById(R.id.currentfolder);
		// textCurrentFolder.setText(_file.toString());
		// File[] files = _file.listFiles();
		try {
			_booklist = new ArrayList<Books>();
			_booklist = this.application.getDataHelper().getBooksByCatalogID(
					catalogid);
			// debug(Integer.toString(catalogid));
			// this.m_adapter = new libraryAdapter(this, R.layout.row_directory,
			// _library);
			this._bookAdapter = new BooksAdapter(this, R.layout.row_directory,
					_booklist);
			setListAdapter(this._bookAdapter);

			Log.i("ARRAY", "" + _booklist.size());
		} catch (Exception e) {

			Log.e("BACKGROUND_PROC", e.getMessage());
			// debug(e.getMessage());
		}
		// runOnUiThread(returnRes);
	}

	private class _libadpapter<T> extends ArrayAdapter<T> {

		protected ArrayList<T> items;
		protected TextView tt, bt, txtRemark;
		protected ImageView viewIcon;

		public _libadpapter(Context context, int textViewResourceId,
				ArrayList<T> items) {
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

			tt = (TextView) v.findViewById(R.id.toptext);
			bt = (TextView) v.findViewById(R.id.bottomtext);
			txtRemark = (TextView) v.findViewById(R.id.txtRemark);
			viewIcon = (ImageView) v.findViewById(R.id.rowicon);
			return v;
		}

	}

	private class libraryAdapter extends _libadpapter {

		protected libraryAdapter(Context context, int textViewResourceId,
				ArrayList<Library> items) {
			super(context, textViewResourceId, items);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			fillValue((Library) super.items.get(position));
			return v;
		}

		public void fillValue(Library o) {
			// Library o = items.get(position);
			if (o != null) {
				int bookscount = application.getDataHelper()
						.getBookCountByCatalogid(o.getid());

				if (tt != null) {
					tt.setText(o.getname());
				}
				if (bt != null) {
					bt.setText(o.getpath());
				}
				txtRemark.setText("(" + Integer.toString(bookscount)
						+ " books)");
			}

		}
		/*
		 * @Override public void onItemClick(AdapterView<?> parent, View view,
		 * int position, long id) { // TODO Auto-generated method stub
		 * 
		 * int selectedRow = (int) id;
		 * 
		 * //final File file = new File(_library.get(selectedRow).getpath());
		 * int catalogid = _library.get(selectedRow).getid(); if
		 * (application.getDataHelper().getBookCountByCatalogid(catalogid)>0) {
		 * getBooks( catalogid);
		 * 
		 * } else {
		 * 
		 * } debug("librarylist"); }
		 */

	}

	private class BooksAdapter extends _libadpapter {

		protected BooksAdapter(Context context, int textViewResourceId,
				ArrayList<Books> items) {
			super(context, textViewResourceId, items);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			fillValue((Books) super.items.get(position));
			return v;
		}

		public void fillValue(Books o) {
			// Library o = items.get(position);
			if (o != null) {

				// txtRemark.setText("xxxx");
				viewIcon.setImageResource(R.drawable.dvi);
				if (tt != null) {
					tt.setText(o.getname());
				}
				if (bt != null) {
					bt.setText(o.getpath());
				}
			}

		}

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		int selectedRow = (int) id;
		// this.debug(Integer.toString(selectedRow));
		boolean isBookList = this.getListAdapter().equals(_bookAdapter);
		if (isBookList) {
			long catalogid = _booklist.get(selectedRow).getid();
			Intent intent = new Intent();
			intent.putExtra("ID", catalogid);
			intent.setClassName(LibraryList.this, VeaderActivity.class
					.getName());
			startActivity(intent);
		} else {

			int catalogid = _library.get(selectedRow).getid();

			int bookcount = application.getDataHelper()
					.getBookCountByCatalogid(catalogid);

			if (bookcount >= 0) {

				getBooks(catalogid);

			} else {

			}
			// this.debug("librarylist");
		}
	}

	public void debug(String msg) {

		new AlertDialog.Builder(this).setTitle("hello").setMessage(msg)
				.setIcon(0).setPositiveButton("OK", null).create().show();
	}

	private void InsertLibrary(final String... args) {
		String _msg;
		boolean executestatus;
		String path = args[0];
		boolean _pathExists = LibraryList.this.application.getDataHelper()
				.pathExists(path);

		if (!_pathExists) {
			// this.dialog.setMessage("Creating New");
			LibraryList.this.application.getDataHelper().insertLibrary(args[0],
					args[1], "");
			_msg = "Create Library";
			executestatus = false;
			new AlertDialog.Builder(this).setTitle(
					"library successfully created").setMessage(_msg).setIcon(0)
					.setPositiveButton("OK", null)

					.create().show();
		} else {

			_msg = "Library Exists Already";
			executestatus = false;
			new AlertDialog.Builder(this).setTitle("library not created")
					.setMessage(_msg).setIcon(0).setPositiveButton("OK", null)

					.create().show();
		}
	}

	
	

	
	
	//dial
	private static final int ENCODE_DIALOG = 3;
	private static final int diag_confirmdelete = 1;

	private static final int diag_Menu = 0;
	private static final int diag_goto = 4;
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
						//LibraryList.this.debug("hihi");
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

	@Override
	public void onDoubleTap() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSwipe(int direction) {
		// TODO Auto-generated method stub
		
	}

}