package com.yamin.reader.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class BookContentProvider extends ContentProvider
{
	private SQLiteDatabase db;
	private final static int DB_VERSION = 2;
	@Override
	public boolean onCreate()
	{
		BookOpenHelper bookOpenHelper = new BookOpenHelper(this.getContext(), null, null, DB_VERSION);
		db = bookOpenHelper.getWritableDatabase();
		if(db==null)
			return true;
		else
			return false;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		return db.query(uri.getLastPathSegment(), projection, selection, selectionArgs, null, null, sortOrder);
	}

	@Override
	public String getType(Uri uri)
	{
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		long id = db.insert(uri.getLastPathSegment(), null, values);
		return ContentUris.withAppendedId(uri, id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		return db.delete(uri.getLastPathSegment(), selection, selectionArgs);
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		return db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
	}

}
