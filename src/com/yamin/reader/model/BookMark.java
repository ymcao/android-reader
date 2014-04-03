package com.yamin.reader.model;

public class BookMark
{
	private int bookMarkId;//
	private int bookId;//
	private String bookName;//
	private String bookPath;//
	private String bookMarkAddTime;//
	private String bookMarkProgress;//
	private String bookMarkDetail;//
	private int bookMarkBeginPosition;//
	public String getBookMarkDetail()
	{
		return bookMarkDetail;
	}
	public void setBookMarkDetail(String bookMarkDetail)
	{
		this.bookMarkDetail = bookMarkDetail;
	}
	public int getBookMarkBeginPosition()
	{
		return bookMarkBeginPosition;
	}
	public void setBookMarkBeginPosition(int bookMarkBeginPosition)
	{
		this.bookMarkBeginPosition = bookMarkBeginPosition;
	}
	public int getBookMarkId()
	{
		return bookMarkId;
	}
	public void setBookMarkId(int bookMarkId)
	{
		this.bookMarkId = bookMarkId;
	}
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
	public String getBookMarkProgress()
	{
		return bookMarkProgress;
	}
	public void setBookMarkProgress(String bookMarkProgress)
	{
		this.bookMarkProgress = bookMarkProgress;
	}
	public String getBookPath()
	{
		return bookPath;
	}
	public void setBookPath(String bookPath)
	{
		this.bookPath = bookPath;
	}
	public String getBookMarkAddTime()
	{
		return bookMarkAddTime;
	}
	public void setBookMarkAddTime(String bookMarkAddTime)
	{
		this.bookMarkAddTime = bookMarkAddTime;
	}
	
}
