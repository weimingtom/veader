package com.android.ray.veader.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class CatalogColumn implements BaseColumns {
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri
            .parse("content://VeaderProvider/catalog");
/*
	db.execSQL("CREATE TABLE IF NOT EXISTS bookmark (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
			+ "name varchar, description varchar, bookid INT, page int,chapter int,type int, dob date, dod date);");
*/
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String PATH = "path";
    public static final String CATAGORYID = "catagoryid";
    public static final String DEFAULT_SORT_ORDER = "_id";
    
 
}
