package com.yamin.reader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BookOpenHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "readerbook.db";
	//
	public BookOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DB_NAME, factory, version);
	}
	//
	@Override
	public void onCreate(SQLiteDatabase db) {
		//
		String createBookInfoSql = "CREATE TABLE [book_info] ("
				+ "[book_id] INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "[book_name] VARCHAR,"
				+ "[book_author] VARCHAR, "
				+ "[book_path] VARCHAR, "
				+ "[book_add_time] VARCHAR,"
				+ "[book_open_time] VARCHAR, "
				+ "[book_is_fav] VARCHAR, "
				+ "[book_category_id] INTEGER CONSTRAINT [book_category_fk] REFERENCES [book_category]([book_category_id]),"
				+ "[book_category_name] VARCHAR," 
				+ "[book_size] VARCHAR, "
				+ "[book_progress] VARCHAR," + "[book_isHasDumped] INTEGER  DEFAULT (0),"
				+ "[book_begin_position] INTEGER NOT NULL DEFAULT (0));";
		
		String createBookCategorySql = "CREATE TABLE [book_category] ("
				+ "[book_category_id] INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "[book_category_name] VARCHAR, " + "[book_id] VARCHAR, "
				+ "[book_name] VARCHAR);";
		String createBookFavSql = "CREATE TABLE [book_fav] ("
			+ "[book_category_id] INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "[book_name] VARCHAR, " 
			+ "[book_size] VARCHAR, "
			+ "[book_add_time] VARCHAR,"
			+ "[book_path] VARCHAR);";
		//
		db.execSQL(createBookInfoSql);
		db.execSQL(createBookCategorySql);
		db.execSQL(createBookFavSql);
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String dropBookInfoSql = "drop table if exists book_info";
		String dropBookCategorySql = "drop table if exists book_category";
		String dropBookFavSql = "drop table if exists book_fav";

		db.execSQL(dropBookInfoSql);
		db.execSQL(dropBookCategorySql);
		db.execSQL(dropBookFavSql);
		

		onCreate(db);
	}

}