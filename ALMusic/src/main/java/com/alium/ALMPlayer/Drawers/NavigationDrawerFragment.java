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
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alium.ALMPlayer.BrowserSubGridActivity.BrowserSubGridActivity;
import com.alium.ALMPlayer.BrowserSubGridActivity.BrowserSubGridAdapter;
import com.alium.ALMPlayer.BrowserSubListActivity.BrowserSubListActivity;
import com.alium.ALMPlayer.Helpers.UIElementsHelper;
import com.alium.ALMPlayer.R;
import com.alium.ALMPlayer.Helpers.TypefaceHelper;
import com.alium.ALMPlayer.MainActivity.MainActivity;
import com.alium.ALMPlayer.SettingsActivity.SettingsActivity;
import com.alium.ALMPlayer.SettingsActivity.SettingsActivity____;
import com.alium.ALMPlayer.Utils.Common;

public class NavigationDrawerFragment extends Fragment {

    private Context mContext;
    private Common mApp;

    private RelativeLayout mLibraryPickerLayout;
    private RelativeLayout mRootNavLayout;
    private TextView mLibraryPickerHeaderText;
    private Spinner mLibraryPickerSpinner;
    private ListView browsersListView;
    private ImageView mBannerImage;

    private Cursor cursor;
    private int mCurrentLibraryPosition;
    private NavigationDrawerLibrariesAdapter mLibrariesAdapter;
    private NavigationDrawerAdapter mBrowsersAdapter;
    private Handler mHandler;
    ArrayList<NavigationDrawerItem> itemsList = new ArrayList<>();

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        mApp = (Common) mContext.getApplicationContext();
        mHandler = new Handler();

        View rootView = inflater.inflate(R.layout.navigation_drawer_layout, null);
        rootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(mContext));

        browsersListView = (ListView) rootView.findViewById(R.id.browsers_list_view);
        mLibraryPickerLayout = (RelativeLayout) rootView.findViewById(R.id.library_picker_layout);
        mRootNavLayout = (RelativeLayout) rootView.findViewById(R.id.root_nav_drawer_layout);
        mLibraryPickerSpinner = (Spinner) rootView.findViewById(R.id.library_picker_spinner);
        mLibraryPickerHeaderText = (TextView) rootView.findViewById(R.id.library_picker_header_text);
        mLibraryPickerHeaderText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

        //Apply the Browser ListView's adapter.
        //List<String> titles = Arrays.asList(getActivity().getResources().getStringArray(R.array.sliding_menu_array));
        try {

            if(mApp.getCurrentTheme()==Common.DARK_THEME)
            {
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_person_white_18dp, "Artists"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_album_white_18dp, "Albums"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_audiotrack_white_18dp, "Songs"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_queue_music_white_18dp, "Playlists"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_library_add_white_18dp, "Genres"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_folder_white_18dp, "Folders"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_settings_white_18dp, "Settings"));
                mLibraryPickerLayout.setBackground(getResources().getDrawable(R.drawable.banner_dark));
            }

            else
            {
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_person_black_18dp, "Artists"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_album_black_18dp, "Albums"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_audiotrack_black_18dp, "Songs"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_queue_music_black_18dp, "Playlists"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_library_add_black_18dp, "Genres"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_folder_black_18dp, "Folders"));
                itemsList.add(new NavigationDrawerItem(R.drawable.ic_settings_black_18dp, "Settings"));
                mLibraryPickerLayout.setBackground(getResources().getDrawable(R.drawable.banner_light));
            }
            mBrowsersAdapter = new NavigationDrawerAdapter(getActivity(), new ArrayList<NavigationDrawerItem>(itemsList));
            browsersListView.setAdapter(mBrowsersAdapter);
            browsersListView.setOnItemClickListener(browsersClickListener);

            if(getActivity() instanceof BrowserSubGridActivity || getActivity() instanceof BrowserSubListActivity)
            {
                setListViewHeightBasedOnChildren(browsersListView);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.v("WE HAVE A PROBLEM", "WOW "+itemsList.get(1).getTitle());
        }
        //Apply the Libraries ListView's adapter.
        cursor = mApp.getDBAccessHelper().getAllUniqueLibraries();
        mLibrariesAdapter = new NavigationDrawerLibrariesAdapter(getActivity(), cursor);
        mLibraryPickerSpinner.setAdapter(mLibrariesAdapter);
        mLibraryPickerSpinner.setSelection(mApp.getCurrentLibraryIndex());
        mLibraryPickerSpinner.setOnItemSelectedListener(librariesItemSelectedListener);

        browsersListView.setDividerHeight(0);

        //KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int navBarHeight = Common.getNavigationBarHeight(mContext);
            if (browsersListView != null) {
                browsersListView.setPadding(0, 0, 0, navBarHeight);
                browsersListView.setClipToPadding(false);
            }

        }

        return rootView;
    }

    private AdapterView.OnItemSelectedListener librariesItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (mApp.getCurrentLibraryIndex() == position)
                return;

            mApp.getSharedPreferences().edit().putString(Common.CURRENT_LIBRARY,
                    (String) view.getTag(R.string.library_name)).commit();

            mApp.getSharedPreferences().edit().putInt(Common.CURRENT_LIBRARY_POSITION, position).commit();

            //Update the fragment.
            ((MainActivity) getActivity()).loadFragment(null);

            //Reset the ActionBar after 500ms.
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    getActivity().invalidateOptionsMenu();

                }

            }, 500);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

    private OnItemClickListener browsersClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long dbID) {
            switch (position) {
                case 0:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.ARTISTS_FRAGMENT);
                    break;
                case 1:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.ALBUMS_FRAGMENT);
                    break;
                case 2:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.SONGS_FRAGMENT);
                    break;
                case 3:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.PLAYLISTS_FRAGMENT);
                    break;
                case 4:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.GENRES_FRAGMENT);
                    break;
                case 5:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.FOLDERS_FRAGMENT);
                    break;
                case 6:
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                    break;
            }

            //Update the adapter to reflect the new fragment.
            //List<String> titles = Arrays.asList(getActivity().getResources().getStringArray(R.array.sliding_menu_array));

            mBrowsersAdapter = new NavigationDrawerAdapter(getActivity(), new ArrayList<NavigationDrawerItem>(itemsList));
            browsersListView.setAdapter(mBrowsersAdapter);

            //Update the fragment.
            ((MainActivity) getActivity()).loadFragment(null);

            //Reset the ActionBar after 500ms.
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    getActivity().invalidateOptionsMenu();

                }

            }, 500);

        }

    };

    /**
     * Clips ListViews to fit within the drawer's boundaries.
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

    }

}
