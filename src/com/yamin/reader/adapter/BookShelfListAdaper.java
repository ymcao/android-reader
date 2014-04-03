package com.yamin.reader.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamin.reader.R;
import com.yamin.reader.model.Book;
import com.yamin.reader.utils.Commons;

public class BookShelfListAdaper extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<Book> mData;
	private int[] itemState;
	private boolean isEditMode = false;

	// private boolean actionModeStarted;
	public BookShelfListAdaper(Context context, ArrayList<Book> mData) {
		mInflater = LayoutInflater.from(context);
		this.mData = mData;
		itemState = new int[mData.size()];
		init();
	}

	public ArrayList<Book> getmData() {
		return mData;
	}

	public void setmData(ArrayList<Book> smData) {
		this.mData =smData;
		itemState = new int[smData.size()];
		init();
	}

	private void init() {

		for (int i = 0; i < mData.size(); i++) {
			itemState[i] = 0;
		}
	}

	public void uncheckAll() {
		for (int i = 0; i < mData.size(); i++) {
			itemState[i] = 0;
		}
	}

	public boolean isAllChecked() {
		for (int i : itemState) {
			if (i == 0)
				return false;
		}
		return true;
	}

	public void checkAll() {
		for (int i = 0; i < itemState.length; i++) {
			itemState[i] = 1;
		}
	}

	public int getCheckedItemCount() {
		int count = 0;
		for (int i : itemState) {
			if (i == 1)
				count++;
		}
		return count;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public int[] getItemState() {
		return itemState;
	}

	public void setItemState(int[] itemState) {
		this.itemState = itemState;
	}

	public boolean isEditMode() {
		return isEditMode;
	}

	public void setEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
	}

	/*
	 * public int[] getItemState() { return itemState; }
	 * 
	 * public void setItemState(int[] itemState) { this.itemState = itemState; }
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// convertView为null的时候初始化convertView。
		if (convertView == null) {
			holder = new ViewHolder();
			if (!Commons.isGridViewMode) {
				convertView = mInflater.inflate(
						R.layout.bookshelf_item_listview, null);
			} else {
				convertView = mInflater.inflate(
						R.layout.bookshelf_tem_gridview, null);
			}
			holder.img = (RelativeLayout) convertView.findViewById(R.id.cover);
			holder.title = (TextView) convertView.findViewById(R.id.tvBookName);
			holder.title1 = (TextView) convertView.findViewById(R.id.tvBookSize);
			holder.title2 = (TextView) convertView.findViewById(R.id.tvBookProgress);
			holder.checked = (ImageView) convertView.findViewById(R.id.bookshelfFileSelectIcon);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(returnSuffix(mData.get(position).getBookName()).contains(".txt")){
			holder.img.setBackgroundResource(R.drawable.listview_txtcover);
		}
		else if(returnSuffix(mData.get(position).getBookName()).contains(".epub")){
			holder.img.setBackgroundResource(R.drawable.listview_epubcover);
		}
		else if(returnSuffix(mData.get(position).getBookName()).contains(".html")){
			holder.img.setBackgroundResource(R.drawable.listview_htmlcover);
		}
		else if(returnSuffix(mData.get(position).getBookName()).contains(".oeb")){
			holder.img.setBackgroundResource(R.drawable.listview_oebicon);
		}
		else if(returnSuffix(mData.get(position).getBookName()).contains(".mobi")){
			holder.img.setBackgroundResource(R.drawable.listview_mobiicon);
		}
		else{
			holder.img.setBackgroundResource(R.drawable.listview_othercover);
		}
		//
		holder.title.setText(mData.get(position).getBookName());
		holder.title1.setText(mData.get(position).getBookSize());
		holder.title1.setTextColor(Color.RED);
		if (TextUtils.isEmpty(mData.get(position).getBookProgress())) {
			holder.title2.setText(R.string.read_no);
		} else {
			holder.title2.setText(mData.get(position).getBookProgress());
		}
		//
		if (isEditMode) {
			holder.checked.setVisibility(View.VISIBLE);
			if (itemState[position] == 0) {
				holder.checked.setBackgroundResource(R.drawable.checkbox_unselect);
			} else {
				holder.checked.setBackgroundResource(R.drawable.checkbox_selected);
			}
		} else {
			holder.checked.setVisibility(View.GONE);
		}
		convertView.setTag(holder);
		return convertView;
	}

	public final class ViewHolder {
		public RelativeLayout img;
		public TextView title;
		public TextView title1;
		public TextView title2;
		public ImageView checked;
	}

	public String returnSuffix(String fileName){
		
		if (fileName.lastIndexOf(".") > 0){
		    String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
		    return fileSuffix;
		}
		return null;
	}
	public String returnName(String fileName){
		
		if (fileName.indexOf(".") > 0){
		    String name = fileName.substring(fileName.indexOf("."));
		    return name;
		}
		return null;
	}
}