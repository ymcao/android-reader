package com.yamin.reader.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.Author;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.language.Language;
import org.geometerplus.zlibrary.core.language.ZLLanguageUtil;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yamin.reader.R;
import com.yamin.reader.adapter.BookHisFavAdaper;
import com.yamin.reader.adapter.BookShelfListAdaper;
import com.yamin.reader.adapter.ScanFileAdapter;
import com.yamin.reader.database.DbDataOperation;
import com.yamin.reader.database.DbTags;
import com.yamin.reader.model.Book;
import com.yamin.reader.utils.Commons;
import com.yamin.reader.utils.ToolUtils;
import com.yamin.reader.view.CustomDialog;
import com.yamin.reader.view.LoadingDialog;

/**
 * 
 * @ClassName: MainActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author ymcao
 * @date 2013-6-22 上午1:21:15
 * 
 */
public class MainActivity extends Activity {

	/** Called when the activity is first created. */
	private ActionBar actionBar;
	//
	private FBReaderApp myFBReaderApp;
	private GridView gdlist;
	private RelativeLayout somethingRl;
	private RelativeLayout nothingRl;
	BookShelfListAdaper mABdapter;
	BookHisFavAdaper favoriteAdapter;
	EditText searchEdit;
	private ContentResolver resolver;
	private ArrayList<Book> shelfData = new ArrayList<Book>();
	private ScanFileAdapter mSearchAdatper;
	//
	private GridView scanFileGridView;
	private TextView numOfTxtFilesTxt;
	private TextView numOfBookFilesTxt;
	private RelativeLayout searchEditRl;
	private ImageView searchDone;
	private ImageView searchAll;
	private ImageView searchCancel;
	private ImageView bookDelete;
	private ImageView bookAll;
	private ImageView bookUnAll;
	//
	private RelativeLayout searchDialoglayout;
	private RelativeLayout detailDialoglayout;
	private RelativeLayout aboutDialoglayout;
	private RelativeLayout alertDialoglayout;
	private RelativeLayout eidtMenulayout;
	private TextView titleTxt;
	private TextView authorTxt;
	private TextView proessTxt;
	private TextView storageTxt;
	private ImageView coverImg;
	private TextView languageTxt;
	private Button openBtn;
	private Button shareBtn;
	private Button favBtn;
	private Button delFavBtn;
	private Button okBtn;
	private Button cancelBtn;
	//
	private ArrayList<ScanFileAdapter.FileInfo> mFileLists;
	private boolean actionModeEdit = false;
	//
	PopupWindow mPopuwindow;
	private Book book;
	private CustomDialog aboutDialog;
	private CustomDialog alertDialog;
	private static LoadingDialog loadDialog;
	private final int BOOK_SHELF = 0;
	private final int BOOK_FAVORITE = 2;
	private final int BOOK_INIT = 3;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case BOOK_SHELF:
				updateLayoutContent();
				break;

			case BOOK_FAVORITE:
				startActivity(new Intent(MainActivity.this,BookFavoriteActivity.class));
				break;
			
