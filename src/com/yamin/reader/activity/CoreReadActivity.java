package com.yamin.reader.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.geometerplus.android.fbreader.NavigationPopup;
import org.geometerplus.android.fbreader.PopupPanel;
import org.geometerplus.android.fbreader.ProcessHyperlinkAction;
import org.geometerplus.android.fbreader.RunPluginAction;
import org.geometerplus.android.fbreader.SearchAction;
import org.geometerplus.android.fbreader.SelectionBookmarkAction;
import org.geometerplus.android.fbreader.SelectionCopyAction;
import org.geometerplus.android.fbreader.SelectionHidePanelAction;
import org.geometerplus.android.fbreader.SelectionPopup;
import org.geometerplus.android.fbreader.SelectionShareAction;
import org.geometerplus.android.fbreader.SelectionShowPanelAction;
import org.geometerplus.android.fbreader.SetScreenOrientationAction;
import org.geometerplus.android.fbreader.ShareBookAction;
import org.geometerplus.android.fbreader.ShowBookmarksAction;
import org.geometerplus.android.fbreader.ShowLibraryAction;
import org.geometerplus.android.fbreader.ShowNavigationAction;
import org.geometerplus.android.fbreader.ShowPreferencesAction;
import org.geometerplus.android.fbreader.ShowTOCAction;
import org.geometerplus.android.fbreader.TextSearchPopup;
import org.geometerplus.android.fbreader.api.ApiListener;
import org.geometerplus.android.fbreader.api.ApiServerImplementation;
import org.geometerplus.android.fbreader.api.PluginApi;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.util.UIUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.SerializerUtil;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.ChangeFontSizeAction;
import org.geometerplus.fbreader.fbreader.ColorProfile;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBRreshAction;
import org.geometerplus.fbreader.fbreader.SwitchProfileAction;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.ZLEnumOption;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.zlibrary.ui.android.application.ZLAndroidApplicationWindow;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.yamin.reader.R;
import com.yamin.reader.adapter.PopGalleryAdapter;
import com.yamin.reader.database.DbDataOperation;
import com.yamin.reader.utils.ToolUtils;
import com.yamin.reader.view.SwitchButton;

/**
 * 
 * @ClassName: CoreReadActivity
 * @Description: TODO(这里用一句话描述这个类的作用) 基于开源的FBREADERJ1.8.2
 * @author ymcao
 * @date 2013-6-24 下午8:27:04
 * 
 */
