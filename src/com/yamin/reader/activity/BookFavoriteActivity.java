package com.yamin.reader.activity;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.Author;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.language.Language;
import org.geometerplus.zlibrary.core.language.ZLLanguageUtil;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yamin.reader.R;
import com.yamin.reader.adapter.BookHisFavAdaper;
import com.yamin.reader.database.DbDataOperation;
import com.yamin.reader.model.Book;
import com.yamin.reader.utils.ToolUtils;

public class BookFavoriteActivity extends Activity {
	private ListView lfFavoriteList;
	BookHisFavAdaper favoriteAdapter;
	private ContentResolver resolver;
	private TextView titleTxt;
	private TextView authorTxt;
	private TextView proessTxt;
	private TextView storageTxt;
	private ImageView coverImg;
	private TextView languageTxt;
	private Button openBtn;
	private Button shareBtn;
	private Button delFavBtn;
	private Button FavBtn;
	private RelativeLayout detailDialoglayout;
	private RelativeLayout somethingRl;
	private RelativeLayout nothingRl;
	PopupWindow mPopuwindow;
	private FBReaderApp myFBReaderApp;
	private ArrayList<Book> favsData = new ArrayList<Book>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_favorite);
		resolver = this.getContentResolver();
		favsData = DbDataOperation.queryBooksFav(resolver);
		favoriteAdapter=new BookHisFavAdaper(this,favsData);
		myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(BookFavoriteActivity.this,
					new BookCollectionShadow());
		}
		getCollection().bindToService(this, null);
		ActionBar bar = this.getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		bar.setTitle(R.string.book_favorite);
		somethingRl=(RelativeLayout )findViewById(R.id.favorite_something);
        nothingRl=(RelativeLayout )findViewById(R.id.favorite_nothing);;
		detailDialoglayout = (RelativeLayout) LayoutInflater.from(
				BookFavoriteActivity.this).inflate(R.layout.book_details_info,
				null);
		titleTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_title);
		authorTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_author);
		proessTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_progress);
		proessTxt.setVisibility(View.GONE);
		storageTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_storage);
		coverImg = (ImageView) detailDialoglayout
				.findViewById(R.id.details_cover);
		languageTxt = (TextView) detailDialoglayout
				.findViewById(R.id.detail_language);
		lfFavoriteList = (ListView) findViewById(R.id.lvBookFavorite);
		lfFavoriteList.setAdapter(favoriteAdapter);
		setListener();
		updateLayout();
	}

	private void setListener() {
		//
		lfFavoriteList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub

						Book b = favsData.get(position);
						// File Exist
						if (ToolUtils.fileIsExists(b.getBookPath())) {
							// openBook(b.getBookPath());
							ZLFile file = ZLFile.createFileByPath(b
									.getBookPath());
							org.geometerplus.fbreader.book.Book book = getCollection().getBookByFile(file);
							if (book != null) {
								titleTxt.setText("书名:" + book.getTitle());
								/*
								if ("00%".equals(b.getBookProgress())) {
									proessTxt.setText("进度:未读");

								} else {
									proessTxt.setText("进度:"
											+ b.getBookProgress());
								}
								*/
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
										BookFavoriteActivity.this, book);
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
							Toast.makeText(BookFavoriteActivity.this,
									"文件已被从SD卡中移除!", Toast.LENGTH_LONG).show();
							DbDataOperation.deleteFavBook(resolver,
									b.getBookName());
							//
							favsData = DbDataOperation.queryBooksFav(resolver);
							favoriteAdapter.notifyDataSetChanged();

						}
						//
					}
				});

	}
    private void  updateLayout(){
    	if(favoriteAdapter!=null&&favoriteAdapter.getCount()>0){
    		somethingRl.setVisibility(View.VISIBLE);
    		nothingRl.setVisibility(View.GONE);
    	}else{
    		somethingRl.setVisibility(View.GONE);
    		nothingRl.setVisibility(View.VISIBLE);
    	}
    }
	//
	public void showDetailsPopupWindow(int x, int y,
			final org.geometerplus.fbreader.book.Book book) {
		//
		openBtn = (Button) detailDialoglayout.findViewById(R.id.openBookBtn);
		shareBtn = (Button) detailDialoglayout.findViewById(R.id.shareBtn);
		delFavBtn = (Button) detailDialoglayout.findViewById(R.id.delFavBtn);
		FavBtn = (Button) detailDialoglayout.findViewById(R.id.favoriteBtn);
		delFavBtn.setVisibility(View.VISIBLE);
		FavBtn.setVisibility(View.GONE);
		mPopuwindow = new PopupWindow(detailDialoglayout,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		//
		ColorDrawable cd = new ColorDrawable(-0000);
		mPopuwindow.setBackgroundDrawable(cd);
		mPopuwindow.setOutsideTouchable(true);
		mPopuwindow.setFocusable(true);
		mPopuwindow.showAtLocation(detailDialoglayout, Gravity.CENTER, x, y);
		openBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					mPopuwindow.dismiss();
				}
				if (book != null) {
					CoreReadActivity.openBookActivity(
							BookFavoriteActivity.this, book, null);
					BookFavoriteActivity.this.overridePendingTransition(
							R.anim.activity_enter, R.anim.activity_exit);
			        BookFavoriteActivity.this.finish();
					
				}
			}
		});
		shareBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//
				ToolUtils.shareWithOther(BookFavoriteActivity.this, "我觉得"
						+ book.getTitle() + "这本书不错，现在推荐给你们！");
			}
		});

		delFavBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DbDataOperation.deleteFavBook(resolver,
						book.File.getShortName());
				favsData = DbDataOperation.queryBooksFav(resolver);
				favoriteAdapter.setmData(favsData);
				favoriteAdapter.notifyDataSetChanged();
				Toast.makeText(BookFavoriteActivity.this, "成功删除收藏！",
						Toast.LENGTH_LONG).show();
				updateLayout();
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					mPopuwindow.dismiss();
				}
			}
		});

	}

	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow) myFBReaderApp.Collection;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent i = new Intent(this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			this.finish();
			return true;
		default:
			return false;
		}
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
}
