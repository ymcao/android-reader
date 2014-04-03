package com.yamin.reader.database;

public class DbTags
{	
	//
	public final static String DB_NAME = "readerbook.db";
	
	//
	public final static String TABLE_BOOK_INFO = "book_info";
	public final static String TABLE_BOOK_CATEGORY = "book_category";
	public final static String TABLE_BOOK_FAV = "book_fav";
	
	//
	public final static String FIELD_BOOK_ID = "book_id";
	public final static String FIELD_BOOK_NAME = "book_name";
	public final static String FIELD_BOOK_AUTHOR = "book_author";
	public final static String FIELD_BOOK_PATH = "book_path";
	public final static String FIELD_BOOK_ADD_TIME = "book_add_time";
	public final static String FIELD_BOOK_OPEN_TIME = "book_open_time";
	public final static String FIELD_BOOK_CATEGORY_ID = "book_category_id";
	public final static String FIELD_BOOK_CATEGORY_NAME = "book_category_name";
	public final static String FIELD_BOOK_SIZE = "book_size";
	public final static String FIELD_BOOK_FAV="book_is_fav";
	public final static String FIELD_BOOK_PROGRESS = "book_progress";
	public final static String FIELD_BOOK_MARK_ID = "book_mark_id";
	public final static String FIELD_BOOK_MARK_ADD_TIME= "book_mark_add_time";
	public final static String FIELD_BOOK_MARK_PROGRESS = "book_mark_progress";
	public final static String FIELD_BOOK_MARK_BEGIN_POSITION = "book_mark_begin_position";
	public final static String FIELD_BOOK_MARK_DETAIL = "book_mark_detail";
	public final static String FIELD_BOOK_BEGIN_POSITION = "book_begin_position";
	public final static String FIELD_BOOK_CHAPTER_NAME = "book_chapter_name";
	public final static String FIELD_BOOK_CHAPTER_BEGIN_POSITION = "book_chapter_begin_position";
	
	//provider
	public final static String DB_PROVIDER = "content://com.yamin.reader.BookContentProvider";
	public final static String URI_TABLE_BOOK_INFO = DB_PROVIDER+"/"+TABLE_BOOK_INFO;
	public final static String URI_TABLE_BOOK_CATEGORY = DB_PROVIDER+"/"+TABLE_BOOK_CATEGORY;
	public final static String URI_TABLE_BOOK_FAV = DB_PROVIDER+"/"+TABLE_BOOK_FAV;
}