			case BOOK_INIT:
				mABdapter.setmData(shelfData);
				mABdapter.notifyDataSetChanged();
				updateView();
			}
			super.handleMessage(msg);
		}
	};

	//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_shelf);

		resolver = getContentResolver();
		mABdapter=new BookShelfListAdaper(this, shelfData);
		intView();
		// handleIntent(getIntent());
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.i("MAIN", "onStart()");
		myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(MainActivity.this,
					new BookCollectionShadow());
		}
		getCollection().bindToService(this, null);
		new sdScanAysnTask(3).execute();
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		new MenuInflater(getApplication()).inflate(R.menu.bookshelf_menu, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_file:
			Intent i = new Intent(MainActivity.this, FileBrowserActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		case R.id.menu_about:
			//
			if (aboutDialog != null)
				aboutDialog.show();
			break;
		case R.id.menu_scan:
			//
			new sdScanAysnTask(0).execute();
			//
			break;
		case R.id.menu_exit:
			//
			if (alertDialog != null)
				alertDialog.show();
			//
			break;

		case R.id.menu_share:
			ToolUtils.shareWithOther(MainActivity.this, Commons.shareMsg);
			break;
		case R.id.menu_edit:
			if (mABdapter != null&&mABdapter.getCount()>0) {
				if(!mABdapter.isEditMode()){
					mABdapter.setEditMode(true);
					mABdapter.notifyDataSetChanged();
					showEditMenuWindow(0,0);
				}else{
					mABdapter.setEditMode(false);
					mABdapter.notifyDataSetChanged();
					if(mPopuwindow!=null&&mPopuwindow.isShowing()){
						mPopuwindow.dismiss();
					}
				}
			}else{
				Toast.makeText(this, "书架中还没有书籍!", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		getCollection().unbind();
		if (mPopuwindow != null && mPopuwindow.isShowing()) {
			mPopuwindow.dismiss();
		}

		super.onDestroy();
	}

	public void intView() {
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		gdlist = (GridView) findViewById(R.id.gdBookshelf);
		gdlist.setAdapter(mABdapter);
		somethingRl = (RelativeLayout) findViewById(R.id.bookshelf_something);
		nothingRl = (RelativeLayout) findViewById(R.id.bookshelf_nothing);

		searchDialoglayout = (RelativeLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.search_dialog, null);
		aboutDialoglayout = (RelativeLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.about_dialog, null);
		alertDialoglayout = (RelativeLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.alert_dialog, null);
		detailDialoglayout = (RelativeLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.book_details_info, null);
		eidtMenulayout= (RelativeLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.bookshelf_edit, null);
		numOfBookFilesTxt=(TextView)eidtMenulayout.findViewById(R.id.numOfBookFiles);
		titleTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_title);
		authorTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_author);
		proessTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_progress);
		storageTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_storage);
		coverImg = (ImageView) detailDialoglayout
				.findViewById(R.id.details_cover);
		languageTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_language);
		okBtn = (Button) alertDialoglayout.findViewById(R.id.okBtn);
		cancelBtn = (Button) alertDialoglayout.findViewById(R.id.cancelBtn);
		//
		searchEditRl = (RelativeLayout) searchDialoglayout
				.findViewById(R.id.searchEditLayout);
		scanFileGridView = (GridView) searchDialoglayout
				.findViewById(R.id.scanFileGridView);
		numOfTxtFilesTxt = (TextView) searchDialoglayout
				.findViewById(R.id.numOfTxtFiles);
		searchDone = (ImageView) searchDialoglayout
				.findViewById(R.id.imgSearchDoneIcon);
		searchAll = (ImageView) searchDialoglayout
				.findViewById(R.id.imgSearchAllIcon);
		searchCancel = (ImageView) searchDialoglayout
				.findViewById(R.id.imgSearchUnAllIcon);
		//
		aboutDialog = new CustomDialog(MainActivity.this, aboutDialoglayout,
				R.style.Theme_dialog);
		aboutDialog.setCanceledOnTouchOutside(true);
		//
		alertDialog = new CustomDialog(MainActivity.this, alertDialoglayout,
				R.style.Theme_dialog);
		alertDialog.setCanceledOnTouchOutside(false);
		//
		mFileLists = new ArrayList<ScanFileAdapter.FileInfo>();
		// search Dialog layout
		setListener();
		//
	}

	/*
	 * private void handleIntent(Intent intent) { if
	 * (Intent.ACTION_VIEW.equals(intent.getAction())) { // handles a click on a
	 * search suggestion; launches activity to show word Intent wordIntent = new
	 * Intent(this, SearchResultActivity.class);
	 * wordIntent.setData(intent.getData()); startActivity(wordIntent); } else
	 * if (Intent.ACTION_SEARCH.equals(intent.getAction())) { // handles a
	 * search query String query = intent.getStringExtra(SearchManager.QUERY);
	 * showResults(query); } }
	 */
	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow) myFBReaderApp.Collection;
	}

	private void setListener() {
		// TODO Auto-generated method stub
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(this, R.array.spinner_menu,
						android.R.layout.simple_spinner_item);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//
		actionBar.setListNavigationCallbacks(spinnerAdapter,
				new DropDownListenser());
		//
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.this.finish();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
		// sacn GridView
		scanFileGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// edit mode
				if (actionModeEdit) {
					if (mSearchAdatper != null) {
						int value = mSearchAdatper.getItemState()[position] == 1 ? 0
								: 1;
						mSearchAdatper.getItemState()[position] = value;
						// mCallback.setSeletedCountShow();
						mSearchAdatper.notifyDataSetChanged();
						updaScanSelectText(Integer.toString(mSearchAdatper
								.getCheckedItemCount()));
					}
				} else {
					//
				}
			}

		});

		scanFileGridView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO
						// Auto-generated method stub
						if (!actionModeEdit) {
							searchEditRl.setVisibility(View.VISIBLE);
							searchEditRl.startAnimation(AnimationUtils
									.loadAnimation(MainActivity.this,
											R.anim.layout_enter));
							actionModeEdit = true;
							Log.i("TAG", "OnLongClickListener()");
							return true;
						}
						return false;
					}

				});

		searchDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mSearchAdatper.getCheckedItemCount() > 0) {
					if (mPopuwindow != null && mPopuwindow.isShowing()) {
						mPopuwindow.dismiss();
					}
					new sdScanAysnTask(1).execute();
				} else {
					Toast.makeText(MainActivity.this, "您还未选择书籍,请先选择书籍!",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		// Select ALl
		searchAll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mSearchAdatper != null) {
					mSearchAdatper.checkAll();
					mSearchAdatper.notifyDataSetChanged();
					updaScanSelectText(Integer.toString(mSearchAdatper
							.getCheckedItemCount()));
					searchAll.setEnabled(false);
					searchAll.setAlpha(0.6f);
					searchCancel.setEnabled(true);
					searchCancel.setAlpha(1f);
				}
			}
		});
		// Diselect All
		searchCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mSearchAdatper != null) {
					if (mSearchAdatper.isAllChecked()) {
						mSearchAdatper.uncheckAll();
						mSearchAdatper.notifyDataSetChanged();
						updaScanSelectText(Integer.toString(mSearchAdatper
								.getCheckedItemCount()));
						searchCancel.setEnabled(false);
						searchCancel.setAlpha(0.6f);
						searchAll.setEnabled(true);
						searchAll.setAlpha(1f);
					}
				}
			}
		});

		gdlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				int p = position;
				Book b = shelfData.get(position);
				// Not In Edit Mode
				if (mABdapter.isEditMode()) {
					if (mABdapter != null) {
						int value = mABdapter.getItemState()[p] == 1 ? 0 : 1;
						mABdapter.getItemState()[p] = value;
						mABdapter.notifyDataSetChanged();
						updaBookSelectText(Integer.toString(mABdapter
								.getCheckedItemCount()));
					}
				}
				// Edit Mode
				else {
					// File Exist
					if (ToolUtils.fileIsExists(b.getBookPath())) {
						//
						// openBook(b.getBookPath());
						ZLFile file = ZLFile.createFileByPath(b.getBookPath());
						org.geometerplus.fbreader.book.Book book = createBookForFile(file);
						if (book != null) {
							titleTxt.setText("书名:" + book.getTitle());
							Log.i("MAIN", b.getBookProgress() + "");
							if ("00%".equals(b.getBookProgress())) {
								proessTxt.setText("进度:未读");

							} else {
								proessTxt.setText("进度:" + b.getBookProgress());
							}
							storageTxt.setText(book.File.getPath() + "");
							String language = book.getLanguage();
							if (!ZLLanguageUtil.languageCodes().contains(
									language)) {
								language = Language.OTHER_CODE;
							}
							final StringBuilder buffer = new StringBuilder();
							final List<Author> authors = book.authors();
							for (Author a : authors) {
								if (buffer.length() > 0) {
									buffer.append(", ");
								}
								buffer.append(a.DisplayName);
							}
							Bitmap cover = ToolUtils.getCover(
									MainActivity.this, book);
							Bitmap bmp = BitmapFactory.decodeResource(
									getResources(), R.drawable.empty_icon);
							if (cover != null) {
								coverImg.setImageBitmap(cover);
							} else {
								coverImg.setImageBitmap(bmp);
							}
							// coverImg;
							authorTxt.setText("作者:" + buffer);
							languageTxt.setText("语言:"
									+ new Language(language).Name);

						}
						showDetailsPopupWindow(0, 0, book);
					} else {
						Toast.makeText(MainActivity.this, "文件已被从SD卡中移除!",
								Toast.LENGTH_LONG).show();
						DbDataOperation.deleteBook(resolver, b.getBookId());
						//
						shelfData = DbDataOperation.getBookInfo(resolver);
						mABdapter.setmData(shelfData);
						mABdapter.notifyDataSetChanged();

					}
				}
				//
			}
		});
	
	}

	//
	private void updaScanSelectText(String msg) {
		numOfTxtFilesTxt.setText("选中" + msg + "个文件！");
	}
	private void updaBookSelectText(String msg) {
		numOfBookFilesTxt.setText("选中" + msg + "个文件！");
	}
	

	public void updateView() {
		// According the data,update view!
		updateLayoutContent();
	}

	private void updateLayoutContent() {
		if (mABdapter != null && mABdapter.getCount() > 0) {
			somethingRl.setVisibility(View.VISIBLE);
			nothingRl.setVisibility(View.GONE);
			;
		} else {
			somethingRl.setVisibility(View.GONE);
			nothingRl.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * private void openBook(String bookPath) { ZLFile file =
	 * ZLFile.createFileByPath(bookPath); org.geometerplus.fbreader.book.Book
	 * book = createBookForFile(file); if (book != null) {
	 * CoreReadActivity.openBookActivity(MainActivity.this, book, null);
	 * MainActivity.this.overridePendingTransition(R.anim.activity_enter,
	 * R.anim.activity_exit); } }
	 */
	private org.geometerplus.fbreader.book.Book createBookForFile(ZLFile file) {
		if (file == null) {
			return null;
		}
		org.geometerplus.fbreader.book.Book book = getCollection()
				.getBookByFile(file);
		if (book != null) {
			return book;
		}
		if (file.isArchive()) {
			for (ZLFile child : file.children()) {
				book = getCollection().getBookByFile(child);
				if (book != null) {
					return book;
				}
			}
		}
		return null;
	}

	public class sdScanAysnTask extends AsyncTask<Integer, Integer, String[]> {
		private int forWhat = 0;

		public sdScanAysnTask(int forWhat) {
			super();
			this.forWhat = forWhat;
		}

		protected void onPreExecute() {
			// 0
			if (forWhat == 0) {
				//
				showLoading(MainActivity.this, "SD卡扫描中...");
				if (mFileLists != null && mFileLists.size() > 0) {
					mFileLists.clear();
				}
			}
			// 1
			if (forWhat == 1) {
				showLoading(MainActivity.this, "正加入书架中...");
			}
			// 2
			if (forWhat == 2) {
				showLoading(MainActivity.this, "正在删除书籍...");
			}
			if (forWhat == 3) {
				// showLoading(MainActivity.this, "初始化书架...");
			}
			super.onPreExecute();
		}

		protected String[] doInBackground(Integer... params) {
			// 0
			if (forWhat == 0) {
				if (!android.os.Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
				}
				GetFiles(Environment.getExternalStorageDirectory());
			}
			// 1
			if (forWhat == 1) {
				DoneScanLocal();
			}
			// 2
			if (forWhat == 2) {
				delLocalShelf();
			}
			if (forWhat == 3) {
				loadShelfData();
			}
			return null;
		}

		protected void onPostExecute(String[] result) {
			// 0
			if (forWhat == 0) {
				stopLoading();
				Toast.makeText(MainActivity.this,
						"扫描完毕，找到" + mFileLists.size() + "个文件",
						Toast.LENGTH_SHORT).show();
				showPopupWindow(0, 0);
			}
			// 1
			if (forWhat == 1) {
				stopLoading();
				shelfData = DbDataOperation.getBookInfo(resolver);
				mABdapter.setmData(shelfData);
				mABdapter.notifyDataSetChanged();
				updateView();
				Toast.makeText(MainActivity.this, "成功添加到书架!",
						Toast.LENGTH_SHORT).show();
			}
			// 2
			if (forWhat == 2) {
				stopLoading();
				shelfData = DbDataOperation.getBookInfo(resolver);
				mABdapter.setmData(shelfData);
				if(mABdapter.isEditMode()){
					mABdapter.setEditMode(false);
				}
				mABdapter.notifyDataSetChanged();
				//
				updateView();
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					mPopuwindow.dismiss();
				}
				Toast.makeText(MainActivity.this, "成功删除书籍!", Toast.LENGTH_SHORT)
						.show();
			}
			if (forWhat == 3) {
				// stopLoading();
				Message message = new Message();
				message.what = BOOK_INIT;
				mHandler.sendMessage(message);
			}
			super.onPostExecute(result);
		}
	}

	private void loadShelfData() {
		shelfData = DbDataOperation.getBookInfo(resolver);
	}

	public void delLocalShelf() {
		Log.i("MAIN", mABdapter.getItemState().length + "");
		Log.i("MAIN", shelfData.size() + "");
		if (mABdapter.getItemState() != null
				&& mABdapter.getItemState().length > 0) {
			for (int i = 0; i < mABdapter.getItemState().length; i++) {
				if (mABdapter.getItemState()[i] == 1) {
					long bookid = shelfData.get(i).getBookId();
					//
					DbDataOperation.deleteBook(resolver, bookid);
					//
					ZLFile file = ZLFile.createFileByPath(shelfData.get(i)
							.getBookPath());
					org.geometerplus.fbreader.book.Book book = createBookForFile(file);
					if (book != null) {
						myFBReaderApp.Collection.removeBook(book, false);
					}
				}
			}
		}
	}

	// search done 按钮
	public void DoneScanLocal() {
		//
		if (mSearchAdatper.getItemState() != null
				&& mSearchAdatper.getItemState().length > 0) {
			for (int i = 0; i < mSearchAdatper.getItemState().length; i++) {
				if (mSearchAdatper.getItemState()[i] == 1) {
					String filename = mFileLists.get(i).getFileName();
					String filepath = mFileLists.get(i).getFilePath();
					long filesize = mFileLists.get(i).getFileSize();
					// Local Book---Easy Book
					book = new Book();
					book.setBookName(filename);
					book.setBookProgress("00%");
					book.setBookPath(filepath);
					book.setBookSize(ToolUtils.FormetFileSize(filesize));
					Book b = DbDataOperation.queryBook(resolver,
							DbTags.FIELD_BOOK_NAME, filename);
					if (b == null) {
						DbDataOperation.insertToBookInfo(resolver, book);
					}
					//
					actionModeEdit = false;
					searchEditRl.setVisibility(View.GONE);
				}
			}
		}
		//
	}

	//
	public void GetFiles(File filePath) {
		File[] files = filePath.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					GetFiles(files[i]);
				} else {
					if (files[i].getName().toLowerCase().endsWith("." + "txt")
							|| files[i].getName().toLowerCase()
									.endsWith("." + "epub")
							|| files[i].getName().toLowerCase()
									.endsWith("." + "mobi")
							|| files[i].getName().toLowerCase()
									.endsWith("." + "fb2")
							|| files[i].getName().toLowerCase()
									.endsWith("." + "oeb")
							|| files[i].getName().toLowerCase()
									.endsWith("." + "html")) {
						ScanFileAdapter.FileInfo fileInfo = new ScanFileAdapter.FileInfo(
								files[i].getAbsolutePath(), files[i].getName(),
								files[i].length(), false);
						Log.i("TAG", files[i].getName());
						// String scanName = files[i].getName();
						if (fileInfo != null) {
							mFileLists.add(fileInfo);
						}
					}
				}
			}
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
	//
	public void showEditMenuWindow(int x, int y) {
		bookDelete=(ImageView)eidtMenulayout.findViewById(R.id.imgDeleteIcon);
		bookAll=(ImageView)eidtMenulayout.findViewById(R.id.imgAllIcon);
	    bookUnAll=(ImageView)eidtMenulayout.findViewById(R.id.imgUnAllIcon);
	    bookDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new sdScanAysnTask(2).execute();
			}
		});
	    bookAll.setOnClickListener(new View.OnClickListener() {
			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mABdapter.checkAll();
					mABdapter.notifyDataSetChanged();
					updaBookSelectText(Integer.toString(mABdapter.getCheckedItemCount()));
				}
			});
	    bookUnAll.setOnClickListener(new View.OnClickListener() {
			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mABdapter.uncheckAll();
					mABdapter.notifyDataSetChanged();
					updaBookSelectText(Integer.toString(mABdapter.getCheckedItemCount()));
				}
			});
		mPopuwindow = new PopupWindow(eidtMenulayout,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		//
		ColorDrawable cd = new ColorDrawable(-0000);
		mPopuwindow.setBackgroundDrawable(cd);
		// mPopuwindow.setAnimationStyle(R.style.popuWindowAnimationStyle);
		mPopuwindow.setOutsideTouchable(false);
		mPopuwindow.setFocusable(false);
		mPopuwindow.showAtLocation(searchDialoglayout, Gravity.BOTTOM, x, y);
		searchDialoglayout.startAnimation(AnimationUtils.loadAnimation(
				MainActivity.this, R.anim.popu_enter));
	}
	//
	public void showPopupWindow(int x, int y) {
		//
		if (mFileLists != null && mFileLists.size() > 0) {
			mSearchAdatper = new ScanFileAdapter(MainActivity.this, mFileLists);
			scanFileGridView.setAdapter(mSearchAdatper);
			scanFileGridView.requestFocus();
		}
		//
		scanFileGridView.setOnKeyListener(new OnKeyListener() {
			// 焦点到了gridview上，所以需要监听此处的键盘事件。否则会出现不响应键盘事件的情况
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_MENU:
					if (mPopuwindow != null && mPopuwindow.isShowing()) {
						mPopuwindow.dismiss();
					}
					break;
				}
				return true;
			}
		});

		mPopuwindow = new PopupWindow(searchDialoglayout,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		//
		ColorDrawable cd = new ColorDrawable(-0000);
		mPopuwindow.setBackgroundDrawable(cd);
		// mPopuwindow.setAnimationStyle(R.style.popuWindowAnimationStyle);
		mPopuwindow.setOutsideTouchable(true);
		mPopuwindow.setFocusable(true);
		mPopuwindow.showAtLocation(searchDialoglayout, Gravity.TOP, x, y);
		searchDialoglayout.startAnimation(AnimationUtils.loadAnimation(
				MainActivity.this, R.anim.popu_enter));
	}

	//
	public void showDetailsPopupWindow(int x, int y,
			final org.geometerplus.fbreader.book.Book book) {
		//
		openBtn = (Button) detailDialoglayout.findViewById(R.id.openBookBtn);
		shareBtn = (Button) detailDialoglayout.findViewById(R.id.shareBtn);
		favBtn = (Button) detailDialoglayout.findViewById(R.id.favoriteBtn);
		delFavBtn = (Button) detailDialoglayout.findViewById(R.id.delFavBtn);
		delFavBtn.setVisibility(View.GONE);
		mPopuwindow = new PopupWindow(detailDialoglayout,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		//
		ColorDrawable cd = new ColorDrawable(-0000);
		mPopuwindow.setBackgroundDrawable(cd);
		mPopuwindow.setOutsideTouchable(true);
		mPopuwindow.setFocusable(true);
		mPopuwindow.showAtLocation(searchDialoglayout, Gravity.CENTER, x, y);
		openBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					mPopuwindow.dismiss();
				}
				if (book != null) {
					CoreReadActivity.openBookActivity(MainActivity.this, book,
							null);
					MainActivity.this.overridePendingTransition(
							R.anim.activity_enter, R.anim.activity_exit);
				}
			}
		});
		shareBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//
				ToolUtils.shareWithOther(MainActivity.this,
						"我觉得" + book.getTitle() + "这本书不错，现在推荐给你们！");
			}
		});
		favBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//
				// Local Book---Easy Book
				Book b = new Book();
				Log.i("MAIN", "" + book.File.getShortName());
				b.setBookName(book.File.getShortName());
				b.setBookPath(book.File.getPath());
				b.setBookProgress("00%");
				b.setBookSize(ToolUtils.FormetFileSize(book.File.size()));
				Date date = new Date();
				//
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				String bookAddTime = formatter.format(date);
				b.setBookAddTime(bookAddTime);

				Book b0 = DbDataOperation.queryBookFav(resolver,
						DbTags.FIELD_BOOK_NAME, book.File.getShortName());
				if (b0 == null) {
					DbDataOperation.insertToBookFav(resolver, b);
					Toast.makeText(MainActivity.this, "成功收藏！",
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	/**
	 * 
	 */
	class DropDownListenser implements OnNavigationListener {

		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			Message message;
			switch (itemPosition) {
			case 0:
				message = new Message();
				message.what = BOOK_SHELF;
				mHandler.sendMessage(message);
				break;

			case 1:
				message = new Message();
				message.what = BOOK_FAVORITE;
				mHandler.sendMessage(message);
				break;
			}
			return true;
		}
	}

}