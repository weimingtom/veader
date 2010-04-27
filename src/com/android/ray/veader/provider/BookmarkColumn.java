package com.android.ray.veader.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class BookmarkColumn implements BaseColumns {
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri
            .parse("content://VeaderProvider/bookmark");
/*
	db.execSQL("CREATE TABLE IF NOT EXISTS bookmark (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT (0), "
			+ "name varchar, description varchar, bookid INT, page int,chapter int,type int, dob date, dod date);");
*/
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String BOOKID = "bookid";
    public static final String PAGE = "page";
    public static final String CHAPTER = "chapter";
    public static final String TOTALPAGE = "totalpage";
    public static final String CHAPTERTITLE = "chaptertitle";
    public static final String CREATEDATE= "createdate";
    public static final String TYPE = "type"; //use for top 1:top 0:normal
    public static final String DEFAULT_SORT_ORDER = "createdate desc";
}
