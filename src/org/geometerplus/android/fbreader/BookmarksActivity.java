/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.util.UIUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.BookmarkQuery;
import org.geometerplus.fbreader.book.SerializerUtil;
import org.geometerplus.zlibrary.core.options.ZLStringOption;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.util.MiscUtil;

import android.app.ActionBar;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.yamin.reader.R;
import com.yamin.reader.activity.CoreReadActivity;

public class BookmarksActivity extends TabActivity {
	private static final int OPEN_ITEM_ID = 0;
	private static final int EDIT_ITEM_ID = 1;
	private static final int DELETE_ITEM_ID = 2;
	private static ActionBar bar;
	private final BookCollectionShadow myCollection = new BookCollectionShadow();
	private volatile Book myBook;

	private final Comparator<Bookmark> myComparator = new Bookmark.ByTimeComparator();

	private volatile BookmarksAdapter myThisBookAdapter;
	private volatile BookmarksAdapter myAllBooksAdapter;
	private volatile BookmarksAdapter mySearchResultsAdapter;

	private final ZLResource myResource = ZLResource.resource("bookmarksView");
	private final ZLStringOption myBookmarkSearchPatternOption = new ZLStringOption(
			"BookmarkSearch", "Pattern", "");

	private ListView createTab(String tag, int id) {
		final TabHost host = getTabHost();
		final String label = myResource.getResource(tag).getValue();
		host.addTab(host.newTabSpec(tag).setIndicator(label).setContent(id));
		return (ListView) findViewById(id);
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// Thread.setDefaultUncaughtExceptionHandler(new
		// org.geometerplus.zlibrary.ui.android.library.UncaughtExceptionHandler(this));
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		// ADD ACTIONBAR
		bar = this.getActionBar();
		bar.setTitle("书签管理");
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowHomeEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		//
		final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
		manager.setOnCancelListener(null);

		final TabHost host = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.bookmarks,
				host.getTabContentView(), true);
		myBook = SerializerUtil.deserializeBook(getIntent().getStringExtra(
				CoreReadActivity.BOOK_KEY));
	}

	private class Initializer implements Runnable {
		public void run() {
			long id = 0;
			if (myBook != null) {
				for (BookmarkQuery query = new BookmarkQuery(myBook, 20);; query = query
						.next()) {
					final List<Bookmark> thisBookBookmarks = myCollection
							.bookmarks(query);
					if (thisBookBookmarks.isEmpty()) {
						break;
					}
					myThisBookAdapter.addAll(thisBookBookmarks);
					myAllBooksAdapter.addAll(thisBookBookmarks);
				}
			}
			id = 0;
			for (BookmarkQuery query = new BookmarkQuery(20);; query = query
					.next()) {
				final List<Bookmark> allBookmarks = myCollection
						.bookmarks(query);
				if (allBookmarks.isEmpty()) {
					break;
				}
				myAllBooksAdapter.addAll(allBookmarks);
			}
			runOnUiThread(new Runnable() {
				public void run() {
					//setProgressBarIndeterminateVisibility(false);
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		runOnUiThread(new Runnable() {
			public void run() {
				//setProgressBarIndeterminateVisibility(true);
			}
		});

		myCollection.bindToService(this, new Runnable() {
			public void run() {
				if (myAllBooksAdapter != null) {
					return;
				}
				if (myBook != null) {
					myThisBookAdapter = new BookmarksAdapter(createTab(
							"thisBook", R.id.this_book), true);
				} else {
					findViewById(R.id.this_book).setVisibility(View.GONE);
				}
				myAllBooksAdapter = new BookmarksAdapter(createTab("allBooks",
						R.id.all_books), false);
				findViewById(R.id.search_results).setVisibility(View.GONE);

				new Thread(new Initializer()).start();
			}
		});

		OrientationUtil.setOrientation(this, getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("MAIN", "onNewIntent");
		OrientationUtil.setOrientation(this, intent);

		if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
			return;
		}
		String pattern = intent.getStringExtra(SearchManager.QUERY);
		Log.i("MAIN", pattern + "");
		myBookmarkSearchPatternOption.setValue(pattern);

		final LinkedList<Bookmark> bookmarks = new LinkedList<Bookmark>();
		pattern = pattern.toLowerCase();
		for (Bookmark b : myAllBooksAdapter.bookmarks()) {
			Log.i("MAIN", b.getText());
			if (MiscUtil.matchesIgnoreCase(b.getText(), pattern)) {
				bookmarks.add(b);
			}
		}
		//
		if (!bookmarks.isEmpty()) {
			showSearchResultsTab(bookmarks);
		} else {
			UIUtil.showErrorMessage(this, "bookmarkNotFound");
		}
	}

	@Override
	protected void onStop() {
		myCollection.unbind();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bookmark_menu, menu);
		return true;
	}

	@Override
	public boolean onSearchRequested() {
		startSearch(myBookmarkSearchPatternOption.getValue(), true, null, false);
		return true;
	}

	void showSearchResultsTab(LinkedList<Bookmark> results) {
		if (mySearchResultsAdapter == null) {
			mySearchResultsAdapter = new BookmarksAdapter(createTab("found",
					R.id.search_results), false);
		} else {
			mySearchResultsAdapter.clear();
		}
		mySearchResultsAdapter.addAll(results);
		getTabHost().setCurrentTabByTag("found");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final int position = ((AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo()).position;
		final ListView view = (ListView) getTabHost().getCurrentView();
		final Bookmark bookmark = ((BookmarksAdapter) view.getAdapter())
				.getItem(position);
		switch (item.getItemId()) {
		case OPEN_ITEM_ID:
			gotoBookmark(bookmark);
			return true;
		case EDIT_ITEM_ID:
			/*
			 * final Intent intent = new Intent(this,
			 * BookmarkEditActivity.class);
			 * OrientationUtil.startActivityForResult(this, intent, 1);
			 */
			return true;
		case DELETE_ITEM_ID:
			myCollection.deleteBookmark(bookmark);
			if (myThisBookAdapter != null) {
				myThisBookAdapter.remove(bookmark);
			}
			if (myAllBooksAdapter != null) {
				myAllBooksAdapter.remove(bookmark);
			}
			if (mySearchResultsAdapter != null) {
				mySearchResultsAdapter.remove(bookmark);
			}
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.bookmark_search:
			onSearchRequested();
			return true;

		case android.R.id.home:
			Intent i = new Intent(this, CoreReadActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addBookmark() {
		final Bookmark bookmark = SerializerUtil
				.deserializeBookmark(getIntent().getStringExtra(
						CoreReadActivity.BOOKMARK_KEY));
		if (bookmark != null) {
			myCollection.saveBookmark(bookmark);
			myThisBookAdapter.add(bookmark);
			myAllBooksAdapter.add(bookmark);
		}
	}

	private void gotoBookmark(Bookmark bookmark) {
		bookmark.markAsAccessed();
		myCollection.saveBookmark(bookmark);
		final Book book = myCollection.getBookById(bookmark.getBookId());
		if (book != null) {
			CoreReadActivity.openBookActivity(this, book, bookmark);
		} else {
			UIUtil.showErrorMessage(this, "cannotOpenBook");
		}
	}

	private final class BookmarksAdapter extends BaseAdapter implements
			AdapterView.OnItemClickListener, View.OnCreateContextMenuListener {
		private final List<Bookmark> myBookmarks = Collections
				.synchronizedList(new LinkedList<Bookmark>());
		private final boolean myShowAddBookmarkItem;

		BookmarksAdapter(ListView listView, boolean showAddBookmarkItem) {
			myShowAddBookmarkItem = showAddBookmarkItem;
			listView.setAdapter(this);
			listView.setOnItemClickListener(this);
			listView.setOnCreateContextMenuListener(this);
		}

		public List<Bookmark> bookmarks() {
			return Collections.unmodifiableList(myBookmarks);
		}

		public void addAll(final List<Bookmark> bookmarks) {
			runOnUiThread(new Runnable() {
				public void run() {
					synchronized (myBookmarks) {
						for (Bookmark b : bookmarks) {
							final int position = Collections.binarySearch(
									myBookmarks, b, myComparator);
							if (position < 0) {
								myBookmarks.add(-position - 1, b);
							}
						}
					}
					notifyDataSetChanged();
				}
			});
		}

		public void add(final Bookmark b) {
			runOnUiThread(new Runnable() {
				public void run() {
					synchronized (myBookmarks) {
						final int position = Collections.binarySearch(
								myBookmarks, b, myComparator);
						if (position < 0) {
							myBookmarks.add(-position - 1, b);
						}
					}
					notifyDataSetChanged();
				}
			});
		}

		public void remove(final Bookmark b) {
			runOnUiThread(new Runnable() {
				public void run() {
					myBookmarks.remove(b);
					notifyDataSetChanged();
				}
			});
		}

		public void clear() {
			runOnUiThread(new Runnable() {
				public void run() {
					myBookmarks.clear();
					notifyDataSetChanged();
				}
			});
		}

		public void onCreateContextMenu(ContextMenu menu, View view,
				ContextMenu.ContextMenuInfo menuInfo) {
			final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
			if (getItem(position) != null) {
				menu.setHeaderTitle(getItem(position).getText());
				menu.add(0, OPEN_ITEM_ID, 0, myResource.getResource("open")
						.getValue());
				// menu.add(0, EDIT_ITEM_ID, 0,
				// myResource.getResource("edit").getValue());
				menu.add(0, DELETE_ITEM_ID, 0, myResource.getResource("delete")
						.getValue());
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final View view = (convertView != null) ? convertView
					: LayoutInflater.from(parent.getContext()).inflate(
							R.layout.bookmark_item, parent, false);
			final ImageView imageView = (ImageView) view
					.findViewById(R.id.bookmark_item_icon);
			final TextView textView = (TextView) view
					.findViewById(R.id.bookmark_item_text);
			final TextView bookTitleView = (TextView) view
					.findViewById(R.id.bookmark_item_booktitle);

			final Bookmark bookmark = getItem(position);
			if (bookmark == null) {
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageResource(R.drawable.bookmark_plus);
				textView.setText(myResource.getResource("new").getValue());
				bookTitleView.setVisibility(View.GONE);
			} else {
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageResource(R.drawable.bookmark_icon);
				textView.setText(bookmark.getText());
				if (myShowAddBookmarkItem) {
					bookTitleView.setVisibility(View.GONE);
				} else {
					bookTitleView.setVisibility(View.VISIBLE);
					bookTitleView.setText(bookmark.getBookTitle());
				}
			}
			return view;
		}

		@Override
		public final boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public final boolean isEnabled(int position) {
			return true;
		}

		@Override
		public final long getItemId(int position) {
			return position;
		}

		@Override
		public final Bookmark getItem(int position) {
			if (myShowAddBookmarkItem) {
				--position;
			}
			return (position >= 0) ? myBookmarks.get(position) : null;
		}

		@Override
		public final int getCount() {
			return myShowAddBookmarkItem ? myBookmarks.size() + 1 : myBookmarks
					.size();
		}

		public final void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			final Bookmark bookmark = getItem(position);
			if (bookmark != null) {
				gotoBookmark(bookmark);
			} else {
				addBookmark();
			}
		}
	}
}
