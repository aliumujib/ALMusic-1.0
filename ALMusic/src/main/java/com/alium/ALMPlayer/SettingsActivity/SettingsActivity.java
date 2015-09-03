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
package com.alium.ALMPlayer.SettingsActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alium.ALMPlayer.Helpers.UIElementsHelper;
import com.alium.ALMPlayer.R;
import com.alium.ALMPlayer.Utils.Common;

import java.util.List;


/**
 * @author Saravan Pantham
 */
public class SettingsActivity extends PreferenceActivity {

    private Context mContext;
    private SettingsActivity mActivity;
    private Common mApp;

    private SettingsActivity____ mSettingsActivity;

    //Preference Manager.
    private SharedPreferences sharedPreferences;
    private PreferenceManager preferenceManager;

    //Blacklist Preferences.
    private Preference blacklistManagerPreference;
    private Preference unblacklistAllPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Context.
        mContext = this.getApplicationContext();
        mActivity = this;
        mApp = (Common) mContext;

        if(mApp.getCurrentTheme()==Common.DARK_THEME){
        setTheme(R.style.SettingsThemeDark);}
        else{
            setTheme(R.style.SettingsThemeLight);
        }

        super.onCreate(savedInstanceState);

        //Set the ActionBar background and text color.
        //getActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext)); ccommenting this line fixed the
        //black notificationbar in settings :)
        getActionBar().setTitle(R.string.settings);
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarText = (TextView) findViewById(titleId);
        actionBarText.setTextColor(0xFFFFFFFF);

        sharedPreferences = this.getSharedPreferences("com.jams.music.player", Context.MODE_PRIVATE);
        preferenceManager = this.getPreferenceManager();

        //Set the ActionBar background and text color.
        applyKitKatTranslucency();
        applyLollipopActionBar();
        applyLowerApis();

    }

    /**
     * Applies KitKat specific translucency.
     */
    public void applyKitKatTranslucency() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

            //Calculate ActionBar and navigation bar height.
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }

            ((View) this.getListView().getParent()).setPadding(0, actionBarHeight + mApp.getStatusBarHeight(mContext),
                    0, 0);

            this.getListView().setPadding(0, 0, 0, mApp.getNavigationBarHeight(mContext));
            this.getListView().setClipToPadding(false);

            //Set the window color.
            getActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
            getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
            getListView().setBackgroundDrawable(UIElementsHelper.getBackgroundGradientDrawable(mContext));
        }

    }

    public void applyLowerApis() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

            //Calculate ActionBar and navigation bar height.
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }

            ((View) this.getListView().getParent()).setPadding(0, actionBarHeight + mApp.getStatusBarHeight(mContext),
                    0, 0);

            this.getListView().setPadding(0, 0, 0, mApp.getNavigationBarHeight(mContext));
            this.getListView().setClipToPadding(false);

            //Set the window color.
            getActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
            getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
            getListView().setBackgroundDrawable(UIElementsHelper.getBackgroundGradientDrawable(mContext));
        }

    }

    public void applyLollipopActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //Calculate ActionBar and navigation bar height.
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }
            this.getListView().setPadding(0, 0, 0, mApp.getNavigationBarHeight(mContext));
            this.getListView().setClipToPadding(false);

            //Set the window color.
            getListView().setBackground(UIElementsHelper.getBackgroundGradientDrawable(mContext));
            getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
            getActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UIElementsHelper.getGeneralStatusBarBackground((mContext)).getColor());
        }

    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        if (mApp.getCurrentTheme() == Common.DARK_THEME) {
            loadHeadersFromResource(R.xml.settings_headers_dark, target);
        } else {
            loadHeadersFromResource(R.xml.settings_headers, target);
        }
        applyKitKatTranslucency();
        applyLollipopActionBar();
        applyLowerApis();

    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;

    }

    @Override
    public void onResume() {
        super.onResume();
        applyKitKatTranslucency();
        applyLollipopActionBar();
        applyLowerApis();
    }

}
