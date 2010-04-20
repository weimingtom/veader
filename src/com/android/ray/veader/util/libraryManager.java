package com.android.ray.veader.util;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.android.ray.veader.R;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
public class libraryManager extends Activity {
	Context context;
	public void libraryManager (Context context){
		//this.context = context;
		
	}
public  void add(String path, String description){
	String strSQL;
	SQLiteDatabase myDB=null;
	//Context context = null;
	//String dbname = clsGlobalsettings.dbname;
	//myDB.openDatabase(path, factory, flags)
	//String dbname = getResources().getText(R.string.dbname).toString();
	//myDB = context.openOrCreateDatabase(dbname, context.MODE_PRIVATE , null);
	strSQL = "insert into catalog(path, description) values('" +path + "', '" + description+ "';";
	//myDB.execSQL(strSQL);
}

}
