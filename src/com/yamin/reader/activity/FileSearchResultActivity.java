package com.yamin.reader.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.yamin.reader.R;
import com.yamin.reader.adapter.FileListAdapter;
import com.yamin.reader.database.DbDataOperation;
import com.yamin.reader.database.DbTags;
import com.yamin.reader.model.Book;
import com.yamin.reader.model.FileItem;
import com.yamin.reader.utils.ToolUtils;

public class FileSearchResultActivity extends Activity {
	private ListView resultLV;
	private TextView resultTV;
	private ArrayList<String> searchitems;
	private ArrayList<FileItem> items;
	private FileListAdapter adapter;
	private FileItem item;
	private PopupWindow mPopuwindow;
	private LinearLayout openFileBtn;
	private LinearLayout shelfFileBtn;
	private LinearLayout favoriteFileBtn;
	private FBReaderApp myFBReaderApp;
	private ContentResolver resolver;
	SearchManager searchManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActionBar bar = this.getActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowHomeEnabled(true);
		bar.setTitle(R.string.book_file_manager);
		bar.setSubtitle("搜索结果");
		setContentView(R.layout.book_search_result);
		resolver=getContentResolver();
		myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(FileSearchResultActivity.this,
					new BookCollectionShadow());
		}
		getCollection().bindToService(this, null);
		resultLV=(ListView)findViewById(R.id.bookSearchList);
		resultTV=(TextView)findViewById(R.id.bookResultNum);
		searchitems=new ArrayList<String>();
		items=new ArrayList<FileItem>();
		Intent i=this.getIntent();
		if(i!=null){
			searchitems=i.getStringArrayListExtra("SEARCH_LIST");

		if(searchitems!=null&&searchitems.size()>0){
			for(int j=0;j<searchitems.size();j++){
				String path=searchitems.get(j);
				File f=new File(path);
				item=new FileItem(f.getName(),ToolUtils.getIcon(FileSearchResultActivity.this, f.getName())
						,f.getPath()
						,ToolUtils.FormetFileSize(f.length()));
				items.add(item);
			}	
		}
		adapter=new FileListAdapter(FileSearchResultActivity.this, items);
		resultLV.setAdapter(adapter);
		resultTV.setText("共为您搜索到"+items.size()+"个文件");
		resultTV.setTextColor(this.getResources().getColor(R.color.holo_blue_light));
		resultLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long arg3) {
				
				 showPopupWindow(view,position);
			}
		});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bookfile_menu, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(
					R.id.menu_file_search).getActionView();
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_file_search:
			onSearchRequested();
			return true;
		case android.R.id.home:
			Intent i = new Intent(this, FileBrowserActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
		default:
			return false;
		}
	}
	/*
	 * @弹出POPU MENU
	 */
	public void showPopupWindow(View v,int position) {
		final int p=position;
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.file_item_menu, null);
		mPopuwindow = new PopupWindow(layout,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		openFileBtn = (LinearLayout) layout.findViewById(R.id.openFileBtn);
		shelfFileBtn = (LinearLayout) layout.findViewById(R.id.shelfFileBtn);
		favoriteFileBtn = (LinearLayout) layout.findViewById(R.id.favoriteFileBtn);
		openFileBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ZLFile file = ZLFile.createFileByPath(items.get(p).getFilepath());
				org.geometerplus.fbreader.book.Book book = getCollection().getBookByFile(file);
				if (book != null) {
					CoreReadActivity.openBookActivity(FileSearchResultActivity.this, book, null);
					FileSearchResultActivity.this.overridePendingTransition(R.anim.activity_enter,
							R.anim.activity_exit);
					FileSearchResultActivity.this.finish();
				}
			}
		});
		shelfFileBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String filename = items.get(p).getFileName();
				String filepath = items.get(p).getFilepath();
				// Local Book---Easy Book
				Book book = new Book();
				book.setBookName(filename);
				book.setBookPath(filepath);
				book.setBookSize(items.get(p).getFilesize());
				Book b = DbDataOperation.queryBook(resolver,DbTags.FIELD_BOOK_NAME,filename);
				if (b == null) {
					DbDataOperation.insertToBookInfo(resolver, book);
					Toast.makeText(FileSearchResultActivity.this, "成功添加到书架", Toast.LENGTH_LONG).show();
				}
			}
		});
		favoriteFileBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String filename = items.get(p).getFileName();
				String filepath = items.get(p).getFilepath();
				// Local Book---Easy Book
				Book book = new Book();
				book.setBookName(filename);
				book.setBookPath(filepath);
				book.setBookSize(items.get(p).getFilesize());
				Date date=new Date();
				//
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String bookAddTime=formatter.format(date);
				book.setBookAddTime(bookAddTime);
				
				Book b = DbDataOperation.queryBookFav(resolver,DbTags.FIELD_BOOK_NAME,filename);
				if (b == null) {
					DbDataOperation.insertToBookFav(resolver, book);
					Toast.makeText(FileSearchResultActivity.this, "成功收藏！", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(FileSearchResultActivity.this, "已经存在收藏夹中！", Toast.LENGTH_LONG).show();
				}
			}
		});
		ColorDrawable cd = new ColorDrawable(-0000);
		mPopuwindow.setBackgroundDrawable(cd);
		mPopuwindow.setBackgroundDrawable(cd);
		mPopuwindow.setOutsideTouchable(true);
		mPopuwindow.setFocusable(true);
		// showAsDropDown
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		mPopuwindow.showAsDropDown(v);

	}

	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow) myFBReaderApp.Collection;
	}

	@Override
	protected void onDestroy() {
		getCollection().unbind();
		if(mPopuwindow!=null&&mPopuwindow.isShowing()){
			mPopuwindow.dismiss();
		}
		Log.i("MAIN", "onDestroy()");
		super.onDestroy();
	}
}
