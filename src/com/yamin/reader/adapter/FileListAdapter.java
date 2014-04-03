package com.yamin.reader.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yamin.reader.R;
import com.yamin.reader.model.FileItem;



public class FileListAdapter extends BaseAdapter {
	ArrayList<FileItem> items;
	LayoutInflater inflater;
	private Context mContext;
	
	public FileListAdapter(Context context,ArrayList<FileItem> items){
		mContext=context;

		if(items==null){
			items=new ArrayList<FileItem>();
		}
		this.inflater = LayoutInflater.from(context);
		this.items = items;
	}
	
	public void dataChanged(ArrayList<FileItem> items){
		if(items==null){
			items = new ArrayList<FileItem>();
		}
		this.items = items;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.file_item, null);
			holder = new ViewHolder();
			holder.ivFileIcon = (ImageView)convertView.findViewById(R.id.ivFileIcon);
			holder.tvFileName = (TextView)convertView.findViewById(R.id.tvFileName);
			holder.operationLL= (LinearLayout)convertView.findViewById(R.id.fileOperationLL);
			holder.openLL= (LinearLayout)convertView.findViewById(R.id.openFileBtn);
			holder.shelfLL= (LinearLayout)convertView.findViewById(R.id.shelfFileBtn);
			holder.favrioLL= (LinearLayout)convertView.findViewById(R.id.favoriteFileBtn);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		final FileItem item = items.get(position);
		holder.ivFileIcon.setImageDrawable(item.getFileIcon());
		holder.tvFileName.setText(item.getFileName());
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView ivFileIcon;
		TextView tvFileName;
		LinearLayout operationLL;
		LinearLayout openLL;
		LinearLayout shelfLL;
		LinearLayout favrioLL;
	}

}
