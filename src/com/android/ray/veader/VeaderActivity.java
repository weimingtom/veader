package com.android.ray.veader;

//import com.google.android.webviewdemo.R;
//import com.google.android.webviewdemo.WebViewDemo.DemoJavaScriptInterface;
//import com.google.android.webviewdemo.WebViewDemo.MyWebChromeClient;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.android.ray.veader.R;

import com.android.ray.veader.SimpleGestureFilter.SimpleGestureListener;
import com.android.ray.veader.pdb.AbstractBookInfo;
import com.android.ray.veader.provider.BookColumn;
import com.android.ray.veader.provider.BookmarkColumn;
import com.android.ray.veader.util.ColorUtil;
import com.android.ray.veader.util.Constatnts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VeaderActivity extends Activity implements SimpleGestureListener {

	final class DemoJavaScriptInterface {

		DemoJavaScriptInterface() {
		}

		/**
		 * This is not called on the UI thread. Post a runnable to invoke
		 * loadUrl on the UI thread.
		 */
		public void clickOnAndroid() {
			mHandler.post(new Runnable() {
				public void run() {
					mWebView.loadUrl("javascript:settext("
							+ "約莫有六尺左右的身材，他那麼挺直的立著" + ");");
				}
			});

		}
	}
	private class loadContentTask extends AsyncTask<CharSequence, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(
				VeaderActivity.this);

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(final CharSequence... args) {
			// List<String> names =
			// VeaderActivity.this.application.getDataHelper().selectAll();
			String paramTxt = args[0].toString();
			int pos;
			if (args.length == 1) {
				pos = 0;
			} else {
				pos = Integer.parseInt(args[1].toString());
			}
			// paramTxt = this.wrap(paramTxt, 27, null, true);
		//	Log.d("settextactivity", paramTxt);
			VeaderActivity.this.settext(args[0]);

			return "";
		}

		// can use UI thread here
		protected void onPostExecute(final String result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			VeaderActivity.this.txtbottomright.setText((mBook.mPage + 1) + "/"
					+ mBook.getPageCount());
	
		}

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Loading...");
			this.dialog.show();
		}

		public String wrap(String str, int wrapLength, String newLineStr,
				boolean wrapLongWords) {
			if (str == null) {
				return null;
			}
			if (newLineStr == null) {
				newLineStr = "\n";
			}
			if (wrapLength < 1) {
				wrapLength = 1;
			}
			int inputLineLength = str.length();
			int offset = 0;
			StringBuffer wrappedLine = new StringBuffer(inputLineLength + 32);

			while ((inputLineLength - offset) > wrapLength) {
				if (str.charAt(offset) == ' ') {
					offset++;
					continue;
				}
				int spaceToWrapAt = str.lastIndexOf(' ', wrapLength + offset);

				if (spaceToWrapAt >= offset) {
					// normal case
					wrappedLine.append(str.substring(offset, spaceToWrapAt));
					wrappedLine.append(newLineStr);
					offset = spaceToWrapAt + 1;

				} else {
					// really long word or URL
					if (wrapLongWords) {
						// wrap really long word one line at a time
						wrappedLine.append(str.substring(offset, wrapLength
								+ offset));
						wrappedLine.append(newLineStr);
						offset += wrapLength;
					} else {
						// do not wrap really long word, just extend beyond
						// limit
						spaceToWrapAt = str.indexOf(' ', wrapLength + offset);
						if (spaceToWrapAt >= 0) {
							wrappedLine.append(str.substring(offset,
									spaceToWrapAt));
							wrappedLine.append(newLineStr);
							offset = spaceToWrapAt + 1;
						} else {
							wrappedLine.append(str.substring(offset));
							offset = inputLineLength;
						}
					}
				}
			}

			// Whatever is left in line is short enough to just pass through
			wrappedLine.append(str.substring(offset));

			return wrappedLine.toString();
		}

	}
	private class loadContentTaskLast extends
			AsyncTask<CharSequence, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(
				VeaderActivity.this);

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(final CharSequence... args) {
			// List<String> names =
	
			String paramTxt = args[0].toString();
			// paramTxt = this.wrap(paramTxt, 27, null, true);
			//Log.d("settextactivity", paramTxt);
			VeaderActivity.this.settext(args[0], 100);

			return "";
		}

		// can use UI thread here
		protected void onPostExecute(final String result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			VeaderActivity.this.txtbottomright.setText((mBook.mPage + 1) + "/"
					+ mBook.getPageCount());
		
		}

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Loading...");
			this.dialog.show();
		}

	}
	private class menuVeaderActivity extends Dialog{
		//private ArrayList<_menuItem> _menulist = null;
		//private menuAdapter mAdapter;
		Context ctx;
		private long totalPage;
		private long currentPage;
		private int selectedPage;
		 private TextView txtpageno;
	    public menuVeaderActivity(Context context) {
	    	
	        super(context);
	        ctx = context;
	        this.totalPage = VeaderActivity.this.totalPage;
	        this.currentPage  = VeaderActivity.this.pageno;
	    }
	   
	    //@override

	    //@override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	        
	        setContentView(R.layout.gotopage);
	        setCancelable(true);
	        setCanceledOnTouchOutside(true);
	        


	        
	        SeekBar seek = (SeekBar)findViewById(R.id.page_seek);
	         txtpageno = (TextView)findViewById(R.id.txtpageno);
	        
	        seek.setMax((int)totalPage-1);
	        
	        txtpageno.setText(String.valueOf(VeaderActivity.this.pageno) +"/"+String.valueOf(VeaderActivity.this.totalPage));
	       // int ib = Settings.System.getInt(getContext().getContentResolver(),
	       //         Settings.System.SCREEN_BRIGHTNESS,100); 
	        
	       // seek.setProgress(ctx.);
	   	 Button btnGoto = (Button) findViewById(R.id.btngotopage);

	    	btnGoto.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
//VeaderActivity.this.debug("xxxx");
String jsCmd = "javascript:SimpleReader.goto("+String.valueOf(menuVeaderActivity.this.selectedPage)+");";
Log.d("jscommand", jsCmd);
VeaderActivity.this.mWebView.loadUrl(jsCmd);
					Log.d("onclick!", "");
				}
			});
	        
	        seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

	            //@override
	            public void onProgressChanged(SeekBar seekbar, int i, boolean flag) {
	                /*
	                WindowManager.LayoutParams lp = getOwnerActivity().getWindow().getAttributes();
	                lp.screenBrightness = Math.max(5,i)/255f;
	                getOwnerActivity().getWindow().setAttributes(lp);*/
	            	menuVeaderActivity.this.selectedPage = (i==0)?1:i;
	            	int seekpos =menuVeaderActivity.this.selectedPage;
	            	Log.d("seekpos", String.valueOf(menuVeaderActivity.this.selectedPage));
	            	if(seekpos==(VeaderActivity.this.totalPage-1))
	            		VeaderActivity.this.previousPage = VeaderActivity.this.totalPage-1;
	            	menuVeaderActivity.this. txtpageno.setText(String.valueOf(seekpos+1) +"/"+String.valueOf(VeaderActivity.this.totalPage));
	        	    
	            }

	            //@override
	            public void onStartTrackingTouch(SeekBar seekbar) {
	              
	            }

	            //@override
	            public void onStopTrackingTouch(SeekBar seekbar) {
	                
	            }
	            
	        });
	        //end seek bar onlist
	    
	        
	        
	    }
	    

	    


	}
	private class MyGestureDetector extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		public boolean nextPage() {
			Log.d("dnxt page", "nxt page");
			if (isLastChapterLastPage) {
				Log.d("loadnx chapter", "yes");
				VeaderActivity.this.nextChapter();
			} else {
				Log.d("loadnx chapter", "no");
				mWebView.loadUrl("javascript:SimpleReader.next(1);");
			}
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;

				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// this.nextPage();
					// mWebView.loadUrl("javascript:previousPage();");
					this.previousPage();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// debug("left to right");
					// this.previousPage();
					nextPage();

				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		public void onLongPress(android.view.MotionEvent e) {

			// debug("hi");
			Log.d("longpress", Integer.toString(diag_Menu));
			showDialog(diag_Menu);
		}

		public boolean previousPage() {
			Log.d("previous page", "previous page");
			mWebView.loadUrl("javascript:SimpleReader.prev(1);");
			return true;
		}
	}
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			Log.d(LOG_TAG, message);
			
			String[] msg = message.toString().split(":");
			if (msg[0].compareTo("percent") == 0) {
				percent = msg[1];
				Log.d("percent:", percent);
				if (Integer.parseInt(msg[1]) == 0)
					previousChapter(100);
				if (Integer.parseInt(msg[1]) == 100) {
					// nextPage();
					VeaderActivity.this.isLastChapterLastPage = true;
				} else {
					VeaderActivity.this.isLastChapterLastPage = false;
				}
				if((VeaderActivity.this.previousPage ==VeaderActivity.this.totalPage )
						&&(percent.equals("100"))){
						 nextChapter();
					}
					
			}
			if (msg[0].compareTo("pagecount") == 0)
			{
				txtbottomcenter.setText("P." + msg[1]);
			try {
				VeaderActivity.this.previousPage =VeaderActivity.this.pageno; 
				VeaderActivity.this.pageno =Integer.parseInt( msg[1].split("/")[0]);
				if(msg[1].split("/").length>1){
					VeaderActivity.this.totalPage =Integer.parseInt( msg[1].split("/")[1]);
				}
				else
				{
					VeaderActivity.this.totalPage = 1;
				}
				Log.d("pageno", String.valueOf(VeaderActivity.this.pageno));
				Log.d("totalpageno", String.valueOf(VeaderActivity.this.totalPage));
				
				if (Integer.parseInt(msg[1].split("/")[0]) == 0) {
					Log.d("msg[1]??" , msg[1]);
					Log.d("msg[1].split('/')[0]?" , msg[1].split("/")[0]);
					previousChapter(100);
				}
				VeaderActivity.this.currentChapter = Integer.parseInt(msg[1]
						.split("/")[0]);
				VeaderActivity.this.updateCurrentPage();
			} catch (Exception e) {
				debug(e.getMessage());

			}
			}
			result.confirm();
			return true;
		}

	}
	private static final String LOG_TAG = "WebViewDemo";
	private String percent;
	private WebView mWebView;
	private TextView txtbottomleft, txtbottomright, txtbottomcenter;
	private Handler mHandler = new Handler();
	long currentChapter, pageno;
	// private GestureDetector gestureScanner;
	protected static final String TAG = "PilotBookReaderActivity";
	private static final int MAX_TEXT_SIZE = 36;
	private static final int MIN_TEXT_SIZE = 8;
	private static final int REQUEST_COLOR = 0x123;
	private int mPage;
	private long previousPage;