public class CoreReadActivity extends Activity {
	public static final String ACTION_OPEN_BOOK = "android.easyreader.action.VIEW";
	public static final String BOOK_KEY = "esayreader.book";
	public static final String BOOKMARK_KEY = "esayreader.bookmark";
	public static final String BOOK_PATH_KEY = "esayreader.book.path";
	public static final int REQUEST_PREFERENCES = 1;
	public static final int REQUEST_CANCEL_MENU = 2;
	private static final int NIGHT_UPDATEUI = 0;
	private static final int DAY_UPDATEUI = 1;
	private static final int GREEN_UPDATEUI = 2;
	private static final int BROWN_UPDATEUI = 3;
	public static final int RESULT_DO_NOTHING = RESULT_FIRST_USER;
	public static final int RESULT_REPAINT = RESULT_FIRST_USER + 1;
	private static final String PLUGIN_ACTION_PREFIX = "___";
	private ZLIntegerRangeOption option;
	ZLEnumOption<ZLView.Animation>  animoption;
	private Gallery bgGallery;
	private boolean isNight = false;
	//
	PopupWindow mPopuwindow;
	private ImageView bookMarksButton;
	private ImageView fontBigButton;
	private ImageView fontSmallButton;
	private ImageView bookTocButton;
	private ImageView bookMoreButton;
	private ImageView bookHomeButton;
	private ImageView bookSearchButton;
	private RelativeLayout topLL;
	private LinearLayout bottomLL;
	private SeekBar brightness_slider;
	private SwitchButton dayornightSwitch;
	private ScrollView popuMenuLL;
	private LinearLayout navigation_settings;
	Spinner animSpinner;
	private ContentResolver resolver;
	private final List<PluginApi.ActionInfo> myPluginActions = new LinkedList<PluginApi.ActionInfo>();
	private final BroadcastReceiver myPluginInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final ArrayList<PluginApi.ActionInfo> actions = getResultExtras(
					true).<PluginApi.ActionInfo> getParcelableArrayList(
					PluginApi.PluginInfo.KEY);
			if (actions != null) {
				synchronized (myPluginActions) {
					int index = 0;
					while (index < myPluginActions.size()) {
						myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX
								+ index++);
					}
					myPluginActions.addAll(actions);
					index = 0;
					for (PluginApi.ActionInfo info : myPluginActions) {
						myFBReaderApp.addAction(PLUGIN_ACTION_PREFIX + index++,
								new RunPluginAction(CoreReadActivity.this,
										myFBReaderApp, info.getId()));
					}
				}
			}
		}
	};
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case NIGHT_UPDATEUI:

				topLL.setBackgroundColor(getResources().getColor(R.color.black));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.black));
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					popuMenuLL.setBackground(getResources().getDrawable(
							R.drawable.popup_menu_0));
				}
				animSpinner.setBackground(getResources().getDrawable(
						R.drawable.button_spinner_dark));
				break;
			case DAY_UPDATEUI:
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					popuMenuLL.setBackground(getResources().getDrawable(
							R.drawable.popup_menu_1));
				}
				animSpinner.setBackground(getResources().getDrawable(
						R.drawable.button_spinner_light));
				break;

			case BROWN_UPDATEUI:
				myFBReaderApp.runAction(ActionCode.SWITCH_TO_BG3,
						new SwitchProfileAction(myFBReaderApp,
								ColorProfile.THIRD));
				myFBReaderApp.runAction(ActionCode.JUST_REFRESH,
						new FBRreshAction(myFBReaderApp, 0));
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_3));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_3));
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					popuMenuLL.setBackground(getResources().getDrawable(
							R.drawable.popup_menu_3));
				}
				break;
			case GREEN_UPDATEUI:
				myFBReaderApp.runAction(ActionCode.SWITCH_TO_BG2,
						new SwitchProfileAction(myFBReaderApp,
								ColorProfile.SECOND));
				myFBReaderApp.runAction(ActionCode.JUST_REFRESH,
						new FBRreshAction(myFBReaderApp, 0));

				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_2));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_2));
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					popuMenuLL.setBackground(getResources().getDrawable(
							R.drawable.popup_menu_2));
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	/*
     * 
     */

	public static void openBookActivity(Context context, Book book,
			Bookmark bookmark) {
		context.startActivity(new Intent(context, CoreReadActivity.class)
				.setAction(ACTION_OPEN_BOOK)
				.putExtra(BOOK_KEY, SerializerUtil.serialize(book))
				.putExtra(BOOKMARK_KEY, SerializerUtil.serialize(bookmark))
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

	private static ZLAndroidLibrary getZLibrary() {
		return (ZLAndroidLibrary) ZLAndroidLibrary.Instance();
	}

	private FBReaderApp myFBReaderApp;
	private volatile Book myBook;

	private RelativeLayout myRootView;
	private ZLAndroidWidget myMainView;
	private boolean isBottomAndTopMenuShow = false;
	private int myFullScreenFlag;

	private synchronized void openBook(Intent intent, Runnable action,
			boolean force) {
		if (!force && myBook != null) {
			return;
		}
		myBook = SerializerUtil
				.deserializeBook(intent.getStringExtra(BOOK_KEY));
		final Bookmark bookmark = SerializerUtil.deserializeBookmark(intent
				.getStringExtra(BOOKMARK_KEY));
		if (myBook == null) {
			final Uri data = intent.getData();
			if (data != null) {
				this.myBook = createBookForFile(ZLFile.createFileByPath(data
						.getPath()));
			}
		}
		myFBReaderApp.openBook(myBook, bookmark, action);
	}

	public Book createBookForFile(ZLFile file) {
		if (file == null) {
			return null;
		}
		Book book = myFBReaderApp.Collection.getBookByFile(file);
		if (book != null) {
			return book;
		}
		if (file.isArchive()) {
			for (ZLFile child : file.children()) {
				book = myFBReaderApp.Collection.getBookByFile(child);
				if (book != null) {
					return book;
				}
			}
		}
		return null;
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Thread.setDefaultUncaughtExceptionHandler(new
		// UncaughtExceptionHandler(
		// this));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.core_main);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		getZLibrary().setActivity(CoreReadActivity.this);
		resolver = getContentResolver();
		//
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLibrary
				.Instance();
		myFullScreenFlag = zlibrary.ShowStatusBarOption.getValue() ? 0
				: WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				myFullScreenFlag);
		//
		option = ZLTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
		//
		myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(CoreReadActivity.this,
					new BookCollectionShadow());
		}
		getCollection().bindToService(this, null);
		myBook = null;

		final ZLAndroidApplication androidApplication = (ZLAndroidApplication) getApplication();
		if (androidApplication.myMainWindow == null) {
			androidApplication.myMainWindow = new ZLAndroidApplicationWindow(
					myFBReaderApp);
			myFBReaderApp.initWindow();
		}
		if (myFBReaderApp.getPopupById(TextSearchPopup.ID) == null) {
			new TextSearchPopup(myFBReaderApp);
		}
		if (myFBReaderApp.getPopupById(NavigationPopup.ID) == null) {
			new NavigationPopup(myFBReaderApp);
		}
		if (myFBReaderApp.getPopupById(SelectionPopup.ID) == null) {
			new SelectionPopup(myFBReaderApp);
		}

		myFBReaderApp.addAction(ActionCode.SHOW_LIBRARY, new ShowLibraryAction(
				this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_PREFERENCES,
				new ShowPreferencesAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SHOW_TOC, new ShowTOCAction(this,
				myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_BOOKMARKS,
				new ShowBookmarksAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SHOW_NAVIGATION,
				new ShowNavigationAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SEARCH, new SearchAction(this,
				myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHARE_BOOK, new ShareBookAction(
				this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SELECTION_SHOW_PANEL,
				new SelectionShowPanelAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_HIDE_PANEL,
				new SelectionHidePanelAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD,
				new SelectionCopyAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_SHARE,
				new SelectionShareAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SELECTION_BOOKMARK,
				new SelectionBookmarkAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.PROCESS_HYPERLINK,
				new ProcessHyperlinkAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SYSTEM,
				new SetScreenOrientationAction(this, myFBReaderApp,
						ZLibrary.SCREEN_ORIENTATION_SYSTEM));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SENSOR,
				new SetScreenOrientationAction(this, myFBReaderApp,
						ZLibrary.SCREEN_ORIENTATION_SENSOR));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT,
				new SetScreenOrientationAction(this, myFBReaderApp,
						ZLibrary.SCREEN_ORIENTATION_PORTRAIT));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE,
				new SetScreenOrientationAction(this, myFBReaderApp,
						ZLibrary.SCREEN_ORIENTATION_LANDSCAPE));
		if (ZLibrary.Instance().supportsAllOrientations()) {
			myFBReaderApp.addAction(
					ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT,
					new SetScreenOrientationAction(this, myFBReaderApp,
							ZLibrary.SCREEN_ORIENTATION_REVERSE_PORTRAIT));
			myFBReaderApp.addAction(
					ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
					new SetScreenOrientationAction(this, myFBReaderApp,
							ZLibrary.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
		}
		initView();
		setListener();

	}

	/*
	 * Init UI View
	 */
	public void initView() {
		myRootView = (RelativeLayout) findViewById(R.id.root_view);
		myMainView = (ZLAndroidWidget) findViewById(R.id.main_view);
		//
		topLL = (RelativeLayout) findViewById(R.id.topMenuLL);
		bottomLL = (LinearLayout) findViewById(R.id.bottomMenuLL);
		animSpinner = (Spinner) findViewById(R.id.animSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.spinner_anim,
				R.layout.spinner_item_print);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		animSpinner.setAdapter(adapter);
		if(myFBReaderApp.PageTurningOptions.Animation.getValue().equals(ZLView.Animation.curl)){
			animSpinner.setSelection(0);
		}
		else if(myFBReaderApp.PageTurningOptions.Animation.getValue().equals(ZLView.Animation.slide)){
			animSpinner.setSelection(1);
		}
		else if(myFBReaderApp.PageTurningOptions.Animation.getValue().equals(ZLView.Animation.shift)){
			animSpinner.setSelection(2);
		}
		bookMoreButton = (ImageView) findViewById(R.id.bookMoreButton);

		// -----------------------------------------
		bookMarksButton = (ImageView) findViewById(R.id.bookMarkButton);
		fontBigButton = (ImageView) findViewById(R.id.fontsizeBigButton);
		fontSmallButton = (ImageView) findViewById(R.id.fontsizeSmallButton);
		bookHomeButton = (ImageView) findViewById(R.id.bookHomeButton);
		bookSearchButton = (ImageView) findViewById(R.id.bookSearchButton);
		bookTocButton = (ImageView) findViewById(R.id.bookTocButton);
		//
		if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.NIGHT)) {
			topLL.setBackgroundColor(getResources().getColor(R.color.black));
			bottomLL.setBackgroundColor(getResources().getColor(R.color.black));
		} else {
			if (myFBReaderApp.getColorProfileName() != null
					&& myFBReaderApp.getColorProfileName().equals(
							ColorProfile.SECOND)) {
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_2));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_2));

			} else if (myFBReaderApp.getColorProfileName() != null
					&& myFBReaderApp.getColorProfileName().equals(
							ColorProfile.THIRD)) {
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_3));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_3));
			} else {
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
			}
		}
		//
	}

	public ZLAndroidWidget getMainView() {
		return myMainView;
	}

	private void setListener() {
		bookMoreButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopupWindow(bookMoreButton);
				Log.i("MAIN", "onClick()");
			}
		});
		bookTocButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!myFBReaderApp.Model.TOCTree.hasChildren()) {
					Toast.makeText(CoreReadActivity.this, "本书暂无目录!",
							Toast.LENGTH_SHORT).show();
				}
				myFBReaderApp.runAction(ActionCode.SHOW_TOC, new ShowTOCAction(
						CoreReadActivity.this, myFBReaderApp));

			}
		});
		bookMarksButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myFBReaderApp.runAction(ActionCode.SHOW_BOOKMARKS,
						new ShowBookmarksAction(CoreReadActivity.this,
								myFBReaderApp));

			}
		});
		fontBigButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (option.getValue() <= 55) {
					myFBReaderApp.runAction(ActionCode.INCREASE_FONT,
							new ChangeFontSizeAction(myFBReaderApp, +2));
				}
			}
		});
		fontSmallButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (option.getValue() >= 30) {
					myFBReaderApp.runAction(ActionCode.DECREASE_FONT,
							new ChangeFontSizeAction(myFBReaderApp, -2));
				}
			}
		});
		bookHomeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backPress();
			}
		});
		bookSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myFBReaderApp.runAction(ActionCode.SEARCH, new SearchAction(
						CoreReadActivity.this, myFBReaderApp));
			}
		});
		animSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				default:
				case 0:
					myFBReaderApp.PageTurningOptions.Animation.setValue(ZLView.Animation.curl);
					break;
				case 1:
					myFBReaderApp.PageTurningOptions.Animation.setValue(ZLView.Animation.slide);
					break;
				case 2:
					myFBReaderApp.PageTurningOptions.Animation.setValue(ZLView.Animation.shift);
					break;
				}
				myFBReaderApp.getViewWidget().repaint();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}

		
		});
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		Log.i("TAG", "onNewIntent()");
		final String action = intent.getAction();
		final Uri data = intent.getData();

		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
			super.onNewIntent(intent);
		} else if (Intent.ACTION_VIEW.equals(action) && data != null
				&& "fbreader-action".equals(data.getScheme())) {
			myFBReaderApp.runAction(data.getEncodedSchemeSpecificPart(),
					data.getFragment());
		} else if (ACTION_OPEN_BOOK.equals(action)) {

			getCollection().bindToService(this, new Runnable() {
				public void run() {
					Log.i("TAG", "openBook()");
					openBook(intent, null, true);
				}
			});
		} else if (Intent.ACTION_SEARCH.equals(action)) {
			final String pattern = intent.getStringExtra(SearchManager.QUERY);
			final Runnable runnable = new Runnable() {
				public void run() {
					final TextSearchPopup popup = (TextSearchPopup) myFBReaderApp
							.getPopupById(TextSearchPopup.ID);
					popup.initPosition();
					myFBReaderApp.TextSearchPatternOption.setValue(pattern);
					if (myFBReaderApp.getTextView().search(pattern, true,
							false, false, false) != 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								myFBReaderApp.showPopup(popup.getId());
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								UIUtil.showErrorMessage(CoreReadActivity.this,
										"textNotFound");
								popup.StartPosition = null;
							}
						});
					}
				}
			};
			UIUtil.wait("search", runnable, this);
		} else {
			super.onNewIntent(intent);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		getCollection().bindToService(this, new Runnable() {
			public void run() {
				new Thread() {
					public void run() {
						openBook(getIntent(), null, false);
						myFBReaderApp.getViewWidget().repaint();
					}
				}.start();

				myFBReaderApp.getViewWidget().repaint();
			}
		});

		initPluginActions();

		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLibrary
				.Instance();
		final int fullScreenFlag = zlibrary.ShowStatusBarOption.getValue() ? 0
				: WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (fullScreenFlag != myFullScreenFlag) {
			finish();
			startActivity(new Intent(this, getClass()));
		}

		SetScreenOrientationAction.setOrientation(this, zlibrary
				.getOrientationOption().getValue());

		((PopupPanel) myFBReaderApp.getPopupById(TextSearchPopup.ID))
				.setPanelInfo(CoreReadActivity.this, myRootView);
		((PopupPanel) myFBReaderApp.getPopupById(NavigationPopup.ID))
				.setPanelInfo(CoreReadActivity.this, myRootView);
		((PopupPanel) myFBReaderApp.getPopupById(SelectionPopup.ID))
				.setPanelInfo(CoreReadActivity.this, myRootView);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		switchWakeLock(hasFocus
				&& getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() < myFBReaderApp
						.getBatteryLevel());
	}

	private void initPluginActions() {
		synchronized (myPluginActions) {
			int index = 0;
			while (index < myPluginActions.size()) {
				myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
			}
			myPluginActions.clear();
		}

		sendOrderedBroadcast(new Intent(PluginApi.ACTION_REGISTER), null,
				myPluginInfoReceiver, null, RESULT_OK, null, null);
	}

	@Override
	protected void onResume() {
		super.onResume();

		myStartTimer = true;
		final int brightnessLevel = getZLibrary().ScreenBrightnessLevelOption
				.getValue();
		if (brightnessLevel != 0) {
			setScreenBrightness(brightnessLevel);
		} else {
			setScreenBrightnessAuto();
		}
		if (getZLibrary().DisableButtonLightsOption.getValue()) {
			setButtonLight(false);
		}

		registerReceiver(myBatteryInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		PopupPanel.restoreVisibilities(myFBReaderApp);
		ApiServerImplementation.sendEvent(this,
				ApiListener.EVENT_READ_MODE_OPENED);

		getCollection().bindToService(this, new Runnable() {
			public void run() {
				final BookModel model = myFBReaderApp.Model;
				if (model == null || model.Book == null) {
					return;
				}
				onPreferencesUpdate(myFBReaderApp.Collection
						.getBookById(model.Book.getId()));
			}
		});
	}

	@Override
	protected void onPause() {
		try {
			unregisterReceiver(myBatteryInfoReceiver);
		} catch (IllegalArgumentException e) {
			// do nothing, this exception means myBatteryInfoReceiver was not
			// registered
		}
		myFBReaderApp.stopTimer();
		if (getZLibrary().DisableButtonLightsOption.getValue()) {
			setButtonLight(true);
		}
		if (mPopuwindow != null && mPopuwindow.isShowing()) {
			mPopuwindow.dismiss();
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		ApiServerImplementation.sendEvent(this,
				ApiListener.EVENT_READ_MODE_CLOSED);
		PopupPanel.removeAllWindows(myFBReaderApp, this);
		Log.i("MAIN", "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		getCollection().unbind();
		//
		if (mPopuwindow != null && mPopuwindow.isShowing()) {
			mPopuwindow.dismiss();
		}
		//
		//backPress();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		myFBReaderApp.onWindowClosing();
		super.onLowMemory();
	}

	@Override
	public boolean onSearchRequested() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		myFBReaderApp.hideActivePopup();
		final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
		manager.setOnCancelListener(new SearchManager.OnCancelListener() {
			public void onCancel() {
				if (popup != null) {
					myFBReaderApp.showPopup(popup.getId());
				}
				manager.setOnCancelListener(null);
			}
		});
		startSearch(myFBReaderApp.TextSearchPatternOption.getValue(), true,
				null, false);
		return true;
	}

	//
	public void backPress() {
		int y = myFBReaderApp.getTextView().pagePosition().Current;
		int z = myFBReaderApp.getTextView().pagePosition().Total;
		Log.i("MAIN", y + "" + "/" + z + ToolUtils.myPercent(y, z));

		DbDataOperation.updateValuesToTable(resolver,
				"" + ToolUtils.myPercent(y, z),
				myFBReaderApp.Model.Book.File.getShortName());

		Log.i("MAIN", "" + myFBReaderApp.getTextView().getEndCursor());
		myFBReaderApp.Collection.storePosition(myBook.getId(), myFBReaderApp
				.getTextView().getEndCursor());
		//
		startActivity(new Intent(CoreReadActivity.this, MainActivity.class));
		CoreReadActivity.this.overridePendingTransition(R.anim.activity_enter,
				R.anim.activity_exit);
		CoreReadActivity.this.finish();
	}

	//
	public void showSelectionPanel() {
		final ZLTextView view = myFBReaderApp.getTextView();
		((SelectionPopup) myFBReaderApp.getPopupById(SelectionPopup.ID)).move(
				view.getSelectionStartY(), view.getSelectionEndY());
		myFBReaderApp.showPopup(SelectionPopup.ID);
	}

	public void hideSelectionPanel() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		if (popup != null && popup.getId() == SelectionPopup.ID) {
			myFBReaderApp.hideActivePopup();
		}
	}

	private void onPreferencesUpdate(Book book) {
		AndroidFontUtil.clearFontCache();
		myFBReaderApp.onBookUpdated(book);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case REQUEST_CANCEL_MENU:
			myFBReaderApp.runCancelAction(resultCode - 1);
			break;
		}
	}

	public void navigate() {
		// ((NavigationPopup) myFBReaderApp.getPopupById(NavigationPopup.ID))
		// .runNavigation();
		if (!isBottomAndTopMenuShow) {
			isBottomAndTopMenuShow = true;
			topLL.setVisibility(View.VISIBLE);
			bottomLL.setVisibility(View.VISIBLE);
			topLL.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.layout_enter));
			bottomLL.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.layout_enter));
		} else {
			isBottomAndTopMenuShow = false;
			topLL.setVisibility(View.GONE);
			bottomLL.setVisibility(View.GONE);
			topLL.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.layout_exit));
			bottomLL.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.layout_exit));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLibrary
				.Instance();
		if (!zlibrary.isKindleFire()
				&& !zlibrary.ShowStatusBarOption.getValue()) {
			// getWindow().addFlags(
			// WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLibrary
				.Instance();
		if (!zlibrary.isKindleFire()
				&& !zlibrary.ShowStatusBarOption.getValue()) {
			// getWindow().addFlags(
			// WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLibrary
				.Instance();
		if (!zlibrary.isKindleFire()
				&& !zlibrary.ShowStatusBarOption.getValue()) {
			// getWindow().clearFlags(
			// WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLibrary
				.Instance();
		if (!zlibrary.isKindleFire()
				&& !zlibrary.ShowStatusBarOption.getValue()) {
			// getWindow().clearFlags(
			// WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		return super.onOptionsItemSelected(item);
	}
	//建议加入onConfigurationChanged回调方法
	//注:如果当前Activity没有设置android:configChanges属性,或者是固定横屏或竖屏模式,则不需要加入
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	
		
		super.onConfigurationChanged(newConfig);
	}
	

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//
			backPress();
			this.onBackPressed();
			return true;
		}
		return (myMainView != null && myMainView.onKeyDown(keyCode, event))
				|| super.onKeyDown(keyCode, event);
	}

	private void setButtonLight(boolean enabled) {
		try {
			final WindowManager.LayoutParams attrs = getWindow()
					.getAttributes();
			final Class<?> cls = attrs.getClass();
			final Field fld = cls.getField("buttonBrightness");
			if (fld != null && "float".equals(fld.getType().toString())) {
				fld.setFloat(attrs, enabled ? -1.0f : 0.0f);
				getWindow().setAttributes(attrs);
			}
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
	}

	private PowerManager.WakeLock myWakeLock;
	private boolean myWakeLockToCreate;
	private boolean myStartTimer;

	public final void createWakeLock() {
		if (myWakeLockToCreate) {
			synchronized (this) {
				if (myWakeLockToCreate) {
					myWakeLockToCreate = false;
					myWakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
							.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
									"FBReader");
					myWakeLock.acquire();
				}
			}
		}
		if (myStartTimer) {
			myFBReaderApp.startTimer();
			myStartTimer = false;
		}
	}

	private final void switchWakeLock(boolean on) {
		if (on) {
			if (myWakeLock == null) {
				myWakeLockToCreate = true;
			}
		} else {
			if (myWakeLock != null) {
				synchronized (this) {
					if (myWakeLock != null) {
						myWakeLock.release();
						myWakeLock = null;
					}
				}
			}
		}
	}

	private BroadcastReceiver myBatteryInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 100);
			final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
			application.myMainWindow.setBatteryLevel(level);
			switchWakeLock(hasWindowFocus()
					&& getZLibrary().BatteryLevelToTurnScreenOffOption
							.getValue() < level);
		}
	};

	private void setScreenBrightnessAuto() {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.screenBrightness = -1.0f;
		getWindow().setAttributes(attrs);
	}

	public void setScreenBrightness(int percent) {
		if (percent < 1) {
			percent = 10;
		} else if (percent > 100) {
			percent = 100;
		}
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.screenBrightness = percent / 100.0f;
		getWindow().setAttributes(attrs);
		getZLibrary().ScreenBrightnessLevelOption.setValue(percent);
	}

	public int getScreenBrightness() {
		final int level = (int) (100 * getWindow().getAttributes().screenBrightness);
		return (level >= 0) ? level : 50;
	}

	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow) myFBReaderApp.Collection;
	}

	/*
	 * @弹出POPU MENU
	 */
	public void showPopupWindow(View v) {
		ScrollView layout = (ScrollView) LayoutInflater.from(
				CoreReadActivity.this).inflate(R.layout.book_settings, null);
		brightness_slider = (SeekBar) layout.findViewById(R.id.brightness_slider);
		int percent = getZLibrary().ScreenBrightnessLevelOption.getValue();
		brightness_slider.setProgress(percent);
		dayornightSwitch = (SwitchButton) layout.findViewById(R.id.main_myslipswitch);
		//
		if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.NIGHT)) {
			dayornightSwitch.setChecked(true);
		} else {
			dayornightSwitch.setChecked(false);
		}
		popuMenuLL = (ScrollView) layout.findViewById(R.id.popuMenuBg);

		if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.NIGHT)) {
			popuMenuLL.setBackground(getResources().getDrawable(
					R.drawable.popup_menu_0));

		} else if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.SECOND)) {
			popuMenuLL.setBackground(getResources().getDrawable(
					R.drawable.popup_menu_2));
		} else if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.THIRD)) {
			popuMenuLL.setBackground(getResources().getDrawable(
					R.drawable.popup_menu_3));
		} else {
			popuMenuLL.setBackground(getResources().getDrawable(
					R.drawable.popup_menu_1));
		}
		navigation_settings = (LinearLayout) layout
				.findViewById(R.id.navigation_settings);
		bgGallery = (Gallery) layout.findViewById(R.id.bgGallery);
		// ---------------------------------------
		mPopuwindow = new PopupWindow(layout,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		ColorDrawable cd = new ColorDrawable(-0000);
		mPopuwindow.setBackgroundDrawable(cd);
		mPopuwindow.setBackgroundDrawable(cd);

		mPopuwindow.setOutsideTouchable(true);
		mPopuwindow.setFocusable(true);
		// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		mPopuwindow.showAsDropDown(v);
		setPopuListener();
	}

	private void setPopuListener() {
		// TODO Auto-generated method stub

		brightness_slider
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					/**
					 * 拖动条停止拖动的时候调用
					 */
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}
					/**
					 * 拖动条开始拖动的时候调用
					 */
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}
					/**
					 * 拖动条进度改变的时候调用
					 */
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						setScreenBrightness(progress);
					}
				});
		dayornightSwitch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							myFBReaderApp.runAction(
									ActionCode.SWITCH_TO_NIGHT_PROFILE,
									new SwitchProfileAction(myFBReaderApp,
											ColorProfile.NIGHT));
							Toast.makeText(CoreReadActivity.this, "夜间模式开启",
									Toast.LENGTH_SHORT).show();
							Message message = new Message();
							message.what = NIGHT_UPDATEUI;
							mHandler.sendMessage(message);
							isNight = true;
						} else {
							myFBReaderApp.runAction(
									ActionCode.SWITCH_TO_DAY_PROFILE,
									new SwitchProfileAction(myFBReaderApp,
											ColorProfile.DAY));
							Toast.makeText(CoreReadActivity.this, "白天模式开启",
									Toast.LENGTH_SHORT).show();
							Message message = new Message();
							message.what = DAY_UPDATEUI;
							mHandler.sendMessage(message);
							isNight = false;
						}
					}
				});
		navigation_settings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					mPopuwindow.dismiss();
				}
				((NavigationPopup) myFBReaderApp
						.getPopupById(NavigationPopup.ID)).runNavigation();
			}
		});
		final PopGalleryAdapter localPopGalleryAdapter = new PopGalleryAdapter(
				this,
				"wallpapers",
				bgGallery,
				((FBReaderApp) FBReaderApp.Instance()).getColorProfile().WallpaperOption
						.getValue());
		bgGallery.setAdapter(localPopGalleryAdapter);
		if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(ColorProfile.DAY)) {
			localPopGalleryAdapter.setUserIndex(0);
			//localPopGalleryAdapter.notifyDataSetChanged();
		} else if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.SECOND)) {
			localPopGalleryAdapter.setUserIndex(1);
			//localPopGalleryAdapter.notifyDataSetChanged();
		} else {
			localPopGalleryAdapter.setUserIndex(2);
			//localPopGalleryAdapter.notifyDataSetChanged();
		}
		if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.NIGHT)) {
			isNight = true;
		} else {
			isNight = false;
		}
		bgGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (!isNight) {
					localPopGalleryAdapter.setUserIndex(arg2);
					localPopGalleryAdapter.notifyDataSetChanged();
					Message message;
					switch (arg2) {
					default:
					case 0:
						Log.i("MAIN", "0");
						message = new Message();
						message.what = DAY_UPDATEUI;
						mHandler.sendMessage(message);

						myFBReaderApp.runAction(
								ActionCode.SWITCH_TO_DAY_PROFILE,
								new SwitchProfileAction(myFBReaderApp,
										ColorProfile.DAY));
						myFBReaderApp.runAction(ActionCode.JUST_REFRESH,
								new FBRreshAction(myFBReaderApp, 0));

						break;
					case 1:
						Log.i("MAIN", "1");
						message = new Message();
						message.what = GREEN_UPDATEUI;
						mHandler.sendMessage(message);
						break;
					case 2:
						Log.i("MAIN", "2");
						message = new Message();
						message.what = BROWN_UPDATEUI;
						mHandler.sendMessage(message);
						break;
					}
				}
			}
		});

	}
}