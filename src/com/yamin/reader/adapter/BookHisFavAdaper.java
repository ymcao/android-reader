package com.yamin.reader.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yamin.reader.R;
import com.yamin.reader.database.DbDataOperation;
import com.yamin.reader.model.Book;
import com.yamin.reader.utils.ToolUtils;

public class BookHisFavAdaper extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Book> mData;
	private Context mContext;
	private int[] itemState;
	private boolean isEditMode = false;
	private Button curDel_btn;
	private float x,ux,y;
	private ContentResolver resolver;

	// private boolean actionModeStarted;
	public BookHisFavAdaper(Context context, List<Book> mData) {
		mInflater = LayoutInflater.from(context);
		this.mData = mData;
		mContext = context;
		resolver=context.getContentResolver();
	}

	public List<Book> getmData() {
		return mData;
	}

	public void setmData(ArrayList<Book> smData) {
		this.mData = smData;
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
	public View getView( int position,  View convertView, ViewGroup parent) {
		final int p=position;
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
		    convertView = mInflater.inflate(R.layout.book_fav_histo_item_listview, null);
			holder.img = (RelativeLayout) convertView.findViewById(R.id.cover);
			holder.title = (TextView) convertView.findViewById(R.id.tvBookName);
			holder.title1 = (TextView) convertView.findViewById(R.id.tvBookSize);
			holder.title2 = (TextView) convertView.findViewById(R.id.tvBookDate);
			holder.btnDel = (Button) convertView.findViewById(R.id.del);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(ToolUtils.returnSuffix(mData.get(position).getBookName()).contains(".txt")){
			holder.img.setBackground(mContext.getResources().getDrawable(R.drawable.listview_txtcover));
		}
		else if(ToolUtils.returnSuffix(mData.get(position).getBookName()).contains(".epub")){
			holder.img.setBackground(mContext.getResources().getDrawable(R.drawable.listview_epubcover));
		}
		else if(ToolUtils.returnSuffix(mData.get(position).getBookName()).contains(".html")){
			holder.img.setBackground(mContext.getResources().getDrawable(R.drawable.listview_htmlcover));
		}
		else if(ToolUtils.returnSuffix(mData.get(position).getBookName()).contains(".oeb")){
			holder.img.setBackground(mContext.getResources().getDrawable(R.drawable.listview_oebicon));
		}
		else if(ToolUtils.returnSuffix(mData.get(position).getBookName()).contains(".mobi")){
			holder.img.setBackground(mContext.getResources().getDrawable(R.drawable.listview_mobiicon));
		}
		else{
			holder.img.setBackground(mContext.getResources().getDrawable(R.drawable.listview_othercover));
		}
		convertView.setBackgroundResource(R.drawable.mm_listitem_simple); 
		convertView.setOnTouchListener(new OnTouchListener() {
			//
			public boolean onTouch(View v, MotionEvent event) {
		        //
				final ViewHolder holder = (ViewHolder) v.getTag();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					//
					x = event.getX();
					if (curDel_btn != null) {
						curDel_btn.setVisibility(View.GONE);
					}
					v.setBackgroundResource(R.drawable.list_focused_holo);  
					
				} else if (event.getAction() == MotionEvent.ACTION_UP) {//״
					//
					ux = event.getX();
					if (holder.btnDel != null) {
						
						if (Math.abs(x - ux) > 20) {
							holder.btnDel.setVisibility(View.VISIBLE);
							curDel_btn = holder.btnDel;
						}
						 v.setBackgroundResource(R.drawable.mm_listitem_simple);  
					}
				}  else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					
					  v.setBackgroundResource(R.drawable.list_focused_holo);  
					  
					 } else {
					  v.setBackgroundResource(R.drawable.mm_listitem_simple);  
					}  
				return false;
			}
		});
		//
		holder.title.setText(mData.get(position).getBookName());
		holder.title1.setText(mData.get(position).getBookSize());
		holder.title1.setTextColor(Color.RED);
		holder.title2.setText(mData.get(position).getBookAddTime());
		holder.btnDel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(curDel_btn!=null)
					curDel_btn.setVisibility(View.GONE);
				    DbDataOperation.deleteFavBook(resolver, mData.get(p).getBookName());
				    mData.remove(p);
				    notifyDataSetChanged();
			}
		});
		convertView.setTag(holder);
		return convertView;
	}

	public final class ViewHolder {
		public RelativeLayout img;
		public TextView title;
		public TextView title1;
		public TextView title2;
		public Button btnDel;
	}
}