package com.yamin.reader.model;
public class Book
{
	private long bookId;					//
	private String bookName;			//
	private String bookAuthor;			//
	private String bookPath;			//
	private String bookAddTime;			//
	private String bookOpenTime;		//
	private int bookCategoryId;			//
	private String bookCategroyName;	//
	private String bookSize;			//
	private String bookProgress;		//
	private String bookCurrent;		//
	private String isFavBook;
	private String isOnlyFavBook;
	
	public long getBookId() {
		return bookId;
	}
	public void setBookId(long bookId) {
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
	public String getBookAuthor()
	{
		return bookAuthor;
	}
	public void setBookAuthor(String bookAuthor)
	{
		this.bookAuthor = bookAuthor;
	}
	public String getBookPath()
	{
		return bookPath;
	}
	public void setBookPath(String bookPath)
	{
		this.bookPath = bookPath;
	}
	public String getBookAddTime()
	{
		return bookAddTime;
	}
	public void setBookAddTime(String bookAddTime)
	{
		this.bookAddTime = bookAddTime;
	}
	public String getBookOpenTime()
	{
		return bookOpenTime;
	}
	public void setBookOpenTime(String bookOpenTime)
	{
		this.bookOpenTime = bookOpenTime;
	}
	public int getBookCategoryId()
	{
		return bookCategoryId;
	}
	public void setBookCategoryId(int bookCategoryId)
	{
		this.bookCategoryId = bookCategoryId;
	}
	public String getBookCategroyName()
	{
		return bookCategroyName;
	}
	public void setBookCategroyName(String bookCategroyName)
	{
		this.bookCategroyName = bookCategroyName;
	}
	public String getBookSize()
	{
		return bookSize;
	}
	public void setBookSize(String bookSize)
	{
		this.bookSize = bookSize;
	}
	public String getBookProgress()
	{
		return bookProgress;
	}
	public void setBookProgress(String bookProgress)
	{
		this.bookProgress = bookProgress;
	}
	public String getBookCurrent() {
		return bookCurrent;
	}
	public void setBookCurrent(String bookCurrent) {
		this.bookCurrent = bookCurrent;
	}
	public String getIsFavBook() {
		return isFavBook;
	}
	public void setIsFavBook(String isFavBook) {
		this.isFavBook = isFavBook;
	}
	public String getIsOnlyFavBook() {
		return isOnlyFavBook;
	}
	public void setIsOnlyFavBook(String isOnlyFavBook) {
		this.isOnlyFavBook = isOnlyFavBook;
	}
	
}