private ZoomControls zoomControl;
	private AbstractBookInfo mBook;

	private SimpleGestureFilter filter;

	private GestureDetector gestureDetector;

	View.OnTouchListener gestureListener;

	private Handler pHandler;

	private int totalPage;

	protected int lastOffset;

	protected boolean isLastChapterLastPage;

	/* ---------------------menu---------------------------- */
	private static final int MENU_ZOOM = 0;

	private static final int MENU_COLOR = 1;
	private static final int MENU_CHARSET = 2;
	private static final int MENU_ROTATAION = 3;

	private static final int MENU_FORMAT = 4;

	private static final int MENU_LIB = 5;

	private static final int MENU_BOOKMARK = 6;
	
	private static final int ENCODE_DIALOG = 3;

	private static final int FORMAT_DIALOG = 1;

	private static final int BRIGHTNESS_DIALOG = 2;

	private static final int diag_Menu = 0;

	private static final int diag_goto = 4;

	public void debug(String msg) {

		new AlertDialog.Builder(this).setTitle("hello").setMessage(msg)
				.setIcon(0).setPositiveButton("OK", null).create().show();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		this.filter.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}
	/*
	 * private Runnable returnRes = new Runnable() {
	 * 
	 * @Override public void run() { try {
	 * 
	 * String txt = (String)mBook.getText().subSequence(0, 300); //debug(txt);
	 * Log.d("Returnres", txt); VeaderActivity.this.settext(txt); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * }//runOnUiThread(returnRes); };
	 */
	private void doShow(final int offset) {
		showProgressBarVisibility(true);
		// mBody.setText("");

		pHandler.post(new Runnable() {
			public void run() {

				try {
					mBook.nextPage();
					final CharSequence txt = mBook.getText();
					Log.d("do show", (String) txt);
					// debug((String) txt);
					runOnUiThread(new Runnable() {
						public void run() {
							try {
								// mBody.setText(txt);
								// new loadContentTask().execute((String)txt);
								// mWebView.loadUrl("javascript:settext('" +txt
								// + "');");
								//VeaderActivity.this.settext(txt);
								new loadContentTask().execute(txt, "0");
								if (offset > 0) {
									new Handler().postDelayed(new Runnable() {
										public void run() {
											/*
											 * int line = mBody.getLayout()
											 * .getLineForOffset(offset); int
											 * scollY = topPanel.getHeight() +
											 * line * mBody.getLineHeight();
											 * scrollview.scrollTo(0, scollY);
											 */
										}
									}, 50);
								}
								setPageTitle();
							} finally {
								showProgressBarVisibility(false);
							}

						}
					});

				} catch (Exception e) {
					Log.d(TAG, e.getMessage(), e);
					runOnUiThread(new Runnable() {
						public void run() {
							showProgressBarVisibility(false);
						}
					});
				}

			}
		});

	}
	public boolean insertBookmark(int i) {
		Log.d("udpatebookmark", String.valueOf(VeaderActivity.this.currentChapter));
				//Uri pdbUri = Uri.parse(BookmarkColumn.CONTENT_URI + "/" + mBook.mID);
				ContentValues values = new ContentValues();
				// values.put(BookmarkColumn.NAME, mBook.mName);
				values.put(BookmarkColumn.PAGE , this.pageno );
				values.put(BookmarkColumn.TOTALPAGE , this.totalPage );
				values.put(BookmarkColumn.CHAPTER, mBook.mPage);
				values.put(BookmarkColumn.NAME , mBook.getname());
				values.put(BookmarkColumn.DESCRIPTION, mBook.getname());

				
				Uri result = getContentResolver().insert(BookmarkColumn.CONTENT_URI, values);
				return true;
			}
	/**
	 * Provides a hook for calling "alert" from javascript. Useful for debugging
	 * your javascript.
	 */

	public boolean nextChapter() {
		isLastChapterLastPage = false;
		Log.d("fn!dnxt page", "nxt page");
		// new loadContentTask().execute("向左");
		// VeaderActivity.this.settext("向左");
		Log.d("has next??", String.valueOf(mBook.hasNextPage()));
		if (mBook.hasNextPage()) {
			// mBody.setText("");
			mBook.nextPage();
			Log.d("dnxt page", "has page");
			try {
				final CharSequence txt = mBook.getText();
				// debug (txt);
				//Log.d("hasnext!!", (String) txt);
				Log.d("hasnext!!", "");
				String temp = txt.toString().replace("\n", ":br:");

				// mWebView.loadUrl("javascript:settext('" +temp + "');");
				// doShow(0);
				new loadContentTask().execute(txt, "0");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
			// scrollview.scrollTo(0, 0);
		}
		return true;
	}
	@Override
	public void onCreate(Bundle icicle) {
		// gestureScanner = new GestureDetector(this);
		super.onCreate(icicle);
		setContentView(R.layout.veader);
		mWebView = (WebView) findViewById(R.id.webview);
		txtbottomleft = (TextView) findViewById(R.id.txtbottomleft);
		txtbottomright = (TextView) findViewById(R.id.txtbottomright);
		txtbottomcenter = (TextView) findViewById(R.id.txtbottomcenter);
	//	TableLayout tablebottom = (TableLayout) findViewById(R.id.tablebottom);

		SpannableStringBuilder builder, buildertxtbottomright;
		buildertxtbottomright = new SpannableStringBuilder();
		builder = new SpannableStringBuilder();
		builder.append("bookname");
		builder.setSpan(new ForegroundColorSpan(Color.rgb(148, 83, 61)), 4, 7,
				Spanned.SPAN_COMPOSING);
		txtbottomleft.setText("bookname");
		int intFontColor = Color.rgb(105, 60, 44);
		txtbottomleft.setTextColor(intFontColor);
		txtbottomcenter.setText("Chapter");
		txtbottomcenter.setTextColor(intFontColor);

		txtbottomright.setText("1/100");
		txtbottomright.setTextColor(intFontColor);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);

		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.setScrollContainer(isRestricted());

		mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
		// mWebView.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.paper3));
		// tablebottom.setBackgroundColor(150);
		mWebView.loadUrl("file:///android_asset/view.html");
		mWebView.setVerticalScrollBarEnabled(false);

		this.filter = new SimpleGestureFilter(this, this);
		this.filter.setMode(SimpleGestureFilter.MODE_TRANSPARENT);

		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("---onDown----", event.toString());
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};

		mWebView.setOnTouchListener(gestureListener);

		//final Button btnTest = (Button) findViewById(R.id.btnTest);

		HandlerThread thread = new HandlerThread("reader");
		thread.start();
		pHandler = new Handler(thread.getLooper());

		setProgressBarIndeterminate(true);
		// long id =2;
		long id = getIntent().getExtras().getLong("ID");
		Log.d("bookid:", String.valueOf(id));
		Uri pdbUri = Uri.parse(BookColumn.CONTENT_URI + "/" + id);
		Log.d("pdburl", pdbUri.toString());
		Cursor cursor = getContentResolver().query(
				pdbUri,
				new String[] { BookColumn._ID, BookColumn.NAME,
						BookColumn.AUTHOR, BookColumn.ENDCODE, BookColumn.PATH,
						BookColumn.LAST_PAGE, BookColumn.FORMAT,
						BookColumn.LAST_OFFSET }, null, null, null);

		Log.d("after q", "");
		String path = "";
		String encode = null;
		String name = null;
		int format = 0;
		int lastPage = 0;
		lastOffset = 0;
		if (cursor.moveToNext()) {
			int pathIdx = cursor.getColumnIndexOrThrow(BookColumn.PATH);
			int encodeIdx = cursor.getColumnIndexOrThrow(BookColumn.ENDCODE);
			int lastpageIdx = cursor
					.getColumnIndexOrThrow(BookColumn.LAST_PAGE);
			int formatIdx = cursor.getColumnIndexOrThrow(BookColumn.FORMAT);
			int offsetIdx = cursor
					.getColumnIndexOrThrow(BookColumn.LAST_OFFSET);
			int nameIdx = cursor.getColumnIndexOrThrow(BookColumn.NAME);

			path = cursor.getString(pathIdx);
			encode = cursor.getString(encodeIdx);
			lastPage = cursor.getInt(lastpageIdx);
			format = cursor.getInt(formatIdx);
			lastOffset = cursor.getInt(offsetIdx);
			name = cursor.getString(nameIdx);
		}
		Log.d("filename:", name);
		Log.d("path:", path);
		cursor.close();

		File f = new File(path);
		mBook = AbstractBookInfo.newBookInfo(f, id);
		try {
			mBook.setEncode(encode);
			mBook.setFormat(format);
			mBook.setFile(f);

			mBook.setPage(lastPage);
			mBook.setName(name);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		txtbottomleft.setText(mBook.mName);
		// mBook.nextPage();
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {

				// mWebView.loadUrl("javascript:settext('" + "test" + "');");
				doShow(VeaderActivity.this.lastOffset);
				// runOnUiThread(returnRes);
				// returnRes.run();
				// Log.d("do show", (String)txt);
			}
		});

		

	}
	// @override
	protected Dialog onCreateDialog(int id) {
Log.d("creatingdialog", Integer.toString(id));
		switch (id) {
		case diag_Menu:
			final CharSequence[] items = {getString(R.string.menu_gotopage),
					getString(R.string.menu_next5),
					getString(R.string.menu_previous5), 
					getString(R.string.menu_nextchapter), 
					getString(R.string.menu_previouschapter), 
					getString(R.string.menu_addbookmark)
					};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("");
		
			builder.setItems(items, new DialogInterface.OnClickListener() {
				
			    public void onClick(DialogInterface dialog, int item) {
			    	Log.d("currentPage", String.valueOf( VeaderActivity.this.pageno));
			    	Log.d("totalPage", String.valueOf( VeaderActivity.this.totalPage));
			    	switch (item){
			    	case 0:
			    		Context mContext = getApplicationContext();
			    		/*
			    		Context mContext = getApplicationContext();
			    		Dialog _dialog = new Dialog(mContext);

			    		_dialog.setContentView(R.layout.gotopage);
			    		_dialog.setTitle("Go to Page");

			    		TextView text = (TextView) _dialog.findViewById(R.id.txtpageno);
			    		text.setText("Hello, this is a custom dialog!");
*/
			    		dismissDialog  (diag_Menu);
			    		showDialog(diag_goto);
			    		break;
			    	case 1:
			 
			    			Log.d("next 5 page", "next 5 page");
			    			mWebView.loadUrl("javascript:SimpleReader.next(5);");
			    			
			    			break;
			    	case 2:
			    		Log.d("prv 5 page", "prev 5 page");
		    			mWebView.loadUrl("javascript:SimpleReader.prev(5);");
		    			break;
			    	case 3:
			    		nextChapter();
			    		break;
			    	case 4:
			    		previousChapter(0);
			    		break;
			    	case 5:
			    		insertBookmark(0);
			    		break;
			    	}
			    	//dismissDialog  (diag_Menu);
			        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
			        dismissDialog  (diag_Menu);
			    }
			});

			return builder.create();
		case ENCODE_DIALOG:
			String[] charsetArray = getResources().getStringArray(
					R.array.charset);
			int i = 0;
			for (String charset : charsetArray) {
				if (charset.equals(mBook.mEncode)) {
					break;
				}
				i++;
			}
			return new AlertDialog.Builder(this).setTitle(
					R.string.default_charset).setSingleChoiceItems(
					R.array.charset, i, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String encode = VeaderActivity.this.getResources()
									.getStringArray(R.array.charset)[which];
							try {
								settext("");
								int page = mBook.mPage;
								mBook.setEncode(encode);
								mBook.setFile(mBook.mFile);

								mBook.setPage(page);
								settext(mBook.getText());
							} catch (Exception e) {
								Log.e(TAG, e.getMessage(), e);
							}

							dialog.dismiss();
						}
					}).create();

		case FORMAT_DIALOG:
			return new AlertDialog.Builder(this).setTitle(
					R.string.default_charset).setSingleChoiceItems(
					R.array.format, mBook.mFormat,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// String encode = PalmBookReaderActivity.this
							// .getResources().getStringArray(
							// R.array.charset)[which];
							try {
								settext("");
								int page = mBook.mPage;
								mBook.setFile(mBook.mFile);
								mBook.setFormat(which);
								mBook.setPage(page);
								settext(mBook.getText());
							} catch (Exception e) {
								Log.e(TAG, e.getMessage(), e);
							}

							dialog.dismiss();
						}
					}).create();
		case diag_goto:
		
    		
			return new menuVeaderActivity(this);
