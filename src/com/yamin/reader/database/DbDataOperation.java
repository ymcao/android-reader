package com.yamin.reader.database;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.yamin.reader.model.Book;

public class DbDataOperation {

	public static ArrayList<Book> getBookInfo(ContentResolver resolver) {
		ArrayList<Book> bookList = new ArrayList<Book>();
		Book book;

		Cursor cursor = resolver.query(Uri.parse(DbTags.URI_TABLE_BOOK_INFO),
				null, null, null, null);
		while (cursor.moveToNext()) {
			book = new Book();
			book.setBookId(Integer.parseInt(getFieldContent(cursor,
					DbTags.FIELD_BOOK_ID)));
			book.setBookName(getFieldContent(cursor, DbTags.FIELD_BOOK_NAME));
			book.setBookAuthor(getFieldContent(cursor, DbTags.FIELD_BOOK_AUTHOR));
			book.setBookPath(getFieldContent(cursor, DbTags.FIELD_BOOK_PATH));
			book.setBookAddTime(getFieldContent(cursor,
					DbTags.FIELD_BOOK_ADD_TIME));
			book.setBookOpenTime(getFieldContent(cursor,
					DbTags.FIELD_BOOK_OPEN_TIME));
			book.setBookCategoryId(Integer.parseInt(getFieldContent(cursor,
					DbTags.FIELD_BOOK_CATEGORY_ID)));
			book.setBookCategroyName(getFieldContent(cursor,
					DbTags.FIELD_BOOK_CATEGORY_NAME));
			book.setBookSize(getFieldContent(cursor, DbTags.FIELD_BOOK_SIZE));
			book.setBookProgress(getFieldContent(cursor,
					DbTags.FIELD_BOOK_PROGRESS));

			bookList.add(book);
		}
		cursor.close();

		return bookList;
	}

	public static ArrayList<Book> queryFilterBooks(ContentResolver resolver,
			String tag, String key) {
		ArrayList<Book> bookList = new ArrayList<Book>();
		Book book;

		Cursor cursor = resolver.query(Uri.parse(DbTags.URI_TABLE_BOOK_INFO),
				null, tag + "=?", new String[] { key + "" }, null);
		while (cursor.moveToNext()) {
			book = new Book();
			book.setBookId(Integer.parseInt(getFieldContent(cursor,
					DbTags.FIELD_BOOK_ID)));
			book.setBookName(getFieldContent(cursor, DbTags.FIELD_BOOK_NAME));
			book.setBookAuthor(getFieldContent(cursor, DbTags.FIELD_BOOK_AUTHOR));
			book.setBookPath(getFieldContent(cursor, DbTags.FIELD_BOOK_PATH));
			book.setBookAddTime(getFieldContent(cursor,
					DbTags.FIELD_BOOK_ADD_TIME));
			book.setBookOpenTime(getFieldContent(cursor,
					DbTags.FIELD_BOOK_OPEN_TIME));
			book.setBookCategoryId(Integer.parseInt(getFieldContent(cursor,
					DbTags.FIELD_BOOK_CATEGORY_ID)));
			book.setBookCategroyName(getFieldContent(cursor,
					DbTags.FIELD_BOOK_CATEGORY_NAME));
			book.setBookSize(getFieldContent(cursor, DbTags.FIELD_BOOK_SIZE));
			book.setBookProgress(getFieldContent(cursor,
					DbTags.FIELD_BOOK_PROGRESS));

			bookList.add(book);
		}
		cursor.close();

		return bookList;
	}

	public static ArrayList<Book> queryBooksFav(ContentResolver resolver) {
		ArrayList<Book> bookList = new ArrayList<Book>();
		Book book;

		Cursor cursor = resolver.query(Uri.parse(DbTags.URI_TABLE_BOOK_FAV),null, null, null, null);
		while (cursor.moveToNext()) {
			book = new Book();

			book.setBookName(getFieldContent(cursor, DbTags.FIELD_BOOK_NAME));
			book.setBookPath(getFieldContent(cursor, DbTags.FIELD_BOOK_PATH));
			book.setBookAddTime(getFieldContent(cursor,DbTags.FIELD_BOOK_ADD_TIME));
			book.setBookSize(getFieldContent(cursor, DbTags.FIELD_BOOK_SIZE));
			bookList.add(book);
		}
		cursor.close();

		return bookList;
	}

