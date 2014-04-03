/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
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

import org.geometerplus.fbreader.book.SerializerUtil;
import org.geometerplus.fbreader.fbreader.FBReaderApp;

import android.content.Intent;

import com.yamin.reader.activity.CoreReadActivity;

public  class ShowBookmarksAction extends FBAndroidAction {
	public ShowBookmarksAction(CoreReadActivity baseActivity, FBReaderApp fbreader) {
		super(baseActivity, fbreader);
	}

	@Override
	public boolean isVisible() {
		return Reader.Model != null;
	}

	@Override
	protected void run(Object ... params) {
		/*
		 * Modyfy by yamin.cao
		 * Action:Remove the externalIntent
		 */
		//final Intent externalIntent =
			//new Intent("android.fbreader.action.EXTERNAL_BOOKMARKS");
		final Intent internalIntent =
			new Intent(BaseActivity.getApplicationContext(), BookmarksActivity.class);
		/*
		if (PackageUtil.canBeStarted(BaseActivity, externalIntent, true)) {
			try {
				startBookmarksActivity(externalIntent);
			} catch (ActivityNotFoundException e) {
				startBookmarksActivity(internalIntent);
			}
		}
		 else {
			*/
			startBookmarksActivity(internalIntent);
		//}
	}

	private void startBookmarksActivity(Intent intent) {
		intent.putExtra(
				CoreReadActivity.BOOK_KEY, SerializerUtil.serialize(Reader.Model.Book)
		);
		intent.putExtra(
				CoreReadActivity.BOOKMARK_KEY, SerializerUtil.serialize(Reader.createBookmark(20, true))
		);
		OrientationUtil.startActivity(BaseActivity, intent);
	}
}