//break;
		//case BRIGHTNESS_DIALOG:
			//return new BrightnessDialog(this);
//return null;
		}
		return null;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(0, MENU_ZOOM, MENU_ZOOM, getResources().getString(
	//			R.string.menu_text_size));

		//menu.add(0, MENU_COLOR, MENU_COLOR, R.string.menu_color);
		//menu.add(0, MENU_CHARSET, MENU_CHARSET, R.string.menu_charset);
		menu.add(0, MENU_ROTATAION, MENU_ROTATAION, "");
	//	menu.add(0, MENU_FORMAT, MENU_FORMAT, R.string.menu_format);
		menu.add(0, MENU_LIB, MENU_LIB, R.string.menu_lib);
		//menu.add(0, MENU_BOOKMARK, MENU_BOOKMARK, R.string.menu_bookmark);
		return true;

	}
	@Override
	public void onDoubleTap() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == MENU_ZOOM) {
			zoomControl.show();
		} else if (item.getItemId() == MENU_COLOR) {
			Intent intent = new Intent(this, ColorListActivity.class);

			startActivityForResult(intent, REQUEST_COLOR);
			// scrollview.fullScroll(View.FOCUS_UP);
			// mBody.moveCursorToVisibleOffset();
		} else if (item.getItemId() == MENU_CHARSET) {
			showDialog(ENCODE_DIALOG);
		} else if (item.getItemId() == MENU_ROTATAION) {
			if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);// .SCREEN_ORIENTATION_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			}
		} else if (item.getItemId() == MENU_FORMAT) {
			showDialog(FORMAT_DIALOG);
		} else if (item.getItemId() == MENU_LIB) {
			Intent intent = new Intent();

			intent.setClassName(VeaderActivity.this, LibraryList.class
					.getName());
			startActivity(intent);
		}

		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		/*
		MenuItem rotationIgtem = menu.getItem(MENU_ROTATAION);
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) {
			rotationIgtem.setTitle(R.string.menu_lock);
		} else {
			rotationIgtem.setTitle(R.string.menu_unlock);
		}
*/
		//MenuItem formatItem = menu.getItem(MENU_FORMAT);
		//formatItem.setEnabled(mBook.supportFormat());

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onSwipe(int direction) {

		switch (direction) {

		case SimpleGestureFilter.SWIPE_RIGHT:
			mWebView.loadUrl("javascript:settext(" + "約莫有六尺左右的身材，他那麼挺直的立著"
					+ ");");

			break;
		case SimpleGestureFilter.SWIPE_LEFT:
			mWebView.loadUrl("javascript:settext(" + "約莫有六尺左右的身材，他那麼挺直的立著"
					+ ");");
			break;
		case SimpleGestureFilter.SWIPE_DOWN:
		case SimpleGestureFilter.SWIPE_UP:

		}
	}
	public boolean previousChapter(int pos) {
		Log.d("fn!prev page", "prev page");
		Log.d("has prev?", String.valueOf(mBook.hasPrevPage()));
		// new loadContentTask().execute("向右");
		// VeaderActivity.this.settext("向右");
		Log.d("currentPage", String.valueOf(mBook.mPage));
		if(currentChapter==0)return false;
		if (mBook.hasPrevPage()) {
			// mBody.setText("");
			mBook.prevPage();
			if (mBook.isProgressing()) {
				mBook.stop();
			}
	
			try {
				final CharSequence txt = mBook.getText();
				// debug (txt);
				Log.d("hasnext!!", "");
				String temp = txt.toString().replace("\n", ":br:");
				if (pos == 100) {
					new loadContentTaskLast()
							.execute(temp, String.valueOf(pos));
				} else {
					new loadContentTask().execute(temp, String.valueOf(pos));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return true;
	}
	private void setPageTitle() {

		txtbottomright.setText("Chapter" + (mBook.mPage) + "/"
				+ mBook.getPageCount());

	}
	final void settext(CharSequence strtxt) {
		String strParam = strtxt.toString().replace("�D", "").replace("\n\n\n",
				"\n\n");
		mWebView.loadUrl("javascript:settext('"
				+ strParam.toString().replace("\n", ":br:") + "',0);");

	}
	final void settext(CharSequence strtxt, int pos) {
		String strParam = strtxt.toString().replace("�D", "").replace("\n\n\n",
				"\n\n");
		mWebView.loadUrl("javascript:settext('"
				+ strParam.toString().replace("\n", ":br:") + "',100);");

	}
	public final void showProgressBarVisibility(boolean visible) {
		// findViewById(R.id.progress_read).setVisibility(visible ?
		// View.VISIBLE:View.GONE);
	}

	public boolean updateCurrentPage() {
Log.d("updatecurrentpage", "");
		Uri pdbUri = Uri.parse(BookColumn.CONTENT_URI + "/" + mBook.mID);
		ContentValues values = new ContentValues();
		// values.put(BookColumn.NAME, mBook.mName);
		values.put(BookColumn.LAST_PAGE, mBook.mPage);
		values.put(BookColumn.ENDCODE, mBook.mEncode);
		values.put(BookColumn.FORMAT, mBook.mFormat);
		values.put(BookColumn.LAST_OFFSET, VeaderActivity.this.currentChapter-1);

		Long now = Long.valueOf(System.currentTimeMillis());
		values.put(BookColumn.CREATE_DATE, now);

		int result = getContentResolver().update(pdbUri, values, null, null);
		return true;
	}
}