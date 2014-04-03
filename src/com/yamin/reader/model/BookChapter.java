package com.yamin.reader.model;

public class BookChapter
{
	private int bookId;//ID
	private String bookName;//
	private String bookChapterName;//
	private String bookChapterBeginPosition;//
	public int getBookId()
	{
		return bookId;
	}
	public void setBookId(int bookId)
	{
		this.bookId = bookId;
	}
	public String getBookName()
	{
		return bookName;
	}
	public void setBookName(String bookName)
	{
		this.bookName = bookName;
	}
	public String getBookChapterName()
	{
		return bookChapterName;
	}
	public void setBookChapterName(String bookChapterName)
	{
		this.bookChapterName = bookChapterName;
	}
	public String getBookChapterBeginPosition()
	{
		return bookChapterBeginPosition;
	}
	public void setBookChapterBeginPosition(String bookChapterBeginPosition)
	{
		this.bookChapterBeginPosition = bookChapterBeginPosition;
	}
}
