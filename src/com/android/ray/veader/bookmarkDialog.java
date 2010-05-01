package com.android.ray.veader;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.android.ray.veader.R;
import com.android.ray.veader.LibraryList.bookmarkCursorAdapter;
import com.android.ray.veader.provider.BookmarkColumn;
import com.android.ray.veader.provider.CatalogColumn;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class bookmarkDialog extends ListActivity {
	private static final String[] BookmarkField = new String[] {
			BookmarkColumn._ID, BookmarkColumn.NAME, BookmarkColumn.PAGE,
			BookmarkColumn.TOTALPAGE, BookmarkColumn.BOOKID,
			BookmarkColumn.CREATEDATE, BookmarkColumn.CHAPTER };

	private bookmarkCursorAdapter _bookmarkcursorAdapter;

	// @override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bookmarkdialog);
		// setCancelable(true);
		// setCanceledOnTouchOutside(true);

		Cursor bookmarkCursor = managedQuery(BookmarkColumn.CONTENT_URI,
				BookmarkField, null, null, BookmarkColumn.DEFAULT_SORT_ORDER);
		this._bookmarkcursorAdapter = new bookmarkCursorAdapter(this,
				bookmarkCursor);
		setListAdapter(_bookmarkcursorAdapter);

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
			String bookmarktitle = cursor.getString(cursor
					.getColumnIndex(BookmarkColumn.NAME));
			int strChapter = cursor.getInt(cursor
					.getColumnIndex(BookmarkColumn.CHAPTER));
			int pageno = cursor.getInt(cursor
					.getColumnIndex(BookmarkColumn.PAGE));
			int totalPage = cursor.getInt(cursor
					.getColumnIndex(BookmarkColumn.TOTALPAGE));

			float percent = (float) pageno / (float) totalPage;
			long createdate = cursor.getLong(cursor
					.getColumnIndex(BookmarkColumn.CREATEDATE));
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss"); // set the format to sql date time
			Date date = new Date(createdate);
			String strDt = dateFormat.format(date).toString();
			String strpercent = String.valueOf(percent) + "%";
			String strRemark = strpercent + "-Create date:" + strDt;
			viewIcon.setImageResource(R.drawable.btn_bookmark);
			if (tt != null) {
				tt.setText(bookmarktitle);
			}
			if (bt != null) {
				bt.setText(strpercent);
			}
			if (txtRemark != null)
				txtRemark.setText("Chapter:" + strChapter);

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.row_library, parent, false);
			bindView(v, context, cursor);
			return v;

		}
	}

}
