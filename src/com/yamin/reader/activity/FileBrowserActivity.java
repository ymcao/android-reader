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
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
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
import com.yamin.reader.view.LoadingDialog;

public class FileBrowserActivity extends Activity implements
		View.OnClickListener{
	/** Called when the activity is first created. */
	private ArrayList<FileItem> items;
	private ArrayList<String> searchitems;
	private FileListAdapter adapter;
	private File current_dir;
	private ListView lvFiles;
	private TextView pathTextView;
	private ImageView upImage;
	private static LoadingDialog loadDialog;
	private PopupWindow mPopuwindow;
	private LinearLayout openFileBtn;
	private LinearLayout shelfFileBtn;
	private LinearLayout favoriteFileBtn;
	private FBReaderApp myFBReaderApp;
	private ContentResolver resolver;

	private final String ROOT_PATH = Environment.getExternalStorageDirectory()
			.getPath();
	// private LinearLayout empty_viewLayout;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				showLoading(FileBrowserActivity.this, "搜索中...");
				break;
			case 1:
				stopLoading();
				if (searchitems != null && searchitems.size() > 0) {
					Intent i = new Intent(FileBrowserActivity.this,
							FileSearchResultActivity.class);
					i.putStringArrayListExtra("SEARCH_LIST", searchitems);
					startActivity(i);
				} else {
					Toast.makeText(FileBrowserActivity.this, "未匹配到文件！",
							Toast.LENGTH_LONG).show();
				}
				break;
			//
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = this.getActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowHomeEnabled(true);
		bar.setTitle(R.string.book_file_manager);
		setContentView(R.layout.file_list);
		resolver=getContentResolver();
		
		myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(FileBrowserActivity.this,
					new BookCollectionShadow());
		}
		getCollection().bindToService(this, null);
		searchitems = new ArrayList<String>();
		lvFiles = (ListView) findViewById(R.id.file_path_list);
		pathTextView = (TextView) findViewById(R.id.current_path_view);
		upImage = (ImageView) findViewById(R.id.path_pane_up_level);
		// empty_viewLayout=(LinearLayout)findViewById(R.id.empty_view);
		
		//
		browseTo(new File(ROOT_PATH));
		adapter = new FileListAdapter(this, items);
		lvFiles.setAdapter(adapter);
		addListener();
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// Because this activity has set launchMode="singleTop", the system
		// calls this method
		// to deliver the intent if this actvity is currently the foreground
		// activity when
		// invoked again (when the user executes a search from this activity, we
		// don't create
		// a new instance of this activity, so the system delivers the search
		// intent here)
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// handles a click on a search suggestion; launches activity to show
			// word
			// Intent wordIntent = new Intent(this, WordActivity.class);
			// wordIntent.setData(intent.getData());
			// startActivity(wordIntent);
			// finish();
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String pattern = intent.getStringExtra(SearchManager.QUERY);
			showQuery(pattern);
		}
	}

	//
	private void showQuery(String query) {
		Message message = new Message();
		message.what = 0;
		mHandler.sendMessage(message);
		new searchThread(query).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bookfile_menu, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
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
			Intent i = new Intent(this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
		default:
			return false;
		}
	}

	private void addListener() {
		upImage.setOnClickListener(this);
		lvFiles.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long arg3) {
				//
				FileItem fi = (FileItem) adapter.getItem(position);
				String fileName = fi.getFileName();
				File file = new File(current_dir, fileName);
				Log.i("life", file.getAbsolutePath());
				if (isValidFileOrDir(file)) {
					showPopupWindow(view,position);
				} else {
					browseTo(new File(current_dir, fileName));
				}
			}

		});
	}

	private boolean isValidFileOrDir(File file) {
		if (file.isDirectory()) {
			return false;
		} else {
			String fileName = file.getName().toLowerCase();
			if (fileName.endsWith(".txt") || fileName.endsWith(".html")
					|| fileName.endsWith(".mobi") || fileName.endsWith(".oeb")
					|| fileName.endsWith(".epub") || fileName.endsWith(".fb2")) {
				return true;
			}
		}
		return false;
	}

	//
	private void browseUpLevel() {
		if (current_dir.getParent() != null
				&& !current_dir.getParent().equals("/")) {
			browseTo(current_dir.getParentFile());
		}
	}

	private void browseTo(File dir) {

		if (dir.isDirectory()) {
			pathTextView.setText(dir.getAbsolutePath());
			this.current_dir = dir;
			fill(current_dir.listFiles());
		}

	}

	private void fill(File[] files) {
		//
		if (items == null) {
			items = new ArrayList<FileItem>();
		}
		//
		items.clear();
		Resources res = getResources();

		if (files != null) {
			for (File file : files) {
				//
				String fileName = file.getName().toLowerCase();
				Drawable icon = null;
				if (file.isDirectory()) {
					icon = res.getDrawable(R.drawable.folder);
					FileItem item = new FileItem(fileName, icon,
							file.getAbsolutePath(),
							ToolUtils.FormetFileSize(file.length()));
					items.add(item);
				} else {
					if (ToolUtils.checkEnd(fileName,
							res.getStringArray(R.array.epubFile))) {
						icon = res.getDrawable(R.drawable.file_epub);
					} else if (ToolUtils.checkEnd(fileName,
							res.getStringArray(R.array.webFile))) {
						icon = res.getDrawable(R.drawable.file_html);
					} else if (ToolUtils.checkEnd(fileName,
							res.getStringArray(R.array.txtFile))) {
						icon = res.getDrawable(R.drawable.file_txt);
					} else if (ToolUtils.checkEnd(fileName,
							res.getStringArray(R.array.oebFile))) {
						icon = res.getDrawable(R.drawable.file_oeb);
					} else if (ToolUtils.checkEnd(fileName,
							res.getStringArray(R.array.mobiFile))) {
						icon = res.getDrawable(R.drawable.file_mobi);
					}
					if (isValidFileOrDir(file)) {
						FileItem item = new FileItem(fileName, icon,
								file.getAbsolutePath(),
								ToolUtils.FormetFileSize(file.length()));
						items.add(item);
					}
				}

			}
		}
		if (adapter != null)
			adapter.dataChanged(items);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.path_pane_up_level:
			browseUpLevel();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//
			if (current_dir.getParent().equals("/")) {
				finish();
			} else {
				browseUpLevel();
			}
		}
		return false;
	}

	// 递归扫描文件夹
	public void GetFiles(File filePath, String query) {
		File[] files = filePath.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					GetFiles(files[i], query);
				} else {
					if (files[i].getName().toLowerCase().contains(query)) {
						searchitems.add(files[i].getAbsolutePath());
						Log.i("MAIN", files[i].getPath());
					}
				}
			}
		}
	}

	class searchThread extends Thread {
		String query;

		public searchThread(String query) {
			this.query = query;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			File f = new File(ROOT_PATH);
			GetFiles(f, query);
			Message message = new Message();
			message.what = 1;
			mHandler.sendMessage(message);
		}
	}

	/*
	 * 
	 */
	public static void showLoading(Activity activity, String msg) {
		if (loadDialog == null) {
			loadDialog = LoadingDialog.createDialog(activity, msg);
			loadDialog.setCanceledOnTouchOutside(false);
		}
		loadDialog.show();
	}

	private static void stopLoading() {
		if (loadDialog != null) {
			loadDialog.dismiss();
			loadDialog = null;
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
					CoreReadActivity.openBookActivity(FileBrowserActivity.this, book, null);
					FileBrowserActivity.this.overridePendingTransition(R.anim.activity_enter,
							R.anim.activity_exit);
					FileBrowserActivity.this.finish();
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
					Toast.makeText(FileBrowserActivity.this, "成功添加到书架", Toast.LENGTH_LONG).show();
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
				book.setBookProgress("00%");
				book.setBookSize(items.get(p).getFilesize());
				Date date=new Date();
				//
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String bookAddTime=formatter.format(date);
				book.setBookAddTime(bookAddTime);
				
				Book b = DbDataOperation.queryBookFav(resolver,DbTags.FIELD_BOOK_NAME,filename);
				if (b == null) {
					DbDataOperation.insertToBookFav(resolver, book);
					Toast.makeText(FileBrowserActivity.this, "成功收藏！", Toast.LENGTH_LONG).show();
				}
			}
		});
		ColorDrawable cd = new ColorDrawable(-0000);
		mPopuwindow.setBackgroundDrawable(cd);
		mPopuwindow.setBackgroundDrawable(cd);
		mPopuwindow.setOutsideTouchable(true);
		mPopuwindow.setFocusable(true);
		// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		mPopuwindow.showAsDropDown(v);

	}

	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow) myFBReaderApp.Collection;
	}

	@Override
	protected void onDestroy() {
		getCollection().unbind();
		Log.i("MAIN", "onDestroy()");
		if(mPopuwindow!=null&&mPopuwindow.isShowing()){
			mPopuwindow.dismiss();
		}
		
		super.onDestroy();
	}

}