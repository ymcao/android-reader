package com.yamin.reader.adapter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.yamin.reader.R;

public class PopGalleryAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<String> wallPapers = new LinkedList();
	private Context context;
	private int userIndex;

	public PopGalleryAdapter(Context paramContext, String paramString1,
			Gallery paramGallery, String paramString2) {
		this.context = paramContext;
		this.inflater = LayoutInflater.from(paramContext);
		try {
			for (String str : paramContext.getAssets().list(paramString1))
				this.wallPapers.add(paramString1 + "/" + str);
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
			this.userIndex = this.wallPapers.indexOf(paramString2);
		}
	}

	public int getCount() {
		return this.wallPapers.size();
	}

	public Object getItem(int paramInt) {
		return this.wallPapers.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public int getUserIndex() {
		return this.userIndex;
	}

	public View getView(int paramInt, View convertView, ViewGroup paramViewGroup) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.pop_gallery_item, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.bgCover);
			holder.choose = (ImageView) convertView.findViewById(R.id.bgSelectIcon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Drawable localDrawable = null;
		try {
			localDrawable = Drawable.createFromStream(this.context.getAssets().open((String)this.wallPapers.get(paramInt)), "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.img .setImageDrawable(localDrawable);
	     if (paramInt == this.userIndex)
	    	 holder.choose.setVisibility(View.VISIBLE);
		return convertView;
	}

	public void setUserIndex(int paramInt) {
		this.userIndex = paramInt;
	}

	public final class ViewHolder {
		private ImageView choose = null;
		private ImageView img = null;

		public ViewHolder() {
		}
	}
}