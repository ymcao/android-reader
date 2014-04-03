package com.yamin.reader.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yamin.reader.R;

public class ScanFileAdapter extends BaseAdapter {

	private ArrayList<FileInfo> mFileLists;
	private LayoutInflater mLayoutInflater = null;
	private int[] itemState;
	private static ArrayList<String> FILE_SUFFIX = new ArrayList<String>();

	static {
		FILE_SUFFIX.add(".txt");
		FILE_SUFFIX.add(".epub");
		FILE_SUFFIX.add(".fb2");
		FILE_SUFFIX.add(".html");
		FILE_SUFFIX.add(".mobi");
		FILE_SUFFIX.add(".oeb");
	}

	public ScanFileAdapter(Context context, ArrayList<FileInfo> fileLists) {
		super();
		mFileLists = fileLists;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//
		if(mFileLists!=null&&mFileLists.size()>0){
		  itemState = new int[mFileLists.size()];
		  for (int i = 0; i < mFileLists.size(); i++) {
			itemState[i] = 0;
		  }
		}
	}

	public int[] getItemState() {
		return itemState;
	}

	public void setItemState(int[] itemState) {
		this.itemState = itemState;
	}
	public void uncheckAll(){
		for(int i=0;i<itemState.length;i++){
			itemState[i] = 0;
		}
	}
	
	public boolean isAllChecked(){
		for(int i :itemState){
			if(i ==0) return false;
		}
		return true;
	}
	
	public void checkAll(){
		for(int i=0;i<itemState.length;i++){
			itemState[i] = 1;
		}
	}
	
	public int getCheckedItemCount(){
		int count = 0;
		for(int i :itemState){
			if(i ==1) count++;
		}
		return count;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFileLists.size();
	}

	@Override
	public FileInfo getItem(int position) {
		// TODO Auto-generated method stub
		return mFileLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			view = mLayoutInflater.inflate(R.layout.search_gridview_item, null);
			holder = new ViewHolder(view);
			/*
			holder.imgFileIcon=(ImageView)view.findViewById(R.id.imgSearchFileIcon);
			holder.imgFileSelectIcon=(ImageView)view.findViewById(R.id.imFileSelectIcon);
			*/
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		FileInfo fileInfo = getItem(position);
		// TODO

		holder.tvFileName.setText(fileInfo.getFileName());

		if (fileInfo.isTXTFile(fileInfo.getFileName())) {
			holder.imgFileIcon.setImageResource(R.drawable.listview_txtcover);
			holder.tvFileName.setTextColor(Color.RED);
		}
		if (fileInfo.isEPUBFile(fileInfo.getFileName())) {
			holder.imgFileIcon.setImageResource(R.drawable.listview_epubcover);
			holder.tvFileName.setTextColor(Color.RED);
		}
		if (fileInfo.isMOBIFile(fileInfo.getFileName())) {
			holder.imgFileIcon.setImageResource(R.drawable.listview_mobiicon);
			holder.tvFileName.setTextColor(Color.RED);
		}
		if (fileInfo.isHTMLFile(fileInfo.getFileName())) {
			holder.imgFileIcon.setImageResource(R.drawable.listview_htmlcover);
			holder.tvFileName.setTextColor(Color.RED);
		}
		if (fileInfo.isOEBFile(fileInfo.getFileName())) {
			holder.imgFileIcon.setImageResource(R.drawable.listview_oebicon);
			holder.tvFileName.setTextColor(Color.RED);
		}
		updateIsChecked(position,holder.imgFileSelectIcon);
		return view;
	}

	static class ViewHolder {
		ImageView imgFileIcon;
		TextView tvFileName;
        ImageView imgFileSelectIcon;
		public ViewHolder(View view) {
			imgFileIcon = (ImageView) view.findViewById(R.id.imgSearchFileIcon);
			tvFileName = (TextView) view.findViewById(R.id.tvFileName);
			imgFileSelectIcon= (ImageView) view.findViewById(R.id.imSearchFileSelectIcon);
		}
	}
  public void updateIsChecked(int position, ImageView select) {
		
		if (itemState[position] == 0) {
			select.setVisibility(View.GONE);
		}else{
			select.setVisibility(View.VISIBLE);
		}
	
	}
	enum FileType {
		FILE, DIRECTORY;
	}

	// =========================
	// Model
	// =========================
	public static class FileInfo {
		private FileType fileType;
		private String fileName;
		private String filePath;
		private long fileSize;

		public FileInfo(String filePath, String fileName,long fileSize, boolean isDirectory) {
			this.filePath = filePath;
			this.fileName = fileName;
			this.fileSize=fileSize;
			fileType = isDirectory ? FileType.DIRECTORY : FileType.FILE;
		}

		public boolean isTXTFile(String fileName) {
			if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
				return false;
			String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
			if (!isDirectory() && fileSuffix.contains(".txt"))
				return true;
			else
				return false;
		}
		public boolean isEPUBFile(String fileName) {
			if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
				return false;
			String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
			if (!isDirectory() && fileSuffix.contains(".epub"))
				return true;
			else
				return false;
		}
		public boolean isHTMLFile(String fileName) {
			if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
				return false;
			String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
			if (!isDirectory() && fileSuffix.contains(".html"))
				return true;
			else
				return false;
		}
		public boolean isMOBIFile(String fileName) {
			if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
				return false;
			String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
			if (!isDirectory() && fileSuffix.contains(".mobi"))
				return true;
			else
				return false;
		}
		public boolean isOEBFile(String fileName) {
			if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
				return false;
			String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
			if (!isDirectory() && fileSuffix.contains(".oeb"))
				return true;
			else
				return false;
		}
		public boolean isFB2File() {
			if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
				return false;
			String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
			if (!isDirectory() && FILE_SUFFIX.contains(fileSuffix))
				return true;
			else
				return false;
		}
		public boolean isDirectory() {
			if (fileType == FileType.DIRECTORY)
				return true;
			else
				return false;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public FileType getFileType() {
			return fileType;
		}

		public void setFileType(FileType fileType) {
			this.fileType = fileType;
		}

		public long getFileSize() {
			return fileSize;
		}

		public void setFileSize(long fileSize) {
			this.fileSize = fileSize;
		}

		@Override
		public String toString() {
			return "FileInfo [fileType=" + fileType + ", fileName=" + fileName
					+ ", filePath=" + filePath + "]";
		}
	}
}