	public static void insertToBookInfo(ContentResolver resolver, Book bookinfo) {
		ContentValues values = new ContentValues();
		values.put(DbTags.FIELD_BOOK_NAME, bookinfo.getBookName());
		values.put(DbTags.FIELD_BOOK_AUTHOR, bookinfo.getBookAuthor());
		values.put(DbTags.FIELD_BOOK_PATH, bookinfo.getBookPath());
		values.put(DbTags.FIELD_BOOK_ADD_TIME, bookinfo.getBookAddTime());
		values.put(DbTags.FIELD_BOOK_OPEN_TIME, bookinfo.getBookOpenTime());
		values.put(DbTags.FIELD_BOOK_CATEGORY_ID, bookinfo.getBookCategoryId());
		values.put(DbTags.FIELD_BOOK_CATEGORY_NAME,
				bookinfo.getBookCategroyName());
		values.put(DbTags.FIELD_BOOK_SIZE, bookinfo.getBookSize());
		values.put(DbTags.FIELD_BOOK_PROGRESS, bookinfo.getBookProgress());
		resolver.insert(Uri.parse(DbTags.URI_TABLE_BOOK_INFO), values);
	}

	public static void insertToBookFav(ContentResolver resolver, Book bookinfo) {
		ContentValues values = new ContentValues();
		values.put(DbTags.FIELD_BOOK_NAME, bookinfo.getBookName());
		values.put(DbTags.FIELD_BOOK_PATH, bookinfo.getBookPath());
		values.put(DbTags.FIELD_BOOK_ADD_TIME, bookinfo.getBookAddTime());
		values.put(DbTags.FIELD_BOOK_SIZE, bookinfo.getBookSize());
		resolver.insert(Uri.parse(DbTags.URI_TABLE_BOOK_FAV), values);
	}

	public static Book queryBookFav(ContentResolver resolver, String tag,
			String key) {
		Book book = null;
		Cursor cursor = resolver.query(Uri.parse(DbTags.URI_TABLE_BOOK_FAV),
				null, tag + "=?", new String[] { key + "" }, null);
		if (cursor.moveToNext()) {
			book = new Book();

			book.setBookName(getFieldContent(cursor, DbTags.FIELD_BOOK_NAME));
			book.setBookPath(getFieldContent(cursor, DbTags.FIELD_BOOK_PATH));
			book.setBookAddTime(getFieldContent(cursor,
					DbTags.FIELD_BOOK_ADD_TIME));
			book.setBookSize(getFieldContent(cursor, DbTags.FIELD_BOOK_SIZE));
		}
		cursor.close();
		return book;

	}

	public static void updateValuesToTable(ContentResolver contentResolver,
			String progress, String bookName) {
		ContentValues values = new ContentValues();
		values.put(DbTags.FIELD_BOOK_PROGRESS, progress);
		contentResolver.update(Uri.parse(DbTags.URI_TABLE_BOOK_INFO), values,
				DbTags.FIELD_BOOK_NAME + "=?", new String[] { bookName + "" });
	}
	public static Book queryBook(ContentResolver resolver, String tag,
			String key) {
		Book book = null;
		Cursor cursor = resolver.query(Uri.parse(DbTags.URI_TABLE_BOOK_INFO),
				null, tag + "=?", new String[] { key + "" }, null);
		if (cursor.moveToNext()) {
			book = new Book();
			book.setBookId(Integer.parseInt(getFieldContent(cursor,
					DbTags.FIELD_BOOK_ID)));
			book.setBookName(getFieldContent(cursor, DbTags.FIELD_BOOK_NAME));
			book.setBookAuthor(getFieldContent(cursor, DbTags.FIELD_BOOK_AUTHOR));
			book.setBookPath(getFieldContent(cursor, DbTags.FIELD_BOOK_PATH));
			book.setBookAddTime(getFieldContent(cursor,
					DbTags.FIELD_BOOK_ADD_TIME));
			book.setBookOpenTime(getFieldContent(cursor,
					DbTags.FIELD_BOOK_OPEN_TIME));
			book.setBookCategoryId(Integer.parseInt(getFieldContent(cursor,
					DbTags.FIELD_BOOK_CATEGORY_ID)));
			book.setBookCategroyName(getFieldContent(cursor,
					DbTags.FIELD_BOOK_CATEGORY_NAME));
			book.setBookSize(getFieldContent(cursor, DbTags.FIELD_BOOK_SIZE));
			book.setBookProgress(getFieldContent(cursor,
					DbTags.FIELD_BOOK_PROGRESS));

		}
		cursor.close();
		return book;
	}

	public static String getFieldContent(Cursor cursor, String fieldName) {
		return cursor.getString(cursor.getColumnIndex(fieldName));
	}

	public static void deleteBook(ContentResolver resolver, long bookId) {
		resolver.delete(Uri.parse(DbTags.URI_TABLE_BOOK_INFO),
				DbTags.FIELD_BOOK_ID + "=?", new String[] { bookId + "" });
	}
	public static void deleteFavBook(ContentResolver resolver, String fieldName) {
		resolver.delete(Uri.parse(DbTags.URI_TABLE_BOOK_FAV),
				DbTags.FIELD_BOOK_NAME+ "=?", new String[] { fieldName + "" });
	}
	
}
