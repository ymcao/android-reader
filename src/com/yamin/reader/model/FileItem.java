package com.yamin.reader.model;

import android.graphics.drawable.Drawable;

public class FileItem
{
	public FileItem() {
		super();
	}
	
	
	public FileItem(String fileName, Drawable fileIcon,String filepath,String filesize) {
		super();
		this.fileName = fileName;
		this.fileIcon = fileIcon;
		this.filepath=filepath;
		this.filesize=filesize;
	}


	String fileName;
	Drawable fileIcon;
	String filepath;
	String filesize;
	
	public String getFilepath() {
		return filepath;
	}


	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}



	public String getFilesize() {
		return filesize;
	}


	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}


	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Drawable getFileIcon() {
		return fileIcon;
	}
	public void setFileIcon(Drawable fileIcon) {
		this.fileIcon = fileIcon;
	}
}
