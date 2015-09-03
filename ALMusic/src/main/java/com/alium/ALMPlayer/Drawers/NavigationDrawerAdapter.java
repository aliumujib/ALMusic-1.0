/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alium.ALMPlayer.Drawers;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alium.ALMPlayer.R;
import com.alium.ALMPlayer.Helpers.TypefaceHelper;
import com.alium.ALMPlayer.Helpers.UIElementsHelper;
import com.alium.ALMPlayer.MainActivity.MainActivity;
import com.alium.ALMPlayer.Utils.Common;

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {

	private Context mContext;
	private SharedPreferences sharedPreferences;
	private ArrayList<NavigationDrawerItem> mTitlesList;

    public NavigationDrawerAdapter(Context context, ArrayList<NavigationDrawerItem> titlesList) {
    	super(context, R.layout.sliding_menu_browsers_layout, titlesList);
    	mContext = context;
    	mTitlesList = titlesList;
    	sharedPreferences = mContext.getSharedPreferences("com.jams.music.player", Context.MODE_PRIVATE);
    }



    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	
    	SongsListViewHolder holder = null;
		if (convertView == null) {	
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sliding_menu_browsers_layout, parent, false);
			holder = new SongsListViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.nav_drawer_item_title);
			holder.icon = (ImageView) convertView.findViewById(R.id.imgIcon);
			convertView.setTag(holder);
		} else {
		    holder = (SongsListViewHolder) convertView.getTag();
		}

		try {
			NavigationDrawerItem currentSelection = mTitlesList.get(position);
			holder.icon.setImageResource(currentSelection.getIcon());
			holder.title.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
			holder.title.setText(currentSelection.getTitle());
			holder.title.setTextColor(UIElementsHelper.getThemeBasedTextColor(mContext));
		}
		catch (NullPointerException e)
		{
			Log.d("WTF", ""+mTitlesList.get(position).getTitle());
		}
		//Highlight the current browser.
		int[] colors = UIElementsHelper.getQuickScrollColors(mContext);
		if (MainActivity.mCurrentFragmentId==Common.ARTISTS_FRAGMENT && 
			mTitlesList.get(position).equals(mContext.getResources().getString(R.string.artists))) {
			holder.title.setTextColor(colors[0]);
		} else if (MainActivity.mCurrentFragmentId==Common.ALBUMS_FRAGMENT &&
				   mTitlesList.get(position).equals(mContext.getResources().getString(R.string.albums))) {
			holder.title.setTextColor(colors[0]);
		} else if (MainActivity.mCurrentFragmentId==Common.SONGS_FRAGMENT &&
				   mTitlesList.get(position).equals(mContext.getResources().getString(R.string.songs))) {
			holder.title.setTextColor(colors[0]);
		} else if (MainActivity.mCurrentFragmentId==Common.PLAYLISTS_FRAGMENT &&
				   mTitlesList.get(position).equals(mContext.getResources().getString(R.string.playlists))) {
			holder.title.setTextColor(colors[0]);
		} else if (MainActivity.mCurrentFragmentId==Common.GENRES_FRAGMENT &&
				   mTitlesList.get(position).equals(mContext.getResources().getString(R.string.genres))) {
			holder.title.setTextColor(colors[0]);
		} else if (MainActivity.mCurrentFragmentId==Common.FOLDERS_FRAGMENT &&
				   mTitlesList.get(position).equals(mContext.getResources().getString(R.string.folders))) {
			holder.title.setTextColor(colors[0]);
		}
		
		return convertView;

	}
    
	static class SongsListViewHolder {
	    public TextView title;
		public ImageView icon;
	}
   
}
