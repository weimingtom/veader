package com.android.ray.veader;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.android.ray.veader.util.Constatnts;

public class DataHelper {

   private static class OpenHelper extends SQLiteOpenHelper {

      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
        // db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY, name TEXT)");
    	  db.beginTransaction();
  		db.execSQL("CREATE TABLE IF NOT EXISTS catalog (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
  						+ "path varchar, name varchar, description varchar, catagoryid varchar);");
  		db.execSQL("CREATE TABLE IF NOT EXISTS books (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
  						+ "path varchar, description varchar, lastoffset int, lastpage int, encode varchar, author varchar" 
  						+ ",name varchar, size INT, rating int, replace int, format int,wordcount int, authorid int, catalogid INT,createdate long);");
  		db.execSQL("CREATE TABLE IF NOT EXISTS author (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
  						+ "name varchar, description varchar,  dob date, dod date);");
  		db.execSQL("CREATE TABLE IF NOT EXISTS bookmark (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
  						+ "name varchar,totalpage numeric, description varchar, bookid INT, page int,chapter int,type int,chaptertitle varchar, createdate date default CURRENT_DATE);");
  		db.execSQL("CREATE TABLE IF NOT EXISTS veadersys (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
					+ "userlang varchar,readerlang numeric, fontsize INT, fontcolor INT, fixorientation int,version int, reserve1 int, reserve2 int, createdate date default CURRENT_DATE);");
	
  		db.setTransactionSuccessful();
  		db.endTransaction();
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         Log.w("Example", "Upgrading database, this will drop tables and recreate.");
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
         onCreate(db);
      }
   }
   private static final String DATABASE_NAME = "reader.db";
   private static final int DATABASE_VERSION = 1;

   private static final String TABLE_NAME = "catalog";
   private Context context;

   private SQLiteDatabase db;

   private SQLiteStatement insertStmt;

   private static final String INSERT = "insert into " + TABLE_NAME + "(path, description, name, _id) values (?, ?, ?, ?)";
   
   public DataHelper(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
    // this.db = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
      
   }

	public void deleteAll() {
	      //this.db.delete(TABLE_NAME, null, null);
	   }

	public ArrayList<Library> getAll() {
		   ArrayList<Library> list = new ArrayList<Library>();
		      
		      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "name" ,"description","path", "_id"}, null, null, null, null, "name desc");
		      if (cursor.moveToFirst()) {
		         do {
		        	 Library _lib = new Library();
		        	 _lib.setname(cursor.getString(0));
		        	 _lib.setDescription(cursor.getString(1));
		        	 _lib.setpath(cursor.getString(2));
		        	 _lib.setid(cursor.getInt(3));
		            list.add(_lib); 
		         } while (cursor.moveToNext());
		      }
		      if (cursor != null && !cursor.isClosed()) {
		         cursor.close();
		      }
		      return list;
		   }
   public ArrayList<Books> getAllBooks() {
	   ArrayList<Books> list = new ArrayList<Books>();
	      
	      Cursor cursor = this.db.query("books", new String[] { "name" ,"description","path", "_id"}, null, null, null, null, "name desc");
	     
	      if (cursor.moveToFirst()) {
	         do {
	        	 Books _book = new Books();
	        	 _book.setname(cursor.getString(0));
	        	 _book.setDescription(cursor.getString(1));
	        	 _book.setpath(cursor.getString(2));
	        	 _book.setid(cursor.getInt(3));
	            list.add(_book); 
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	   }

   public int getBookCountByCatalogid(int catid){
	    OpenHelper openHelper = new OpenHelper(this.context);
	    if(!this.db.isOpen()) this.db = openHelper.getWritableDatabase();
	 //   db.openOrCreateDatabase(file, factory)
	    Cursor cursor = this.db.rawQuery("select count(*) as count  from books where catalogid =  "+catid, null);
	cursor.moveToNext();
	    return (cursor.getInt(0));
	   
 }
   public ArrayList<Books> getBooksByCatalogID(int _catalogid) {
	   ArrayList<Books> list = new ArrayList<Books>();
	      
	      Cursor cursor = this.db.query("books", new String[] { "name" ,"description","path", "_id"}, "catalogid = "+Integer.toString(_catalogid), null, null, null, "name desc");
	     
	      if (cursor.moveToFirst()) {
	         do {
	        	 Books _book = new Books();
	        	 _book.setname(cursor.getString(0));
	        	 _book.setDescription(cursor.getString(1));
	        	 _book.setpath(cursor.getString(2));
	        	 _book.setid(cursor.getInt(3));
	            list.add(_book); 
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	   }
   public SQLiteDatabase getDb() {
      return this.db;
   }
   public int getMaxLibID(){
	   
	    
	    
	    Cursor cursor = this.db.rawQuery("select max(_id) as id from catalog ", null);
	cursor.moveToNext();
	    return (cursor.getInt(0));
	   
  }
  
   public long insertBook(String path, String description, String title,
		int catalogid, String Encode) {
	String strsql = "insert into books( path, description, name, catalogid, size, wordcount, authorid, Encode) values(?,?,?,?,?,?,?, ?);";

	this.insertStmt = this.db.compileStatement(strsql);

	this.insertStmt.bindString(1, path);
	this.insertStmt.bindString(2, description);
	this.insertStmt.bindString(3, title);
	this.insertStmt.bindLong(4, catalogid);
	this.insertStmt.bindLong(5, -1);
	this.insertStmt.bindLong(6, -1);
	this.insertStmt.bindLong(7, -1);
	this.insertStmt.bindString(8, Encode);
  long Returnval =  this.insertStmt.executeInsert();
	return Returnval;
	//return 1;
}
   public long insertLibrary(String path, String description, String name, long _id) {
	this.insertStmt = this.db.compileStatement(INSERT);
	
	this.insertStmt.bindString(1, path);
	this.insertStmt.bindString(2, description);
	this.insertStmt.bindString(3, name);
	this.insertStmt.bindLong(4, _id);
	return this.insertStmt.executeInsert();
	// return 1;
}
   public boolean pathExists(String _path){
	   
	    //Cursor cursor = this.db.query(TABLE_NAME, new String[] { "name" }, null, null, null, null, "name desc");
	    
	    
	    
	    Cursor cursor = this.db.rawQuery("select count(*) as count from catalog where path = '" +_path+ "'", null);
	cursor.moveToNext();
	    return (cursor.getInt(0)>0);
	   
   }
   public List<String> selectAll() {
      List<String> list = new ArrayList<String>();
      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "name" }, null, null, null, null, "name desc");
      if (cursor.moveToFirst()) {
         do {
            list.add(cursor.getString(0)); 
         } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }
}