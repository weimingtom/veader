/*
 * Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.ray.veader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


import com.android.ray.veader.R;

import com.android.ray.veader.pdb.AbstractBookInfo;
import com.android.ray.veader.provider.BookColumn;
import com.android.ray.veader.util.ColorUtil;
import com.android.ray.veader.util.Constatnts;

/**
 * This example shows how to use choice mode on a list. This list is in
 * CHOICE_MODE_SINGLE mode, which means the items behave like checkboxes.
 */
public class chapterDialog extends ListActivity {
	protected static final String TAG = "chapterDialog";
	private AbstractBookInfo mBook;
	
	private class chapterAdapter extends ArrayAdapter<clsChapter> {

		private ArrayList<clsChapter> items;

		public chapterAdapter(Context context, int textViewResourceId,
				ArrayList<clsChapter> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
TextView tt, bt, txtRemark;
ImageView viewIcon;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
//			View v = view;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_library, null);
			}

			tt = (TextView) v.findViewById(R.id.toptext);
			bt = (TextView) v.findViewById(R.id.bottomtext);
			txtRemark = (TextView) v.findViewById(R.id.txtRemark);
			viewIcon = (ImageView) v.findViewById(R.id.rowicon);
			
		
			clsChapter o = items.get(position);
			if (o != null) {
				viewIcon = (ImageView) v.findViewById(R.id.rowicon);
				viewIcon.setImageResource(R.drawable.ledgreen);
				if (tt != null) {
					tt.setText(o.getTitle());
				}
				if (bt != null) {
					bt.setText(o.getname());
				}
			}
			return v;
		}
	}


	private ArrayList<clsChapter> _chapterlist = null;
	private String path, encode;
	private long  bookid;
    //@override
    public void onCreate(Bundle savedInstanceState) {
    	_chapterlist = new ArrayList<clsChapter>();
        super.onCreate(savedInstanceState);
         bookid= getIntent().getExtras().getLong("ID");
       //  this.bookid = id;
      path = getIntent().getExtras().getString("PATH");
      encode = getIntent().getExtras().getString("ENCODE");
      Log.d("id", String.valueOf(bookid));
     
		File f = new File(path);
		mBook = AbstractBookInfo.newBookInfo(f, bookid);
		try {
		mBook.setEncode(encode);
			mBook.setFile(f);
			
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		String[] _chapters = mBook.getChapters();
		for (String _chap : mBook.mChapterTitles){
			_chapterlist.add((new clsChapter(mBook.mName , _chap)));
			
		}
        setListAdapter(new chapterAdapter(this,
                android.R.layout.simple_list_item_single_choice, _chapterlist));
       
    }
    protected void onListItemClick(ListView l, View v, int position, long id) {
		int selectedRow = (int) id;
		Log.d("selectedID", String.valueOf(id));
		 Bundle bdlChapter = new Bundle();
		 bdlChapter.putLong("CHAPTERID",id); 
         Intent i = new Intent();  
         
          
         
           
         i.putExtras(bdlChapter); 
      
		if (getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey("TARGET")) {
			String strTarget = getIntent().getExtras().getString("TARGET");
			if (strTarget.equals("READ")) {
				Intent intent = new Intent();
				intent.putExtra("ID", this.bookid);
				Log.d("chapter", String.valueOf(id));
				intent.putExtra("CHAPTER", (int)id);
				//intent.putExtra("PAGE", 1);
			//	intent.putExtra("TOTALPAGE", totalPage);
				//intent.putExtra("PERCENT", percent);

				Log.d("BOOKID", String.valueOf(id));
				intent.setClassName(chapterDialog.this, VeaderActivity.class
						.getName());
				startActivity(intent);
			} 
		}else {
			this.setResult(RESULT_OK, i);
			finish();
		}
    }

}
